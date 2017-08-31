package cn.ljj.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import cn.ljj.util.Logger;
import cn.ljj.util.QCloudManager;
import cn.ljj.util.ServletUtil;
import cn.ljj.util.UrlStringUtil;

/**
 * Servlet implementation class ProxyUplodServer
 */
@WebServlet("/ProxyUplodServer")
public class ProxyUplodServer extends HttpServlet implements StaticDefines {
    private static final long serialVersionUID = 1L;
    private static final String TAG = ProxyUplodServer.class.getSimpleName();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProxyUplodServer() {
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
        ServletUtil.dumpRequest(request);
        if (!checkAuthorize(request, response)) {
            Logger.e(TAG, "doGet Authorize failed!");
            return;
        }
        String proxyUrl = request.getParameter(KEY_PARAM_PROXY_URL);
        Logger.i(TAG, "doGet proxyUrl=" + proxyUrl);
        URL url = new URL(proxyUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();
        byte[] buffer = new byte[102400];
        int len = inStream.read(buffer);
        while (len != -1) {
            Logger.d(TAG, "doGet transfer " + len);
            response.getOutputStream().write(buffer, 0, len);
            len = inStream.read(buffer);
        }
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
        String proxyUrl = request.getParameter(KEY_PARAM_PROXY_URL);
        Logger.i(TAG, "doPost proxyUrl=" + proxyUrl);
        URL url = new URL(proxyUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();
        File tempFile = new File(UPLOAD_SAVE_FILE_ROOT_PATH,
                ".proxyUploadTemp/" + System.currentTimeMillis() + "_" + Thread.currentThread().getId());
        Logger.i(TAG, "doPost tempFile=" + tempFile.getAbsolutePath());
        tempFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[102400];
        int len = inStream.read(buffer);
        while (len != -1) {
            fileOutputStream.write(buffer, 0, len);
            len = inStream.read(buffer);
        }
        fileOutputStream.close();
        String path = request.getParameter(KEY_PARAM_PATH);
        Logger.i(TAG, "doPost path=" + path);
        String qCloudUrl = QCloudManager.getInstance().upload(path, tempFile.getPath());
        tempFile.delete();
        JsonObject object = new JsonObject();
        object.addProperty("url", qCloudUrl);
        response.getWriter().write(object.toString());
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
