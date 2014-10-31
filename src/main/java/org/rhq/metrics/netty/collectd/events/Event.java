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

import org.rhq.metrics.netty.collectd.values.Values;

/**
 * @author Thomas Segismont
 */
public final class Event {
    private final String host;
    private final TimeSpan timestamp;
    private final PluginData pluginData;
    private final TypeData typeData;
    private final Values values;
    private final TimeSpan interval;

    public Event(String host, TimeSpan timestamp, PluginData pluginData, TypeData typeData, Values values,
        TimeSpan interval) {
        this.host = host;
        this.timestamp = timestamp;
        this.pluginData = pluginData;
        this.typeData = typeData;
        this.values = values;
        this.interval = interval;
    }

    public String getHost() {
        return host;
    }

    public TimeSpan getTimestamp() {
        return timestamp;
    }

    public PluginData getPluginData() {
        return pluginData;
    }

    public TypeData getTypeData() {
        return typeData;
    }

    public Values getValues() {
        return values;
    }

    public TimeSpan getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "Event[" + "host='" + host + '\'' + ", when="
            + TimeResolution.toDate(timestamp.getValue(), timestamp.getResolution()) + ", pluginData=" + pluginData
            + ", typeData=" + typeData + ", values=" + values + ", interval="
            + TimeResolution.toMillis(interval.getValue(), interval.getResolution()) + " ms" + ']';
    }
}
