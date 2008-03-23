package cn.edu.zju.acm.onlinejudge.util;

public class Features {
	public static boolean problemset() {
		return "true".equalsIgnoreCase(ConfigManager.getValue("feature_problemset"));
	}
	public static boolean contest() {
		return "true".equalsIgnoreCase(ConfigManager.getValue("feature_contest"));
	}
	public static boolean editProfile() {
		return "true".equalsIgnoreCase(ConfigManager.getValue("feature_editProfile"));
	}
	public static boolean register() {
		return "true".equalsIgnoreCase(ConfigManager.getValue("feature_register"));
	}
	public static boolean forgotPassword() {
		return "true".equalsIgnoreCase(ConfigManager.getValue("feature_forgotPassword"));
	}
}
