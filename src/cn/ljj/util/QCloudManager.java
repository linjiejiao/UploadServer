package cn.ljj.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.request.StatFileRequest;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;

public class QCloudManager {
	private static final String TAG = QCloudManager.class.getSimpleName();
	private static QCloudManager sInstance = null;

	private COSClient cosClient = null;

	private QCloudManager() {
		long appId = Integer.parseInt(Config.getInstance().get("qc_app_id"));
		String secretId = Config.getInstance().get("qc_secret_id");
		String secretKey = Config.getInstance().get("qc_secret_key");
		String region = Config.getInstance().get("qc_region");
		Credentials cred = new Credentials(appId, secretId, secretKey);
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setRegion(region);
		cosClient = new COSClient(clientConfig, cred);
	}

	public static QCloudManager getInstance() {
		if (sInstance == null) {
			sInstance = new QCloudManager();
		}
		return sInstance;
	}

	public String upload(String bucketName, String remoteFilePath, String localFilePath) {
		UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, remoteFilePath, localFilePath);
		String uploadFileRet = cosClient.uploadFile(uploadFileRequest);
		Logger.d(TAG, "uploadFileRet=" + uploadFileRet);
		JsonObject retJson = new JsonParser().parse(uploadFileRet).getAsJsonObject();
		if (retJson == null) {
            Logger.e(TAG, "upload retJson=null");
			return null;
		}
		int code = retJson.get("code").getAsInt();
		String message = retJson.get("message").getAsString();
		if (code == 0 && "SUCCESS".equals(message)) {
			JsonObject dataObject = retJson.get("data").getAsJsonObject();
			if (dataObject != null) {
				String url = dataObject.get("access_url").getAsString();
				Logger.w(TAG, "accessUrl=" + url);
				if (url == null || url.length() <= 0) {
					url = dataObject.get("source_url").getAsString();
					Logger.w(TAG, "source_url=" + url);
				}
				if (url == null || url.length() <= 0) {
					url = dataObject.get("url").getAsString();
					Logger.w(TAG, "url=" + url);
				}
				return url;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param remoteFilePath bucketName/remoteFileName
	 * @param localFilePath
	 * @return
	 */
	public String upload(String remoteFilePath, String localFilePath) {
		if (remoteFilePath.startsWith("/")) {
			remoteFilePath = remoteFilePath.substring(1);
		}
		int index = remoteFilePath.indexOf("/");
		if (index == -1) {
            Logger.e(TAG, "upload remoteFilePath=" + remoteFilePath);
			return null;
		}
		String bucketName = remoteFilePath.substring(0, index);
		remoteFilePath = remoteFilePath.substring(index);
		return upload(bucketName, remoteFilePath, localFilePath);
	}

	public String getUrlIfFileExits(String bucketName, String fileName) {
		StatFileRequest statFileRequest = new StatFileRequest(bucketName, fileName);
		String statFileRet = cosClient.statFile(statFileRequest);
		Logger.d(TAG, "statFileRet=" + statFileRet);
		JsonObject retJson = new JsonParser().parse(statFileRet).getAsJsonObject();
		if (retJson == null) {
            Logger.e(TAG, "getUrlIfFileExits retJson=null");
			return null;
		}
		int code = retJson.get("code").getAsInt();
		String message = retJson.get("message").getAsString();
		if (code == 0 && "SUCCESS".equals(message)) {
			JsonObject dataObject = retJson.get("data").getAsJsonObject();
			if (dataObject != null) {
				String url = dataObject.get("access_url").getAsString();
				Logger.w(TAG, "accessUrl=" + url);
				if (url == null || url.length() <= 0) {
					url = dataObject.get("source_url").getAsString();
					Logger.w(TAG, "source_url=" + url);
				}
				return url;
			}
		}
		return null;
	}

}
