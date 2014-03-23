package de.amshaegar.economy.http;

import java.io.File;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class InternHttpHandler extends TemplateHttpHandler {
	
	@Override
	protected void respond(int responseCode, File f, HttpExchange e) throws IOException {
		// TODO check login
		if(true) {
			super.respond(responseCode, f, e);
		}
	}
	
}
