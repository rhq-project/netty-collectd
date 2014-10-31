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

package org.rhq.metrics.netty.collectd.events;

import static org.rhq.metrics.netty.collectd.events.TimeResolution.HIGH_RES;
import static org.rhq.metrics.netty.collectd.events.TimeResolution.SECONDS;
import static org.rhq.metrics.netty.collectd.parts.PartType.INTERVAL;
import static org.rhq.metrics.netty.collectd.parts.PartType.INTERVAL_HIGH_RESOLUTION;
import static org.rhq.metrics.netty.collectd.parts.PartType.TIME;
import static org.rhq.metrics.netty.collectd.parts.PartType.TIME_HIGH_RESOLUTION;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import org.rhq.metrics.netty.collectd.parts.NumericPart;
import org.rhq.metrics.netty.collectd.parts.Part;
import org.rhq.metrics.netty.collectd.parts.PartType;
import org.rhq.metrics.netty.collectd.parts.StringPart;
import org.rhq.metrics.netty.collectd.parts.ValuePart;
import org.rhq.metrics.netty.collectd.values.Values;

/**
 * @author Thomas Segismont
 */
public class CollectdEventsDecoder extends MessageToMessageDecoder<Part> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CollectdEventsDecoder.class);

    private String host;
    private TimeSpan timestamp;
    private TimeSpan interval;
    private String pluginName;
    private String pluginInstance;
    private String type;
    private String instance;

    @Override
    protected void decode(ChannelHandlerContext ctx, Part msg, List<Object> out) throws Exception {
        PartType partType = msg.getPartType();
        switch (partType) {
        case HOST:
            StringPart hostPart = (StringPart) msg;
            host = hostPart.getValue();
            break;
        case PLUGIN:
            StringPart pluginPart = (StringPart) msg;
            pluginName = pluginPart.getValue();
            break;
        case PLUGIN_INSTANCE:
            StringPart pluginInstancePart = (StringPart) msg;
            pluginInstance = pluginInstancePart.getValue();
            break;
        case TYPE:
            StringPart typePart = (StringPart) msg;
            type = typePart.getValue();
            break;
        case INSTANCE:
            StringPart instancePart = (StringPart) msg;
            instance = instancePart.getValue();
            break;
        case VALUES:
            ValuePart valuePart = (ValuePart) msg;
            Values values = valuePart.getValue();
            PluginData pluginData = new PluginData(pluginName, pluginInstance);
            TypeData typeData = new TypeData(type, instance);
            out.add(new Event(host, timestamp, pluginData, typeData, values, interval));
            break;
        case TIME:
        case TIME_HIGH_RESOLUTION:
            NumericPart timePart = (NumericPart) msg;
            timestamp = new TimeSpan(timePart.getValue(), getResolutionFromPartType(partType));
            break;
        case INTERVAL:
        case INTERVAL_HIGH_RESOLUTION:
            NumericPart intervalPart = (NumericPart) msg;
            interval = new TimeSpan(intervalPart.getValue(), getResolutionFromPartType(partType));
            break;
        default:
            logger.debug("Skipping unknown part type: {}", partType);
        }
    }

    private TimeResolution getResolutionFromPartType(PartType partType) {
        if (partType == TIME_HIGH_RESOLUTION || partType == INTERVAL_HIGH_RESOLUTION) {
            return HIGH_RES;
        }
        if (partType == TIME || partType == INTERVAL) {
            return SECONDS;
        }
        throw new IllegalArgumentException(partType.name());
    }
}
