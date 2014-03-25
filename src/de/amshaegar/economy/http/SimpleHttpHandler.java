package de.amshaegar.economy.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.amshaegar.economy.EcoFlow;

public class SimpleHttpHandler implements HttpHandler {
	
	@Override
	public void handle(HttpExchange e) throws IOException {
		String root = EcoFlow.getPlugin().getDataFolder().getPath()+"/web";
		File f;
		if(e.getRequestURI().getPath().endsWith("/")) {
			f = new File(root+e.getRequestURI().getPath()+"index.html");
		} else {
			f = new File(root+e.getRequestURI().getPath()+".html");
			if(!f.exists()) {
				redirect(301, e.getRequestURI().getPath()+"/", e);
			}
		}
		if(f.exists()) {
			respond(200, f, e, parseCookie(e));
		} else {
			error(404, e);
		}
	}

	protected Map<String, String> parseCookie(HttpExchange e) {
		List<String> cookies = e.getRequestHeaders().get("Cookie");
		Map<String, String> cookie;
		if(cookies != null) {
			cookie = parseParameters(cookies.get(0), "; ");
		} else {
			cookie = new HashMap<String, String>();
		}
		if(cookie.get("session") == null || SessionManager.get(cookie.get("session")) == null) {
			String session = UUID.randomUUID().toString();
			SessionManager.create(session);
			e.getResponseHeaders().set("Set-Cookie", "session="+session+"; Path=/");
			cookie.put("session", session);
		}
		return cookie;
	}

	protected void respond(int responseCode, File f, HttpExchange e) throws IOException {
		FileInputStream i = new FileInputStream(f);
		e.sendResponseHeaders(responseCode, f.length());
		OutputStream o = e.getResponseBody();
		int next = -1;
		while((next = i.read()) != -1) {
			o.write(next);
		}
		i.close();
		o.close();
	}

	protected void respond(int responseCode, File f, HttpExchange e, Map<String, String> cookie) throws IOException {
		respond(responseCode, f, e);
	}

	protected void redirect(int responseCode, String to, HttpExchange e) throws IOException {
		e.getResponseHeaders().set("Location", to);
		e.sendResponseHeaders(responseCode, -1);
	}
	
	protected void error(int responseCode, HttpExchange e) throws IOException {
		respond(responseCode, new File(EcoFlow.getPlugin().getDataFolder().getPath()+"/web/error/"+responseCode+".html"), e);
	}
	
	protected Map<String, String> parseParameters(String query, String split) {
		Map<String, String> parameters = new HashMap<String, String>();
		if(query != null) {
			String[] pairs = query.split(split);
			for(String pair : pairs) {
				String[] keyvalue = pair.split("=");
				if(keyvalue.length == 2) {
					parameters.put(keyvalue[0], keyvalue[1]);
				}
			};
		}
		return parameters;
	}
	
}
