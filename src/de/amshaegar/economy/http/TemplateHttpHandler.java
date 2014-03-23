package de.amshaegar.economy.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bukkit.configuration.file.FileConfiguration;

import com.sun.net.httpserver.HttpExchange;

import de.amshaegar.economy.EcoFlow;
import de.amshaegar.economy.http.template.TemplateManager;
import de.amshaegar.economy.http.template.TransactionsTemplate;

public class TemplateHttpHandler extends SimpleHttpHandler {
	
	private TemplateManager templates;
	
	public TemplateHttpHandler() {
		templates = new TemplateManager();
		
		FileConfiguration c = EcoFlow.getPlugin().getConfig();
		
		templates.registerTemplate("{RECENT_TRANS}", new TransactionsTemplate(c.getInt("web.recent.limit")));
	}

	@Override
	protected void respond(int responseCode, File f, HttpExchange e) throws IOException {
		long start = System.currentTimeMillis();
		templates.setParameters(parseParameters(e.getRequestURI().getQuery()));
		e.sendResponseHeaders(responseCode, 0);
		BufferedReader in = new BufferedReader(new FileReader(f));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(e.getResponseBody()));
		String line;
		while((line = in.readLine()) != null) {
			line = templates.handle(line);
			out.write(line);
			out.newLine();
		}
		out.write(String.format("<!-- Generated in %.3fs -->", (System.currentTimeMillis()-start)/1000.0));
		out.close();
		in.close();
	}
	
}
