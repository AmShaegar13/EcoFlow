package de.amshaegar.economy.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.amshaegar.economy.EcoFlow;

public class WebInterface {
	
	private HttpServer server;

	public WebInterface(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new SimpleHttpHandler("<h1>It works!</h1>"));
		server.start();
		EcoFlow.getPlugin().getLogger().info(String.format("Web interface started on port %d", server.getAddress().getPort()));
	}
	
	public void stop() {
		server.stop(0);
	}
	
	class SimpleHttpHandler implements HttpHandler {
		
		private String response;

		public SimpleHttpHandler(String response) {
			this.response = response;
		}
		
		@Override
		public void handle(HttpExchange e) throws IOException {
			e.sendResponseHeaders(200, response.getBytes().length);
			OutputStream o = e.getResponseBody();
			o.write(response.getBytes());
			o.close();
		}
		
	}

}
