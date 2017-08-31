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

import com.google.gson.JsonObject;

import cn.ljj.util.FileUtils;
import cn.ljj.util.Logger;
import cn.ljj.util.ServletUtil;
import cn.ljj.util.UrlStringUtil;

/**
 * Servlet implementation class UploadServer
 */
@MultipartConfig(location = ".")
@WebServlet(name = "UploadServer")
public class UploadServer extends LocalFilesBaseServlet implements StaticDefines {
    private static final long serialVersionUID = 1L;
    private static final String TAG = UploadServer.class.getSimpleName();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServer() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        String prefix = getServletContext().getRealPath("/");
        String log4jConfigFile = getServletContext().getInitParameter("log4j-init-file");
        if (log4jConfigFile != null) {
            Logger.init(prefix + log4jConfigFile);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAuthorize(request, response)) {
            Logger.e(TAG, "doGet Authorize failed!");
            return;
        }
        super.doGet(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.dumpRequest(request);
        if (!checkAuthorize(request, response)) {
            Logger.e(TAG, "doPost Authorize failed!");
            return;
        }
        Part filePart = request.getPart(KEY_PARAM_FILE_PART);
        String fileName = ServletUtil.getPartFileName(filePart);
        Logger.d(TAG, "doPost filePart : fileName=" + fileName);
        if (fileName == null) {
            return;
        }
        String path = request.getParameter(KEY_PARAM_PATH);
        if (path == null) {
            path = ".";
        }
        File folder = new File(UPLOAD_SAVE_FILE_ROOT_PATH, path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        boolean override = "1".equals(request.getParameter(KEY_PARAM_OVERRIDE));
        String finalFileName = null;
        if (!override) {
            finalFileName = FileUtils.getConflictResovedPath(folder.getPath(), fileName);
        } else {
            finalFileName = folder.getPath() + File.separator + fileName;
        }
        Logger.i(TAG, "finalFileName=" + finalFileName);
        filePart.write(finalFileName);
        JsonObject object = new JsonObject();
        object.addProperty("path", finalFileName.replace(UPLOAD_SAVE_FILE_ROOT_PATH, ""));
        response.getWriter().write(object.toString());
    }

    @Override
    protected String getBaseLocalPath() {
        return UPLOAD_SAVE_FILE_ROOT_PATH;
    }

    @Override
    protected String getBaseUrlPath() {
        return URL_PATH_NORMAL;
    }

    private boolean checkAuthorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = UrlStringUtil.parseQueryString(request.getQueryString()).get(KEY_PARAM_TOKEN);
        Logger.i(TAG, "checkAuthorize token=" + token);
        if (!AUTHORIZE_TOKEN.equals(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
