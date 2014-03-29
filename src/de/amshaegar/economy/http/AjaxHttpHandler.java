package de.amshaegar.economy.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;

import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import de.amshaegar.economy.EcoFlow;

public class AjaxHttpHandler extends SimpleHttpHandler {
	
	@Override
	public void handle(HttpExchange e) throws IOException {
		if(e.getRequestMethod().equalsIgnoreCase("get")) {
			error(400, e);
			return;
		}
		
		Map<String, String> session = SessionManager.get(parseCookie(e).get("session"));
		boolean auth = session != null ? Boolean.parseBoolean(session.get("auth")) : false;
		if(auth) {
			BufferedReader in = new BufferedReader(new InputStreamReader(e.getRequestBody()));
			Map<String, String> parameters = parseParameters(in.readLine(), "&");
			in.close();
			if(parameters.containsKey("id")) {
				try {
					EcoFlow.getConnector().updateAlias(Integer.parseInt(parameters.get("id")), parameters.get("alias"));
					respond(e);
				} catch(NumberFormatException ex) {
					error(400, e);
				} catch(SQLException ex) {
					respond(ex.getMessage(), e);
				}
			} else {
				error(400, e);
			}
		} else {
			error(401, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void respond(HttpExchange e) throws IOException {
		JSONObject json = new JSONObject();
		json.put("sucess", true);
		respond(json, e);
	}
	
	@SuppressWarnings("unchecked")
	private void respond(String error, HttpExchange e) throws IOException {
		JSONObject json = new JSONObject();
		json.put("sucess", false);
		json.put("error", error);
		respond(json, e);
	}
	
	private void respond(JSONObject json, HttpExchange e) throws IOException {
		e.sendResponseHeaders(200, 0);
		OutputStream out = e.getResponseBody();
		out.write(json.toJSONString().getBytes());
		out.close();
	}

}
