package de.amshaegar.economy.http;

import java.io.IOException;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class InternHttpHandler extends TemplateHttpHandler {
	
	@Override
	public void handle(HttpExchange e) throws IOException {
		Map<String, String> cookie = parseCookie(e);
		Map<String, String> session = SessionManager.get(cookie.get("session"));
		boolean auth = session != null ? Boolean.parseBoolean(session.get("auth")) : false;
		if(auth) {
			super.handle(e);
		} else {
			error(401, e);
		}
	}
	
}
