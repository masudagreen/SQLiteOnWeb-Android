package io.github.skyhacker2.sqliteonweb;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import io.github.skyhacker2.sqliteonweb.dtos.Message;

/**
 * Created by eleven on 16/4/3.
 */
public class AndroidServer extends NanoHTTPD {

    public interface AndroidServerListener {
        Message onOpenRequest(Map<String, String> params);
        Message onExecSQLRequest(Map<String, String> params);
        Message onGetDBList(Map<String, String> params);
    }

    public final static String TAG = AndroidServer.class.getSimpleName();

    private Context mContext;
    private Gson mGson = new Gson();
    private AndroidServerListener mListener;

    public AndroidServer(Context context, int port) {
        super(port);
        mContext = context;
    }

    public AndroidServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Response response = getStaticFileResponse("/sqliteonweb", session);
        if (response != null) {
            return response;
        }
        if (uri.equals("/")) {
            return getStaticFileResponse("sqliteonweb/index.html");
        }
        // Open database
        else if (uri.equals("/open")) {
            return getOpenResponse(session);
        }
        else if (uri.equals("/execSQL")) {
            return getExecSQLResponse(session);
        }
        else if (uri.equals("/listDB")) {
            return getListDBResponse(session);
        }
        return getNotFoundResponse();
    }

    protected Response getNotFoundResponse() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
    }

    /**
     * Get static file response
     * @param dir
     * @param session
     * @return file if file found or null
     */
    public Response getStaticFileResponse(String dir, IHTTPSession session) {
        String uri = session.getUri();
        String fileName = uri.substring(1);
        Method method = session.getMethod();
        if (uri.indexOf(dir) == 0) {
            Log.d(TAG, "match static dir");
            // 只有Get方法可以访问静态文件
            if (method == Method.GET) {
                return getStaticFileResponse(fileName);
            }
        }

        return null;
    }

    /**
     * Get static file response
     * @param fileName
     * @return file if file found or null
     */
    public Response getStaticFileResponse(String fileName) {
        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream stream = assetManager.open(fileName);
            String extension = getFileExtensionName(fileName);
            return newChunkedResponse(Response.Status.OK, mimeTypes().get(extension),stream);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "File not exist. " + fileName);
            return null;
        }
    }

    /**
     * Get the extension name of filename
     * @param fileName
     * @return
     */
    public String getFileExtensionName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
    }

    /**
     * Return a json response
     * @param msg
     * @return
     */
    public Response newJsonResponse(Message msg) {
        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("json"), mGson.toJson(msg));
    }

    /**
     * Open database
     * @param session
     * @return
     */
    public Response getOpenResponse(IHTTPSession session) {
        if (mListener != null) {
            return newJsonResponse(mListener.onOpenRequest(session.getParms()));
        }
        return getNotFoundResponse();
    }

    public Response getExecSQLResponse(IHTTPSession session) {
        if (mListener != null) {
            return newJsonResponse(mListener.onExecSQLRequest(session.getParms()));
        }
        return getNotFoundResponse();
    }

    public Response getListDBResponse(IHTTPSession session) {
        if (mListener != null) {
            return newJsonResponse(mListener.onGetDBList(session.getParms()));
        }
        return getNotFoundResponse();
    }

    public AndroidServerListener getListener() {
        return mListener;
    }

    public void setListener(AndroidServerListener listener) {
        mListener = listener;
    }
}
