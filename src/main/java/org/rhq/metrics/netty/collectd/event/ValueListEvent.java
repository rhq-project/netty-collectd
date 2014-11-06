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
 * @author Thomas Segismont
 */
public final class ValueListEvent extends Event {
    private final Number[] values;
    private final TimeSpan interval;

    public ValueListEvent(String host, TimeSpan timestamp, String pluginName, String pluginInstance, String typeName,
        String typeInstance, Number[] values, TimeSpan interval) {
        super(host, timestamp, pluginName, pluginInstance, typeName, typeInstance);
        assertNotNull(values, "values is null");
        assertNotNull(interval, "interval is null");
        this.values = values;
        this.interval = interval;
    }

    public Number[] getValues() {
        return values;
    }

    public TimeSpan getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "ValueListEvent[" + super.toString() + ", values=" + Arrays.asList(values) + ", interval="
            + TimeResolution.toMillis(interval) + " ms" + ']';
    }
}
