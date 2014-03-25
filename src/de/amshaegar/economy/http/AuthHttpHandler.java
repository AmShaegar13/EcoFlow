package de.amshaegar.economy.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import de.amshaegar.economy.EcoFlow;

public class AuthHttpHandler extends SimpleHttpHandler {

	@Override
	public void handle(HttpExchange e) throws IOException {
		if(e.getRequestMethod().equalsIgnoreCase("post")) {
			BufferedReader in = new BufferedReader(new InputStreamReader(e.getRequestBody()));
			Map<String, String> parameters = parseParameters(in.readLine(), "&");
			in.close();
			if(parameters.containsKey("password") && parameters.get("password").equals(EcoFlow.getPlugin().getConfig().getString("web.password"))) {
				Map<String, String> cookie = parseCookie(e);
				SessionManager.get(cookie.get("session")).put("auth", "true");
				
				redirect(303,"/intern/", e);
				return;
			} else {
				EcoFlow.getPlugin().getLogger().warning("Login to web interface from "+e.getRemoteAddress().getAddress().getHostAddress()+" failed!");
			}
		}
		error(403, e);
	}

}
