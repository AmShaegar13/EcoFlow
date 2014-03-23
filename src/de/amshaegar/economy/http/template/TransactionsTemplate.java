package de.amshaegar.economy.http.template;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringEscapeUtils;

import de.amshaegar.economy.EcoFlow;
import de.amshaegar.economy.db.SQLConnector;

public class TransactionsTemplate implements Template {
	
	private int limit;
	
	public TransactionsTemplate(int limit) {
		this.limit = limit;
	}

	@Override
	public String apply(String sequence, String line) {
		SQLConnector connector = EcoFlow.getConnector();
		StringBuilder transfers = new StringBuilder("<table id=\"transfers\">");;
		try {
			ResultSet rs = connector.selectTransfers(limit);
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
		} catch (SQLException ex) {
			transfers = new StringBuilder("<tr><td>Error while getting recent transfers: "+ex.getMessage()+"</td></tr>");
		}
		return line.replace(sequence, transfers);
	}

}
