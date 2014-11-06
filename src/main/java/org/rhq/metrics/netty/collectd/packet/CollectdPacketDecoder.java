/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rhq.metrics.netty.collectd.packet;

import static io.netty.channel.ChannelHandler.Sharable;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import org.rhq.metrics.netty.collectd.event.DataType;

/**
 * @author Thomas Segismont
 */
@Sharable
public final class CollectdPacketDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CollectdPacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) throws Exception {
        long start = System.currentTimeMillis();
        ByteBuf content = packet.content();
        List<Part> parts = new ArrayList<Part>(100);
        for (;;) {
            if (!hasReadableBytes(content, 4)) {
                break;
            }
            short partTypeId = content.readShort();
            PartType partType = PartType.findById(partTypeId);
            int partLength = content.readUnsignedShort();
            int valueLength = partLength - 4;
            if (!hasReadableBytes(content, valueLength)) {
                break;
            }
            if (partType == null) {
                content.skipBytes(valueLength);
                continue;
            }
            Part part;
            switch (partType) {
            case HOST:
            case PLUGIN:
            case PLUGIN_INSTANCE:
            case TYPE:
            case INSTANCE:
                part = new StringPart(partType, readStringPartContent(content, valueLength));
                break;
            case TIME:
            case TIME_HIGH_RESOLUTION:
            case INTERVAL:
            case INTERVAL_HIGH_RESOLUTION:
                part = new NumericPart(partType, readNumericPartContent(content));
                break;
            case VALUES:
                part = new ValuesPart(partType, readValuePartContent(content, valueLength));
                break;
            default:
                part = null;
                content.skipBytes(valueLength);
            }
            //noinspection ConstantConditions
            if (part != null) {
                logger.trace("Decoded part: {}", part);
                parts.add(part);
            }
        }

        CollectdPacket collectdPacket = new CollectdPacket(parts.toArray(new Part[parts.size()]));

        if (logger.isDebugEnabled()) {
            long stop = System.currentTimeMillis();
            logger.debug("Decoded datagram {} in {} ms", collectdPacket, stop - start);
        }

        out.add(collectdPacket);
    }

    private boolean hasReadableBytes(ByteBuf content, int count) {
        return content.readableBytes() >= count;
    }

    private String readStringPartContent(ByteBuf content, int length) {
        String string = content.toString(content.readerIndex(), length - 1 /* collectd strings are \0 terminated */,
            CharsetUtil.US_ASCII);
        content.skipBytes(length); // the previous call does not move the readerIndex
        return string;
    }

    private long readNumericPartContent(ByteBuf content) {
        return content.readLong();
    }

    private Values readValuePartContent(ByteBuf content, int length) {
        int total = content.readUnsignedShort();
        DataType[] dataTypes = new DataType[total];
        for (int i = 0; i < total; i++) {
            byte sampleTypeId = content.readByte();
            dataTypes[i] = DataType.findById(sampleTypeId);
        }
        Number[] data = new Number[total];
        for (int i = 0; i < total; i++) {
            DataType dataType = dataTypes[i];
            // Read the 64 bits field
            long value = content.readLong();
            // Now convert to the approriate type
            switch (dataType) {
            case COUNTER:
            case ABSOLUTE:
                data[i] = value;
                break;
            case DERIVE:
                data[i] = (int) value;
                break;
            case GAUGE:
                data[i] = Double.longBitsToDouble(ByteBufUtil.swapLong(value));
                break;
            default:
                logger.debug("Skipping unknown data type: {}", dataType);
            }
        }
        // Skip any additionnal bytes
        content.skipBytes(length - 2 /* total */- 9 /* typeId(1) + data(8) */* total);
        return new Values(dataTypes, data);
    }
}
