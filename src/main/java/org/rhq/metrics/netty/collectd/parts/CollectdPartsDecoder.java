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

package org.rhq.metrics.netty.collectd.parts;

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

import org.rhq.metrics.netty.collectd.values.DoubleSample;
import org.rhq.metrics.netty.collectd.values.IntegerSample;
import org.rhq.metrics.netty.collectd.values.LongSample;
import org.rhq.metrics.netty.collectd.values.Sample;
import org.rhq.metrics.netty.collectd.values.SampleType;
import org.rhq.metrics.netty.collectd.values.Values;

/**
 * @author Thomas Segismont
 */
@Sharable
public class CollectdPartsDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CollectdPartsDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        long start = System.currentTimeMillis();
        ByteBuf content = msg.content();
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
                part = new ValuePart(partType, readValuePartContent(content, valueLength));
                break;
            default:
                part = null;
                content.skipBytes(valueLength);
            }
            //noinspection ConstantConditions
            if (part != null) {
                logger.trace("Decoded new part: {}", part);
                out.add(part);
            }
        }
        long stop = System.currentTimeMillis();
        logger.debug("Decoded datagram in {} ms", stop - start);
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
        int samplesCount = content.readUnsignedShort();
        SampleType[] sampleTypes = new SampleType[samplesCount];
        for (int i = 0; i < samplesCount; i++) {
            byte sampleTypeId = content.readByte();
            sampleTypes[i] = SampleType.findById(sampleTypeId);
        }
        List<Sample> samples = new ArrayList<Sample>(samplesCount);
        for (int i = 0; i < samplesCount; i++) {
            SampleType sampleType = sampleTypes[i];
            // Read the 64 bits field
            long value = content.readLong();
            // Now adjust convert to a real value
            switch (sampleType) {
            case COUNTER:
            case ABSOLUTE:
                samples.add(new LongSample(sampleType, value));
                break;
            case DERIVE:
                samples.add(new IntegerSample(sampleType, (int) value));
                break;
            case GAUGE:
                samples.add(new DoubleSample(sampleType, Double.longBitsToDouble(ByteBufUtil.swapLong(value))));
                break;
            default:
                logger.debug("Skipping unknown sample type: {}", sampleType);
            }
        }
        // Skip any additionnal bytes
        content.skipBytes(length - 2 /* samplesCount */- 9 /* typeId(1) + data(8) */* samplesCount);
        return new Values(samples);
    }
}
