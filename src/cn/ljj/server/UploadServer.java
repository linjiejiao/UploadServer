package cn.ljj.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import cn.ljj.util.Logger;
import cn.ljj.util.ServletUtil;

/**
 * Servlet implementation class UploadServer
 */
@MultipartConfig(location = "/Users/liangjiajian/Servers")
@WebServlet(name = "UploadServer")
public class UploadServer extends HttpServlet implements StaticDefines {
	private static final long serialVersionUID = 1L;
	private static final String TAG = UploadServer.class.getSimpleName();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServer() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletUtil.dumpRequest(request);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletUtil.dumpRequest(request);
		Part filePart = request.getPart("file");
		String fileName = ServletUtil.getPartFileName(filePart);
		Logger.d(TAG, "doPost filePart : fileName=" + fileName);
		if(fileName == null){
			return;
		}
		String path = request.getParameter("path");
		File folder = new File(UPLOAD_SAVE_FILE_ROOT_PATH, path);
		if(!folder.exists()){
			folder.mkdirs();
		}
		File file = new File(folder, fileName);
		boolean override = "1".equals(request.getParameter("override"));
		if(!override && file.exists()){
			if(fileName.contains(".")){
				
			}else{
				fileName = fileName + "";
			}
		}else{
			filePart.write(fileName);
		}
	}
}
