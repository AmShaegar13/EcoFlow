package de.amshaegar.economy.http.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import de.amshaegar.economy.EcoFlow;
import de.amshaegar.economy.db.SQLConnector;

public class TransactionsTemplate implements Template {
	
	private int limit;
	
	public TransactionsTemplate(int limit) {
		this.limit = limit;
	}

	@Override
	public String apply(String sequence, String line, Map<String, String> parameters) {
		SQLConnector connector = EcoFlow.getConnector();
		try {
			StringBuilder transfers = new StringBuilder("<table id=\"transfers\">");
			ResultSet rs = connector.selectTransfers(parameters.containsKey("limit") ? Integer.parseInt(parameters.get("limit")) : limit);
			while(rs.next()) {
				transfers.append("<tr><td>");
				transfers.append(rs.getDate("time"));
				transfers.append(" ");
				transfers.append(rs.getTime("time"));
				transfers.append("</td><td>");
				transfers.append(rs.getFloat("amount"));
				transfers.append("</td><td>");
				transfers.append(rs.getString("name"));
				transfers.append("</td><td>");
				transfers.append(StringEscapeUtils.escapeHtml(rs.getString("alisub")));
				transfers.append("</td></tr>");
			}
			transfers.append("</table>");
			return line.replace(sequence, transfers.toString());
		} catch (SQLException e) {
			return "Error while getting recent transfers: "+e.getMessage();
		} catch (NumberFormatException e) {
			return "Limit is not a valid number: "+e.getMessage();
		}
	}

}
