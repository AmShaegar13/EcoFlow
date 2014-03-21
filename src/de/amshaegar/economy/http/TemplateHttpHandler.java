package de.amshaegar.economy.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringEscapeUtils;

import com.sun.net.httpserver.HttpExchange;

import de.amshaegar.economy.EcoFlow;
import de.amshaegar.economy.db.SQLConnector;

public class TemplateHttpHandler extends SimpleHttpHandler {

	@Override
	protected void respond(int responseCode, File f, HttpExchange e) throws IOException {
		e.sendResponseHeaders(responseCode, 0);
		BufferedReader in = new BufferedReader(new FileReader(f));
		OutputStream out = e.getResponseBody();
		String line;
		while((line = in.readLine()) != null) {
			if(line.contains("{RECENT_TRANS}")) {
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
				line = line.replace("{RECENT_TRANS}", transfers);
			}
			out.write(line.getBytes());
		}
		out.close();
		in.close();
	}
	
}
