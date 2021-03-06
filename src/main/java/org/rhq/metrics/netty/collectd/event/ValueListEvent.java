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

import static org.rhq.metrics.netty.collectd.util.Assert.assertNotNull;

import java.util.Arrays;

/**
 * Event emitted by the {@link CollectdEventsDecoder} and signaling a decoded collectd
 * <a href="https://collectd.org/wiki/index.php/Value_list">Value List</a>.
 *
 * @author Thomas Segismont
 */
public final class ValueListEvent extends Event {
    private final Number[] values;
    private final TimeSpan interval;

    /**
     * Creates a new Value List event.
     *
     * @param host hostname of the machine where the values were collected
     * @param timestamp when they were collected
     * @param pluginName the corresponding collectd plugin, e.g. "cpu"
     * @param pluginInstance the corresponding collectd plugin instance, e.g. "1"
     * @param typeName the corresponding collectd type, e.g. "cpu"
     * @param typeInstance  the corresponding collectd type instance, e.g. "idle"
     * @param values the metrics, cannot be null
     * @param interval how often these metrics are collected, cannot be null
     */
    public ValueListEvent(String host, TimeSpan timestamp, String pluginName, String pluginInstance, String typeName,
        String typeInstance, Number[] values, TimeSpan interval) {
        super(host, timestamp, pluginName, pluginInstance, typeName, typeInstance);
        assertNotNull(values, "values is null");
        assertNotNull(interval, "interval is null");
        this.values = values;
        this.interval = interval;
    }

    /**
     * @return the metrics, not null
     */
    public Number[] getValues() {
        return values;
    }

    /**
     * @return how often these metrics are collected, not null
     */
    public TimeSpan getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "ValueListEvent[" + super.toString() + ", values=" + Arrays.asList(values) + ", interval="
            + TimeResolution.toMillis(interval) + " ms" + ']';
    }
}
