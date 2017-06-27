package cn.ljj.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ljj.util.Logger;
import cn.ljj.util.ServletUtil;

/**
 * Servlet implementation class LocalFilesBaseServlet
 */
public abstract class LocalFilesBaseServlet extends HttpServlet {
	private static final String TAG = LocalFilesBaseServlet.class.getSimpleName();
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LocalFilesBaseServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletUtil.dumpRequest(request);
		String filePath = getLocalFilePath(request.getRequestURI(), request.getQueryString());
		respondFileContent(filePath, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected boolean respondFileContent(String path, HttpServletResponse response) throws IOException {
		Logger.d(TAG, "respondFileContent path=" + path);
		if (path == null) {
			response.sendError(HttpServletResponse.SC_FOUND);
			return false;
		}
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			response.sendError(HttpServletResponse.SC_FOUND);
			return false;
		}
		InputStream input = new FileInputStream(path);
		byte[] buffer = new byte[1024];
		int len = input.read(buffer, 0, 1024);
		while (len > 0) {
			response.getOutputStream().write(buffer, 0, len);
			len = input.read(buffer, 0, 1024);
		}
		input.close();
		return true;
	}

	protected String getLocalFilePath(String uri, String queryString) {
		Logger.d(TAG, "getLocalFilePath uri=" + uri + ", queryString=" + queryString);
		String baseUrlPath = getBaseUrlPath();
		int index = uri.indexOf(baseUrlPath);
		if (index != -1) {
			String relativePath = uri.substring(index + baseUrlPath.length());
			if (relativePath.length() > 0) {
				if (!relativePath.startsWith("/")) {
					relativePath = "/" + relativePath;
				}
				relativePath = relativePath.replace("/", File.separator);
			} else {
				relativePath = File.separator;
			}
			return getBaseLocalPath() + relativePath;
		} else {
			return null;
		}
	}

	/**
	 * implement to return the local base file path
	 */
	protected abstract String getBaseLocalPath();

	/**
	 * implement to return the base url path
	 */
	protected abstract String getBaseUrlPath();

}
