package cn.ljj.server;

import cn.ljj.util.Config;

public interface StaticDefines {
	public static final String UPLOAD_SAVE_FILE_ROOT_PATH = Config.userHomePath() + "/Servers";
	public static final String AUTHORIZE_TOKEN = Config.getInstance().get("upload_server_token");

	// Url path
	public static final String URL_PATH_NORMAL = "normal";
	public static final String URL_PATH_PROXY = "proxy";

	// parameter keys
	public static final String KEY_PARAM_FILE_PART = "file";
	public static final String KEY_PARAM_PATH = "path";
	public static final String KEY_PARAM_OVERRIDE = "override";
	public static final String KEY_PARAM_TOKEN = "token";
	public static final String KEY_PARAM_PROXY_URL = "proxy_url";
}
