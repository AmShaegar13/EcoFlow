package de.amshaegar.economy.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import de.amshaegar.economy.EcoFlow;

public class AuthHttpHandler extends SimpleHttpHandler {

	@Override
	public void handle(HttpExchange e) throws IOException {
		String dataDir = EcoFlow.getPlugin().getDataFolder().getPath();
		if(e.getRequestMethod().equalsIgnoreCase("post")) {
			BufferedReader in = new BufferedReader(new InputStreamReader(e.getRequestBody()));
			Map<String, String> parameters = parseParameters(in.readLine());
			in.close();
			if(parameters.containsKey("password") && parameters.get("password").equals(EcoFlow.getPlugin().getConfig().getString("web.password"))) {
				// TODO do login
				e.getResponseHeaders().set("Location", "/");
				e.sendResponseHeaders(303, -1);
				return;
			} else {
				EcoFlow.getPlugin().getLogger().warning("Login to web interface from "+e.getRemoteAddress().getAddress().getHostAddress()+" failed!");
			}
		}
		respond(403, new File(dataDir+"/web/error/403.html"), e);
	}

}
