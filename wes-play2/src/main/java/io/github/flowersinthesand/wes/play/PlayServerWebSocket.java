package io.github.flowersinthesand.wes.play;

import io.github.flowersinthesand.wes.Data;
import io.github.flowersinthesand.wes.websocket.AbstractServerWebSocket;
import io.github.flowersinthesand.wes.websocket.CloseReason;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Http.Request;
import play.mvc.WebSocket;
import play.mvc.WebSocket.In;
import play.mvc.WebSocket.Out;

public class PlayServerWebSocket extends AbstractServerWebSocket {

	private final Request request;
	private final WebSocket.Out<String> out;

	public PlayServerWebSocket(Request request, In<String> in, Out<String> out) {
		this.request = request;
		this.out = out;
		in.onMessage(new Callback<String>() {
			@Override
			public void invoke(String message) throws Throwable {
				messageActions.fire(new Data(message));
			}
		});
		in.onClose(new Callback0() {
			@Override
			public void invoke() throws Throwable {
				closeActions.fire();
			}
		});
	}

	@Override
	public String uri() {
		return request.uri();
	}

	@Override
	protected void doClose(CloseReason reason) {
		out.close();
	}

	@Override
	protected void doSend(String data) {
		out.write(data);
	}
	
	@Override
	public <T> T unwrap(Class<T> clazz) {
		return Request.class.isAssignableFrom(clazz) ? 
			clazz.cast(request) : 
			Out.class.isAssignableFrom(clazz) ? 
				clazz.cast(out) : 
				null;
	}

}