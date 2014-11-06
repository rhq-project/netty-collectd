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

/**
 * @author Thomas Segismont
 */
public abstract class Event {
    private final String host;
    private final TimeSpan timestamp;
    private final String pluginName;
    private final String pluginInstance;
    private final String typeName;
    private final String typeInstance;

    protected Event(String host, TimeSpan timestamp, String pluginName, String pluginInstance, String typeName,
        String typeInstance) {
        this.host = host;
        this.timestamp = timestamp;
        this.pluginName = pluginName;
        this.pluginInstance = pluginInstance;
        this.typeName = typeName;
        this.typeInstance = typeInstance;
    }

    public final String getHost() {
        return host;
    }

    public final TimeSpan getTimestamp() {
        return timestamp;
    }

    public final String getPluginName() {
        return pluginName;
    }

    public final String getPluginInstance() {
        return pluginInstance;
    }

    public final String getTypeName() {
        return typeName;
    }

    public final String getTypeInstance() {
        return typeInstance;
    }

    @Override
    public String toString() {
        return "Event[" + "host='" + host + '\'' + ", timestamp=" + TimeResolution.toDate(timestamp) + ", pluginName='"
            + pluginName + '\'' + ", pluginInstance='" + pluginInstance + '\'' + ", typeName='" + typeName + '\''
            + ", typeInstance='" + typeInstance + '\'' + ']';
    }
}
