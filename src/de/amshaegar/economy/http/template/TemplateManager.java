package de.amshaegar.economy.http.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TemplateManager {
	
	private Map<String, Template> templates = new HashMap<String, Template>();
	private Map<String, String> parameters;
	
	public void registerTemplate(String sequence, Template template) {
		templates.put(sequence, template);
	}

	public String handle(String line) {
		for(Entry<String, Template> e : templates.entrySet()) {
			if(line.contains(e.getKey())) {
				line = e.getValue().apply(e.getKey(), line, parameters);
			}
		}
		return line;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

}
