package de.amshaegar.economy.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import de.amshaegar.economy.EcoFlow;

public class WebInterface {
	
	private HttpServer server;

	public WebInterface(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new TemplateHttpHandler());
		server.createContext("/auth", new AuthHttpHandler());
		server.createContext("/intern", new InternHttpHandler());
		server.start();
		EcoFlow.getPlugin().getLogger().info(String.format("Web interface started on port %d", server.getAddress().getPort()));
	}
	
	public void stop() {
		server.stop(0);
	}

}
