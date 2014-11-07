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

import static org.junit.Assert.assertFalse;

import java.net.InetSocketAddress;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;

import org.junit.Test;

public class CollectdPacketDecoderTest {
    private static final InetSocketAddress DUMMY_ADDRESS = InetSocketAddress.createUnresolved("dummy", 9999);

    @Test
    public void handlerShouldNotOutputCollectdPacketWhenNoPartIsDecoded() {
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.buffer(), DUMMY_ADDRESS);
        EmbeddedChannel channel = new EmbeddedChannel(new CollectdPacketDecoder());
        assertFalse("Expected no CollectdPacket", channel.writeInbound(datagramPacket));
    }
}