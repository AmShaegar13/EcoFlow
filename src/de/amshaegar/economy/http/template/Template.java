package de.amshaegar.economy.http.template;

import java.util.Map;

public interface Template {
	
	public String apply(String sequence, String line, Map<String, String> parameters);

}
