package de.amshaegar.economy.http;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

	private static Map<String, Map<String, String>> sessions = new HashMap<String, Map<String, String>>();
	
	public static void create(String id) {
		sessions.put(id, new HashMap<String, String>());
	}
	
	public static Map<String, String> get(String id) {
		return sessions.get(id);
	}
	
	public static void destroy(String id) {
		sessions.remove(id);
	}
	
}
