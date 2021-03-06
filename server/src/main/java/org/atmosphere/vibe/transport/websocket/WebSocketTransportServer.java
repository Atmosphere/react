/*
 * Copyright 2014 The Vibe Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.atmosphere.vibe.transport.websocket;

import java.nio.ByteBuffer;

import org.atmosphere.vibe.platform.action.Action;
import org.atmosphere.vibe.platform.action.Actions;
import org.atmosphere.vibe.platform.action.ConcurrentActions;
import org.atmosphere.vibe.platform.action.VoidAction;
import org.atmosphere.vibe.platform.websocket.ServerWebSocket;
import org.atmosphere.vibe.transport.BaseServerTransport;
import org.atmosphere.vibe.transport.ServerTransport;
import org.atmosphere.vibe.transport.TransportServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Websocket implementation of {@link TransportServer}.
 * <p>
 * It processes transport whose URI whose protocol is either {@code ws} or
 * {@code wss} like {@code ws://localhost:8080/vibe}. Because WebSocket protocol
 * itself meets transport's requirements, a produced transport is actually a
 * thread-safe version of {@link ServerWebSocket}.
 * 
 * @author Donghwan Kim
 */
public class WebSocketTransportServer implements TransportServer<ServerWebSocket> {

    private final Logger log = LoggerFactory.getLogger(WebSocketTransportServer.class);
    private Actions<ServerTransport> transportActions = new ConcurrentActions<ServerTransport>()
    .add(new Action<ServerTransport>() {
        @Override
        public void on(final ServerTransport transport) {
            log.trace("{}'s request has opened", transport);
            transport.onclose(new VoidAction() {
                @Override
                public void on() {
                    log.trace("{}'s request has been closed", transport);
                }
            });
        }
    });

    @Override
    public void on(ServerWebSocket ws) {
        transportActions.fire(new DefaultTransport(ws));
    }

    @Override
    public WebSocketTransportServer ontransport(Action<ServerTransport> action) {
        transportActions.add(action);
        return this;
    }

    /**
     * Represents a server-side WebSocket transport.
     * 
     * @author Donghwan Kim
     */
    private static class DefaultTransport extends BaseServerTransport {

        private final ServerWebSocket ws;

        public DefaultTransport(ServerWebSocket ws) {
            this.ws = ws;
            ws.onerror(new Action<Throwable>() {
                @Override
                public void on(Throwable throwable) {
                    errorActions.fire(throwable);
                }
            })
            .onclose(new VoidAction() {
                @Override
                public void on() {
                    closeActions.fire();
                }
            })
            .ontext(new Action<String>() {
                @Override
                public void on(String data) {
                    textActions.fire(data);
                }
            })
            .onbinary(new Action<ByteBuffer>() {
                @Override
                public void on(ByteBuffer data) {
                    binaryActions.fire(data);
                }
            });
        }

        @Override
        public String uri() {
            return ws.uri();
        }

        @Override
        protected synchronized void doSend(String data) {
            ws.send(data);
        }

        @Override
        protected synchronized void doSend(ByteBuffer data) {
            ws.send(data);
        }

        @Override
        public synchronized void doClose() {
            ws.close();
        }

        /**
         * {@link ServerWebSocket} is available.
         */
        @Override
        public <T> T unwrap(Class<T> clazz) {
            return ServerWebSocket.class.isAssignableFrom(clazz) ? clazz.cast(ws) : null;
        }

    }

}
