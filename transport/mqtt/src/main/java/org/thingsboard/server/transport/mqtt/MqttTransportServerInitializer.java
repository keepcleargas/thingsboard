/**
 * Copyright © 2016 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.transport.mqtt;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.springframework.beans.factory.annotation.Value;
import org.thingsboard.server.common.transport.SessionMsgProcessor;
import org.thingsboard.server.common.transport.auth.DeviceAuthService;
import org.thingsboard.server.transport.mqtt.adaptors.MqttTransportAdaptor;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author Andrew Shvayka
 */
public class MqttTransportServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SessionMsgProcessor processor;
    private final DeviceAuthService authService;
    private final MqttTransportAdaptor adaptor;
    private final MqttSslHandlerProvider sslHandlerProvider;

    public MqttTransportServerInitializer(SessionMsgProcessor processor, DeviceAuthService authService, MqttTransportAdaptor adaptor,
                                          MqttSslHandlerProvider sslHandlerProvider) {
        this.processor = processor;
        this.authService = authService;
        this.adaptor = adaptor;
        this.sslHandlerProvider = sslHandlerProvider;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslHandlerProvider != null) {
            pipeline.addLast(sslHandlerProvider.getSslHandler());
        }
        pipeline.addLast("decoder", new MqttDecoder());
        pipeline.addLast("encoder", MqttEncoder.INSTANCE);

        MqttTransportHandler handler = new MqttTransportHandler(processor, authService, adaptor);
        pipeline.addLast(handler);
        ch.closeFuture().addListener(handler);
    }

}
