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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.rhq.metrics.netty.collectd.event.DataType.ABSOLUTE;
import static org.rhq.metrics.netty.collectd.event.DataType.GAUGE;
import static org.rhq.metrics.netty.collectd.event.TimeResolution.HIGH_RES;
import static org.rhq.metrics.netty.collectd.packet.PartType.HOST;
import static org.rhq.metrics.netty.collectd.packet.PartType.INSTANCE;
import static org.rhq.metrics.netty.collectd.packet.PartType.INTERVAL_HIGH_RESOLUTION;
import static org.rhq.metrics.netty.collectd.packet.PartType.PLUGIN;
import static org.rhq.metrics.netty.collectd.packet.PartType.PLUGIN_INSTANCE;
import static org.rhq.metrics.netty.collectd.packet.PartType.TIME_HIGH_RESOLUTION;
import static org.rhq.metrics.netty.collectd.packet.PartType.TYPE;
import static org.rhq.metrics.netty.collectd.packet.PartType.VALUES;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.netty.channel.embedded.EmbeddedChannel;

import org.junit.Test;

import org.rhq.metrics.netty.collectd.packet.CollectdPacket;
import org.rhq.metrics.netty.collectd.packet.NumericPart;
import org.rhq.metrics.netty.collectd.packet.Part;
import org.rhq.metrics.netty.collectd.packet.PartType;
import org.rhq.metrics.netty.collectd.packet.StringPart;
import org.rhq.metrics.netty.collectd.packet.ValuePart;
import org.rhq.metrics.netty.collectd.packet.Values;

public class CollectdEventsDecoderTest {

    @Test
    public void handlerShouldNotOutputEventsWhenNoValuePartIsInCollectdPacket() throws Exception {
        List<Part> parts = new ArrayList<Part>();
        for (PartType partType : PartType.values()) {
            switch (partType) {
            case HOST:
            case PLUGIN:
            case PLUGIN_INSTANCE:
            case TYPE:
            case INSTANCE:
                parts.add(new StringPart(partType, "marseille"));
                break;
            case TIME:
            case TIME_HIGH_RESOLUTION:
            case INTERVAL:
            case INTERVAL_HIGH_RESOLUTION:
                parts.add(new NumericPart(partType, 13l));
                break;
            case VALUES:
                // Don't add such part;
                break;
            default:
                fail("Unknown part type: " + partType);
            }
        }
        CollectdPacket packet = new CollectdPacket(parts.toArray(new Part[parts.size()]));

        EmbeddedChannel channel = new EmbeddedChannel(new CollectdEventsDecoder());
        assertFalse("Expected no event", channel.writeInbound(packet));
    }

    @Test
    public void handlerShouldDecodeSimplePacket() throws Exception {
        List<Part> parts = new ArrayList<Part>();
        parts.add(new StringPart(HOST, HOST.name()));
        parts.add(new StringPart(PLUGIN, PLUGIN.name()));
        parts.add(new StringPart(PLUGIN_INSTANCE, PLUGIN_INSTANCE.name()));
        parts.add(new StringPart(TYPE, TYPE.name()));
        parts.add(new StringPart(INSTANCE, INSTANCE.name()));
        parts.add(new NumericPart(TIME_HIGH_RESOLUTION, (long) TIME_HIGH_RESOLUTION.ordinal()));
        parts.add(new NumericPart(INTERVAL_HIGH_RESOLUTION, (long) INTERVAL_HIGH_RESOLUTION.ordinal()));
        DataType[] dataTypes = new DataType[] { GAUGE, ABSOLUTE };
        Number[] data = new Number[] { 13.13d, BigInteger.valueOf(13) };
        Values values = new Values(dataTypes, data);
        ValuePart valuePart = new ValuePart(VALUES, values);
        parts.add(valuePart);
        parts.add(valuePart); // add it twice
        CollectdPacket packet = new CollectdPacket(parts.toArray(new Part[parts.size()]));

        EmbeddedChannel channel = new EmbeddedChannel(new CollectdEventsDecoder());
        assertTrue("Expected an event", channel.writeInbound(packet));

        Object output = channel.readInbound();
        assertEquals(ValueListEvent.class, output.getClass());
        ValueListEvent event = (ValueListEvent) output;
        checkValueListEvent(event);

        // A second event with same values should be emitted
        output = channel.readInbound();
        assertEquals(ValueListEvent.class, output.getClass());
        event = (ValueListEvent) output;
        checkValueListEvent(event);

        assertNull("Expected no more than two instances of Event", channel.readInbound());
    }

    private void checkValueListEvent(ValueListEvent event) {
        assertEquals(HOST.name(), event.getHost());
        assertEquals(PLUGIN.name(), event.getPluginName());
        assertEquals(PLUGIN_INSTANCE.name(), event.getPluginInstance());
        assertEquals(TYPE.name(), event.getTypeName());
        assertEquals(INSTANCE.name(), event.getTypeInstance());
        TimeSpan timestamp = event.getTimestamp();
        assertEquals(TIME_HIGH_RESOLUTION.ordinal(), timestamp.getValue());
        assertEquals(HIGH_RES, timestamp.getResolution());
        TimeSpan interval = event.getInterval();
        assertEquals(INTERVAL_HIGH_RESOLUTION.ordinal(), interval.getValue());
        assertEquals(HIGH_RES, interval.getResolution());
        Number[] values = event.getValues();
        assertEquals("Expected two values", 2, values.length);
        assertEquals(Double.class, values[0].getClass());
        assertEquals(13.13d, values[0]);
        assertEquals(BigInteger.class, values[1].getClass());
        assertEquals(BigInteger.valueOf(13), values[1]);
    }

    @Test
    public void handlerShouldNotUseNullWhenNoTypeInstancePartHasBeenSent() throws Exception {
        List<Part> parts = new ArrayList<Part>();
        parts.add(new StringPart(HOST, HOST.name()));
        parts.add(new StringPart(PLUGIN, PLUGIN.name()));
        parts.add(new StringPart(PLUGIN_INSTANCE, PLUGIN_INSTANCE.name()));
        parts.add(new StringPart(TYPE, TYPE.name()));
        parts.add(new NumericPart(TIME_HIGH_RESOLUTION, (long) TIME_HIGH_RESOLUTION.ordinal()));
        parts.add(new NumericPart(INTERVAL_HIGH_RESOLUTION, (long) INTERVAL_HIGH_RESOLUTION.ordinal()));
        DataType[] dataTypes = new DataType[] { GAUGE, ABSOLUTE };
        Number[] data = new Number[] { 13.13d, BigInteger.valueOf(13) };
        Values values = new Values(dataTypes, data);
        ValuePart valuePart = new ValuePart(VALUES, values);
        parts.add(valuePart);
        CollectdPacket packet = new CollectdPacket(parts.toArray(new Part[parts.size()]));

        EmbeddedChannel channel = new EmbeddedChannel(new CollectdEventsDecoder());
        assertTrue("Expected an event", channel.writeInbound(packet));

        Object output = channel.readInbound();
        assertEquals(ValueListEvent.class, output.getClass());
        ValueListEvent event = (ValueListEvent) output;
        assertNotNull(event.getTypeInstance());

        assertNull("Expected exactly one instance of Event", channel.readInbound());
    }
}
