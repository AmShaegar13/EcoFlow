package de.amshaegar.economy.http.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import de.amshaegar.economy.EcoFlow;
import de.amshaegar.economy.db.SQLConnector;

public class SubjectsTemplate implements Template {
	
	private int limit;
	
	public SubjectsTemplate(int limit) {
		this.limit = limit;
	}

	@Override
	public String apply(String sequence, String line, Map<String, String> parameters) {
		SQLConnector connector = EcoFlow.getConnector();
		try {
			StringBuilder transfers = new StringBuilder("<table id=\"subjects\">");
			transfers.append("<tr><th>Alias</th><th>Edit</th><th>Subject</th></tr>");
			ResultSet rs = connector.selectSubjects(parameters.containsKey("limit") ? Integer.parseInt(parameters.get("limit")) : limit);
			while(rs.next()) {
				transfers.append("<tr><td><input type=\"text\" value=\"");
				if(rs.getString("alias") != null) {
					transfers.append(rs.getString("alias"));
				}
				transfers.append("\" /></td><td><input type=\"submit\" value=\"Edit\" /></td><td>");
				transfers.append(rs.getString("subject"));
				transfers.append("</td></tr>");
			}
			transfers.append("</table>");
			return line.replace(sequence, transfers.toString());
		} catch (SQLException e) {
			return "Error while getting subjects: "+e.getMessage();
		} catch (NumberFormatException e) {
			return "Limit is not a valid number: "+e.getMessage();
		}
	}

}
