package cn.ljj.util;

import java.util.HashMap;
import java.util.Map;

public class Config {
	static Config sInstance = null;
	private Map<String, String> configMap = new HashMap<String, String>();

	public static Config getInstance() {
		if (sInstance == null) {
			sInstance = new Config();
			sInstance.loadFromFile(userHomePath() + "/config.ini", false);
			sInstance.loadFromFile("config.ini", false);
		}
		return sInstance;
	}

	public static String userHomePath() {
		return System.getProperty("user.home");
	}

	private Config() {

	}

	public void loadFromFile(String configFile, boolean clean) {
		if (clean) {
			configMap.clear();
		}
		configMap.putAll(FileUtils.getConfigs(configFile));
	}

	public String get(String key) {
		return configMap.get(key);
	}
}
