package de.amshaegar.economy.http;

import java.io.File;
import java.io.FileInputStream;
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
		server.createContext("/", new SimpleHttpHandler());
		server.start();
		EcoFlow.getPlugin().getLogger().info(String.format("Web interface started on port %d", server.getAddress().getPort()));
	}
	
	public void stop() {
		server.stop(0);
	}
	
	class SimpleHttpHandler implements HttpHandler {
		
		@Override
		public void handle(HttpExchange e) throws IOException {
			File f = new File(EcoFlow.getPlugin().getDataFolder().getPath()+"/web"+e.getRequestURI().getPath());
			if(f.isDirectory()) {
				f = new File(f.getPath()+"/index.html");
			}
			if(f.exists()) {
				respond(200, f, e);
			} else {
				respond(404, new File(EcoFlow.getPlugin().getDataFolder().getPath()+"/web/404.html"), e);
			}
		}

		private void respond(int responseCode, File f, HttpExchange e) throws IOException {
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
		
	}

}
