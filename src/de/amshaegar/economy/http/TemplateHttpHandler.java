package de.amshaegar.economy.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringEscapeUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.amshaegar.economy.EcoFlow;
import de.amshaegar.economy.db.SQLConnector;

public class TemplateHttpHandler implements HttpHandler {
	
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
		StringBuilder template = new StringBuilder();
		byte[] next = new byte[1024];
		while(i.read(next) != -1) {
			template.append(new String(next));
		}
		i.close();
		
		String response = template.toString();
		if(response.contains("{RECENT_TRANS}")) {
			SQLConnector c = EcoFlow.getConnector();
			StringBuilder transfers;
			try {
				PreparedStatement ps = c.getConnection().prepareStatement("SELECT * FROM "+c.getTableName("transfer")+" AS t JOIN "+c.getTableName("player")+" AS p ON t.player = p.id ORDER BY id DESC LIMIT 25");
				ResultSet rs = ps.executeQuery();
				transfers = new StringBuilder("<table id=\"transfers\">");
				while(rs.next()) {
					transfers.append("<tr><td>");
					transfers.append(rs.getFloat("amount"));
					transfers.append("</td><td>");
					transfers.append(rs.getString("name"));
					transfers.append("</td><td>");
					transfers.append(StringEscapeUtils.escapeHtml(rs.getString("subject")));
					transfers.append("</td></tr>");
				}
				transfers.append("</table>");
			} catch (SQLException ex) {
				transfers = new StringBuilder("<tr><td>Error while getting recent transfers: "+ex.getMessage()+"</td></tr>");
			}
			response = response.replace("{RECENT_TRANS}", transfers);
		}

		e.sendResponseHeaders(responseCode, response.getBytes().length);
		OutputStream o = e.getResponseBody();
		o.write(response.getBytes());
		o.close();
	}
	
}
