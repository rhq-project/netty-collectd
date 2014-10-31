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

package org.rhq.metrics.netty.collectd;

import static io.netty.channel.ChannelHandler.Sharable;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.rhq.metrics.netty.collectd.events.Event;

/**
 * @author Thomas Segismont
 */
@Sharable
public class DumpToLogEventHandler extends SimpleChannelInboundHandler<Event> {
    private static final Logger LOG = LoggerFactory.getLogger(DumpToLogEventHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Event msg) throws Exception {
        LOG.info(msg.toString());
    }
}
