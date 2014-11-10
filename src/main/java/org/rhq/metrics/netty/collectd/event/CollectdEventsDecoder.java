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

package org.rhq.metrics.netty.collectd.event;

import static io.netty.channel.ChannelHandler.Sharable;
import static org.rhq.metrics.netty.collectd.event.TimeResolution.HIGH_RES;
import static org.rhq.metrics.netty.collectd.event.TimeResolution.SECONDS;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import org.rhq.metrics.netty.collectd.packet.CollectdPacket;
import org.rhq.metrics.netty.collectd.packet.NumericPart;
import org.rhq.metrics.netty.collectd.packet.Part;
import org.rhq.metrics.netty.collectd.packet.PartType;
import org.rhq.metrics.netty.collectd.packet.StringPart;
import org.rhq.metrics.netty.collectd.packet.Values;
import org.rhq.metrics.netty.collectd.packet.ValuePart;

/**
 * @author Thomas Segismont
 */
@Sharable
public final class CollectdEventsDecoder extends MessageToMessageDecoder<CollectdPacket> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CollectdEventsDecoder.class);

    private static final String EMPTY_STRING_VALUE = "";

    @Override
    protected void decode(ChannelHandlerContext context, CollectdPacket packet, List<Object> out) throws Exception {
        long start = System.currentTimeMillis();
        String host = EMPTY_STRING_VALUE, pluginName = EMPTY_STRING_VALUE, pluginInstance = EMPTY_STRING_VALUE;
        String typeName = EMPTY_STRING_VALUE, typeInstance = EMPTY_STRING_VALUE;
        TimeSpan timestamp = null, interval = null;
        List<Event> events = new ArrayList<Event>(50);
        for (Part part : packet.getParts()) {
            PartType partType = part.getPartType();
            switch (partType) {
            case HOST:
                host = getString(part);
                break;
            case PLUGIN:
                pluginName = getString(part);
                break;
            case PLUGIN_INSTANCE:
                pluginInstance = getString(part);
                break;
            case TYPE:
                typeName = getString(part);
                break;
            case INSTANCE:
                typeInstance = getString(part);
                break;
            case TIME:
                timestamp = new TimeSpan(getLong(part), SECONDS);
                break;
            case TIME_HIGH_RESOLUTION:
                timestamp = new TimeSpan(getLong(part), HIGH_RES);
                break;
            case INTERVAL:
                interval = new TimeSpan(getLong(part), SECONDS);
                break;
            case INTERVAL_HIGH_RESOLUTION:
                interval = new TimeSpan(getLong(part), HIGH_RES);
                break;
            case VALUES:
                Number[] values = getValues(part).getData();
                ValueListEvent event = new ValueListEvent(host, timestamp, pluginName, pluginInstance, typeName,
                    typeInstance, values, interval);
                logger.trace("Decoded ValueListEvent: {}", event);
                events.add(event);
                break;
            default:
                logger.debug("Skipping unknown part type: {}", partType);
            }
        }

        if (logger.isTraceEnabled()) {
            long stop = System.currentTimeMillis();
            logger.trace("Decoded events in {} ms", stop - start);
        }

        out.addAll(events);
    }

    private String getString(Part part) {
        return ((StringPart) part).getValue();
    }

    private Long getLong(Part part) {
        return ((NumericPart) part).getValue();
    }

    private Values getValues(Part part) {
        return ((ValuePart) part).getValue();
    }
}
