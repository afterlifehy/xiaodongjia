package com.wbb.base.view.dsbridge;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.wbb.base.BaseApplication;
import com.wbb.base.http.RetrofitUtils;
import com.wbb.base.network.SSLCertUtil;
import com.wbb.base.util.AppUtil;
import com.wbb.base.util.Constant;
import com.wbb.base.util.HttpsUtils;
import com.wbb.base.util.StringUtil;
import com.wbb.base.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.OnReturnValue;

//import android.app.AlertDialog;


/**
 * Created by du on 16/12/29.
 */

public class DWebView extends WebView {
    private static final String BRIDGE_NAME = "_dsbridge";
    private static final String LOG_TAG = "dsBridge";
    private static boolean isDebug = false;
    private Map<String, Object> javaScriptNamespaceInterfaces = new HashMap<String, Object>();
    private String APP_CACHE_DIRNAME;
    private int callID = 0;
    private WebChromeClient webChromeClient;

    private volatile boolean alertBoxBlock = true;
    private JavascriptCloseWindowListener javascriptCloseWindowListener = null;
    private ArrayList<CallInfo> callInfoList;
    private InnerJavascriptInterface innerJavascriptInterface = new InnerJavascriptInterface();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private WebViewUrlChangeListener webViewUrlChangeListener = null;
    private HttpsUtils.SSLParams sslParams = null;
    String toLoadJs = "WebViewJavascriptBridge.js";
    private static final String PPT = "https://phk1-powerpoint.officeapps.live.com";
    private OkHttpClient httpClient;
    //是否允许抓包
    private boolean isProxy = false;

    private class InnerJavascriptInterface {

        private void PrintDebugInfo(String error) {
            Log.d(LOG_TAG, error);
            if (isDebug) {
                evaluateJavascript(String.format("alert('%s')", "DEBUG ERR MSG:\\n" + error.replaceAll("\\'", "\\\\'")));
            }
        }

        @Keep
        @JavascriptInterface
        public String call(String methodName, String argStr) {
            String error = "Js bridge  called, but can't find a corresponded " +
                    "JavascriptInterface object , please check your code!";
            String[] nameStr = parseNamespace(methodName.trim());
            methodName = nameStr[1];
            Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);
            JSONObject ret = new JSONObject();
            try {
                ret.put("code", -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsb == null) {
                PrintDebugInfo(error);
                return ret.toString();
            }
            Object arg = null;
            Method method = null;
            String callback = null;

            try {
                JSONObject args = new JSONObject(argStr);
                if (args.has("_dscbstub")) {
                    callback = args.getString("_dscbstub");
                }
                if (args.has("data")) {
                    arg = args.get("data");
                }
            } catch (JSONException e) {
                error = String.format("The argument of \"%s\" must be a JSON object string!", methodName);
                PrintDebugInfo(error);
                e.printStackTrace();
                return ret.toString();
            }


            Class<?> cls = jsb.getClass();
            boolean asyn = false;
            try {
                method = cls.getMethod(methodName,
                        new Class[]{Object.class, CompletionHandler.class});
                asyn = true;
            } catch (Exception e) {
                try {
                    method = cls.getMethod(methodName, new Class[]{Object.class});
                } catch (Exception ex) {

                }
            }

            if (method == null) {
                error = "Not find method \"" + methodName + "\" implementation! please check if the  signature or namespace of the method is right ";
                PrintDebugInfo(error);
                return ret.toString();
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                if (annotation == null) {
                    error = "Method " + methodName + " is not invoked, since  " +
                            "it is not declared with JavascriptInterface annotation! ";
                    PrintDebugInfo(error);
                    return ret.toString();
                }
            }

            Object retData;
            method.setAccessible(true);
            try {
                if (asyn) {
                    final String cb = callback;
                    method.invoke(jsb, arg, new wendu.dsbridge.CompletionHandler() {

                        @Override
                        public void complete(Object retValue) {
                            complete(retValue, true);
                        }

                        @Override
                        public void complete() {
                            complete(null, true);
                        }

                        @Override
                        public void setProgressData(Object value) {
                            complete(value, false);
                        }

                        private void complete(Object retValue, boolean complete) {
                            try {
                                JSONObject ret = new JSONObject();
                                ret.put("code", 0);
                                ret.put("data", retValue);
                                //retValue = URLEncoder.encode(ret.toString(), "UTF-8").replaceAll("\\+", "%20");
                                if (cb != null) {
                                    //String script = String.format("%s(JSON.parse(decodeURIComponent(\"%s\")).data);", cb, retValue);
                                    String script = String.format("%s(%s.data);", cb, ret.toString());
                                    if (complete) {
                                        script += "delete window." + cb;
                                    }
                                    //Log.d(LOG_TAG, "complete " + script);
                                    evaluateJavascript(script);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    retData = method.invoke(jsb, arg);
                    ret.put("code", 0);
                    ret.put("data", retData);
                    return ret.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                error = String.format("Call failed：The parameter of \"%s\" in Java is invalid.", methodName);
                PrintDebugInfo(error);
                return ret.toString();
            }
            return ret.toString();
        }

    }

    Map<Integer, OnReturnValue> handlerMap = new HashMap<>();

    public interface JavascriptCloseWindowListener {
        /**
         * @return If true, close the current activity, otherwise, do nothing.
         */
        boolean onClose();
    }


    @Deprecated
    public interface FileChooser {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        void openFileChooser(ValueCallback valueCallback, String acceptType);

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        void openFileChooser(ValueCallback<Uri> valueCallback,
                             String acceptType, String capture);
    }

    public DWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DWebView(Context context) {
        super(context);
        init();
    }

    /**
     * Set debug mode. if in debug mode, some errors will be prompted by a dialog
     * and the exception caused by the native handlers will not be captured.
     *
     * @param enabled
     */
    public static void setWebContentsDebuggingEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(enabled);
        }
        isDebug = enabled;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init() {
        isProxy = BaseApplication.instance().getOnAppBaseProxyLinsener().onIsProxy();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Constant.h5CrtStr.getBytes());
        sslParams = HttpsUtils.getSslSocketFactory(byteArrayInputStream);
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder().connectTimeout(Constant.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constant.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constant.TIMEOUT, TimeUnit.SECONDS).proxy(Proxy.NO_PROXY)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);//证书;
        httpClient = mBuilder.build();
        APP_CACHE_DIRNAME = getContext().getFilesDir().getAbsolutePath() + "/webcache";
        WebSettings settings = getSettings();
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setGeolocationEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCachePath(APP_CACHE_DIRNAME);
        settings.setUseWideViewPort(true);
        settings.setDatabaseEnabled(true);
        super.setWebChromeClient(mWebChromeClient);
        setWebViewClient(generateDWebViewClient());
        addInternalJavascriptObject();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            super.addJavascriptInterface(innerJavascriptInterface, BRIDGE_NAME);
        } else {
            // add dsbridge tag in lower android version
            settings.setUserAgentString(settings.getUserAgentString() + " _dsbridge");
        }
    }

    private DWebViewClient generateDWebViewClient() {
        return new DWebViewClient();
    }

    private String[] parseNamespace(String method) {
        int pos = method.lastIndexOf('.');
        String namespace = "";
        if (pos != -1) {
            namespace = method.substring(0, pos);
            method = method.substring(pos + 1);
        }
        return new String[]{namespace, method};
    }

    @Keep
    private void addInternalJavascriptObject() {
        addJavascriptObject(new Object() {

            @Keep
            @JavascriptInterface
            public boolean hasNativeMethod(Object args) throws JSONException {
                JSONObject jsonObject = (JSONObject) args;
                String methodName = jsonObject.getString("name").trim();
                String type = jsonObject.getString("type").trim();
                String[] nameStr = parseNamespace(methodName);
                Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);
                if (jsb != null) {
                    Class<?> cls = jsb.getClass();
                    boolean asyn = false;
                    Method method = null;
                    try {
                        method = cls.getMethod(nameStr[1],
                                new Class[]{Object.class, CompletionHandler.class});
                        asyn = true;
                    } catch (Exception e) {
                        try {
                            method = cls.getMethod(nameStr[1], new Class[]{Object.class});
                        } catch (Exception ex) {

                        }
                    }
                    if (method != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                            if (annotation == null) {
                                return false;
                            }
                        }
                        if ("all".equals(type) || (asyn && "asyn".equals(type) || (!asyn && "syn".equals(type)))) {
                            return true;
                        }

                    }
                }
                return false;
            }

            @Keep
            @JavascriptInterface
            public String closePage(Object object) throws JSONException {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (javascriptCloseWindowListener == null
                                || javascriptCloseWindowListener.onClose()) {
                            Context context = getContext();
                            if (context instanceof Activity) {
                                ((Activity) context).onBackPressed();
                            }
                        }
                    }
                });
                return null;
            }

            @Keep
            @JavascriptInterface
            public void disableJavascriptDialogBlock(Object object) throws JSONException {
                JSONObject jsonObject = (JSONObject) object;
                alertBoxBlock = !jsonObject.getBoolean("disable");
            }

            @Keep
            @JavascriptInterface
            public void dsinit(Object jsonObject) {
                DWebView.this.dispatchStartupQueue();
            }

            @Keep
            @JavascriptInterface
            public void returnValue(final Object obj) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) obj;
                        Object data = null;
                        try {
                            int id = jsonObject.getInt("id");
                            boolean isCompleted = jsonObject.getBoolean("complete");
                            OnReturnValue handler = handlerMap.get(id);
                            if (jsonObject.has("data")) {
                                data = jsonObject.get("data");
                            }
                            if (handler != null) {
                                handler.onValue(data);
                                if (isCompleted) {
                                    handlerMap.remove(id);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }, "_dsb");
    }

    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DWebView.super.evaluateJavascript(script, null);
        } else {
            super.loadUrl("javascript:" + script);
        }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param script
     */
    public void evaluateJavascript(final String script) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                _evaluateJavascript(script);
            }
        });
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     */
    @Override
    public void loadUrl(final String url) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (url != null && url.startsWith("javascript:")) {
                    DWebView.super.loadUrl(url);
                } else {
                    callInfoList = new ArrayList<>();
                    DWebView.super.loadUrl(url);
                }
            }
        });
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     * @param additionalHttpHeaders
     */
    @Override
    public void loadUrl(final String url, final Map<String, String> additionalHttpHeaders) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (url != null && url.startsWith("javascript:")) {
                    DWebView.super.loadUrl(url, additionalHttpHeaders);
                } else {
                    callInfoList = new ArrayList<>();
                    DWebView.super.loadUrl(url, additionalHttpHeaders);
                }
            }
        });
    }

    @Override
    public void reload() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callInfoList = new ArrayList<>();
                DWebView.super.reload();
            }
        });
    }

    /**
     * set a listener for javascript closing the current activity.
     */
    public void setJavascriptCloseWindowListener(JavascriptCloseWindowListener listener) {
        javascriptCloseWindowListener = listener;
    }


    private static class CallInfo {
        private String data;
        private int callbackId;
        private String method;

        CallInfo(String handlerName, int id, Object[] args) {
            if (args == null) args = new Object[0];
            data = new JSONArray(Arrays.asList(args)).toString();
            callbackId = id;
            method = handlerName;
        }

        @Override
        public String toString() {
            JSONObject jo = new JSONObject();
            try {
                jo.put("method", method);
                jo.put("callbackId", callbackId);
                jo.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jo.toString();
        }
    }

    private synchronized void dispatchStartupQueue() {
        if (callInfoList != null) {
            for (CallInfo info : callInfoList) {
                dispatchJavascriptCall(info);
            }
            callInfoList = null;
        }
    }

    private void dispatchJavascriptCall(CallInfo info) {
        evaluateJavascript(String.format("window._handleMessageFromNative(%s)", info.toString()));
    }

    public synchronized <T> void callHandler(String method, Object[] args, final OnReturnValue<T> handler) {

        CallInfo callInfo = new CallInfo(method, ++callID, args);
        if (handler != null) {
            handlerMap.put(callInfo.callbackId, handler);
        }

        if (callInfoList != null) {
            callInfoList.add(callInfo);
        } else {
            dispatchJavascriptCall(callInfo);
        }

    }

    public void callHandler(String method, Object[] args) {
        callHandler(method, args, null);
    }

    public <T> void callHandler(String method, OnReturnValue<T> handler) {
        callHandler(method, null, handler);
    }


    /**
     * Test whether the handler exist in javascript
     *
     * @param handlerName
     * @param existCallback
     */
    public void hasJavascriptMethod(String handlerName, OnReturnValue<Boolean> existCallback) {
        callHandler("_hasJavascriptMethod", new Object[]{handlerName}, existCallback);
    }

    /**
     * Add a java object which implemented the javascript interfaces to dsBridge with namespace.
     * Remove the object using {@link #removeJavascriptObject(String) removeJavascriptObject(String)}
     *
     * @param object
     * @param namespace if empty, the object have no namespace.
     */
    public void addJavascriptObject(Object object, String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        if (object != null) {
            javaScriptNamespaceInterfaces.put(namespace, object);
        }
    }

    /**
     * remove the javascript object with supplied namespace.
     *
     * @param namespace
     */
    public void removeJavascriptObject(String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        javaScriptNamespaceInterfaces.remove(namespace);

    }


    public void disableJavascriptDialogBlock(boolean disable) {
        alertBoxBlock = !disable;
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        webChromeClient = client;
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (webViewUrlChangeListener != null) {
                webViewUrlChangeListener.loadProgress(newProgress);
            }
            if (webChromeClient != null) {
                webChromeClient.onProgressChanged(view, newProgress);
            } else {
                super.onProgressChanged(view, newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (webChromeClient != null) {
                webChromeClient.onReceivedTitle(view, title);
            } else {
                super.onReceivedTitle(view, title);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (webChromeClient != null) {
                webChromeClient.onReceivedIcon(view, icon);
            } else {
                super.onReceivedIcon(view, icon);
            }
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            if (webChromeClient != null) {
                webChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
            } else {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (webChromeClient != null) {
                webChromeClient.onShowCustomView(view, callback);
            } else {
                super.onShowCustomView(view, callback);
            }
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void onShowCustomView(View view, int requestedOrientation,
                                     CustomViewCallback callback) {
            if (webChromeClient != null) {
                webChromeClient.onShowCustomView(view, requestedOrientation, callback);
            } else {
                super.onShowCustomView(view, requestedOrientation, callback);
            }
        }

        @Override
        public void onHideCustomView() {
            if (webChromeClient != null) {
                webChromeClient.onHideCustomView();
            } else {
                super.onHideCustomView();
            }
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            if (webChromeClient != null) {
                return webChromeClient.onCreateWindow(view, isDialog,
                        isUserGesture, resultMsg);
            }
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public void onRequestFocus(WebView view) {
            if (webChromeClient != null) {
                webChromeClient.onRequestFocus(view);
            } else {
                super.onRequestFocus(view);
            }
        }

        @Override
        public void onCloseWindow(WebView window) {
            if (webChromeClient != null) {
                webChromeClient.onCloseWindow(window);
            } else {
                super.onCloseWindow(window);
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
            if (!alertBoxBlock) {
                result.confirm();
            }
            if (webChromeClient != null) {
                if (webChromeClient.onJsAlert(view, url, message, result)) {
                    return true;
                }
            }
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setMessage(message).
                    setCancelable(false).
                    setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (alertBoxBlock) {
                                result.confirm();
                            }
                        }
                    })
                    .create();
            alertDialog.show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            if (!alertBoxBlock) {
                result.confirm();
            }
            if (webChromeClient != null && webChromeClient.onJsConfirm(view, url, message, result)) {
                return true;
            } else {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertBoxBlock) {
                            if (which == Dialog.BUTTON_POSITIVE) {
                                result.confirm();
                            } else {
                                result.cancel();
                            }
                        }
                    }
                };
                new AlertDialog.Builder(getContext())
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, listener).show();
                return true;

            }

        }

        @Override
        public boolean onJsPrompt(WebView view, String url, final String message,
                                  String defaultValue, final JsPromptResult result) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                String prefix = "_dsbridge=";
                if (message.startsWith(prefix)) {
                    result.confirm(innerJavascriptInterface.call(message.substring(prefix.length()), defaultValue));
                    return true;
                }
            }

            if (!alertBoxBlock) {
                result.confirm();
            }

            if (webChromeClient != null && webChromeClient.onJsPrompt(view, url, message, defaultValue, result)) {
                return true;
            } else {
                final EditText editText = new EditText(getContext());
                editText.setText(defaultValue);
                if (defaultValue != null) {
                    editText.setSelection(defaultValue.length());
                }
                float dpi = getContext().getResources().getDisplayMetrics().density;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertBoxBlock) {
                            if (which == Dialog.BUTTON_POSITIVE) {
                                result.confirm(editText.getText().toString());
                            } else {
                                result.cancel();
                            }
                        }
                    }
                };
                new AlertDialog.Builder(getContext())
                        .setTitle(message)
                        .setView(editText)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, listener)
                        .show();
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int t = (int) (dpi * 16);
                layoutParams.setMargins(t, 0, t, 0);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                editText.setLayoutParams(layoutParams);
                int padding = (int) (15 * dpi);
                editText.setPadding(padding - (int) (5 * dpi), padding, padding, padding);
                return true;
            }

        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            if (webChromeClient != null) {
                return webChromeClient.onJsBeforeUnload(view, url, message, result);
            }
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
                                            long estimatedDatabaseSize,
                                            long totalQuota,
                                            WebStorage.QuotaUpdater quotaUpdater) {
            if (webChromeClient != null) {
                webChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater);
            } else {
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater);
            }
        }

        @Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            if (webChromeClient != null) {
                webChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, true);
            if (webChromeClient != null) {
                webChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
            } else {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            if (webChromeClient != null) {
                webChromeClient.onGeolocationPermissionsHidePrompt();
            } else {
                super.onGeolocationPermissionsHidePrompt();
            }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onPermissionRequest(PermissionRequest request) {
            if (webChromeClient != null) {
                webChromeClient.onPermissionRequest(request);
            } else {
                super.onPermissionRequest(request);
            }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            if (webChromeClient != null) {
                webChromeClient.onPermissionRequestCanceled(request);
            } else {
                super.onPermissionRequestCanceled(request);
            }
        }

        @Override
        public boolean onJsTimeout() {
            if (webChromeClient != null) {
                return webChromeClient.onJsTimeout();
            }
            return super.onJsTimeout();
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            if (webChromeClient != null) {
                webChromeClient.onConsoleMessage(message, lineNumber, sourceID);
            } else {
                super.onConsoleMessage(message, lineNumber, sourceID);
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (webChromeClient != null) {
                return webChromeClient.onConsoleMessage(consoleMessage);
            }
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public Bitmap getDefaultVideoPoster() {

            if (webChromeClient != null) {
                return webChromeClient.getDefaultVideoPoster();
            }
            return super.getDefaultVideoPoster();
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (webChromeClient != null) {
                return webChromeClient.getVideoLoadingProgressView();
            }
            return super.getVideoLoadingProgressView();
        }

        @Override
        public void getVisitedHistory(ValueCallback<String[]> callback) {
            if (webChromeClient != null) {
                webChromeClient.getVisitedHistory(callback);
            } else {
                super.getVisitedHistory(callback);
            }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            if (webChromeClient != null) {
                return webChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }


        @Keep
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            if (webChromeClient instanceof FileChooser) {
                ((FileChooser) webChromeClient).openFileChooser(valueCallback, acceptType);
            }
        }


        @Keep
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void openFileChooser(ValueCallback<Uri> valueCallback,
                                    String acceptType, String capture) {
            if (webChromeClient instanceof FileChooser) {
                ((FileChooser) webChromeClient).openFileChooser(valueCallback, acceptType, capture);
            }
        }

    };

    public void setWebViewUrlChangeListener(WebViewUrlChangeListener webViewUrlChangeListener) {
        this.webViewUrlChangeListener = webViewUrlChangeListener;
    }

    @Override
    public void clearCache(boolean includeDiskFiles) {
        super.clearCache(includeDiskFiles);
        CookieManager.getInstance().removeAllCookie();
        Context context = getContext();
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        File appCacheDir = new File(APP_CACHE_DIRNAME);
        File webviewCacheDir = new File(context.getCacheDir()
                .getAbsolutePath() + "/webviewCache");

        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }

        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e("Webview", "delete file no exists " + file.getAbsolutePath());
        }
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
            return;
        }
        mainHandler.post(runnable);
    }

    class DWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String tempUrl = url;
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (url.endsWith("apk")) {
                StringUtil.INSTANCE.copyString(url);
                AppUtil.INSTANCE.goBrowser(url);
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (webViewUrlChangeListener != null) {
                webViewUrlChangeListener.onPageFinsh();
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (webViewUrlChangeListener != null) {
                webViewUrlChangeListener.changedUrl(view.getUrl());
            }
            super.onLoadResource(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //6.0以下执行
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return;
            }
            if (webViewUrlChangeListener != null) {
                webViewUrlChangeListener.onPageError();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                if (webViewUrlChangeListener != null) {
                    webViewUrlChangeListener.onPageError();
                }
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            // 这个方法在 android 6.0才出现
            int statusCode = errorResponse.getStatusCode();
            if (404 == statusCode || 500 == statusCode) {
                if (webViewUrlChangeListener != null) {
                    if (!view.getUrl().startsWith(PPT)) {
                        webViewUrlChangeListener.onPageError();
                    }
                }
            }
        }

//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            handler.proceed();
//        }


//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
//            Log.i("keey", "err:" + error.getCertificate());
//            if (error.getPrimaryError() == SslError.SSL_INVALID) {
//                if (SSLCertUtil.isSSLCertOk(error.getCertificate(), Constant.h5CrtStr)) {
//                    handler.proceed();
//                } else {
//                    ToastUtils.INSTANCE.showErrorToast("发现风险请检查网络");
//                }
//            } else {
//                handler.cancel();
//            }
//        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (isProxy) {//如果允许抓包
                return super.shouldInterceptRequest(view, request);
            }
            String url = request.getUrl().toString();
//            return getNewResponse(url, request);
            return checkSsl(view, request);
        }


    }

    private WebResourceResponse checkSsl(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        String urlPath = uri.toString();
        if (urlPath.startsWith("http://") || !urlPath.startsWith("https://app.jinilife.com/") || request.getUrl().toString().contains("/activity/attend")) {
            return null;
        }

        URLConnection urlConnection = null;
        try {
            URL url = null;
            try {
                url = new URL(urlPath);
                urlConnection = url.openConnection();
                if (urlConnection instanceof HttpsURLConnection) {
                    Map<String, String> headers = request.getRequestHeaders();
                    Set<String> keySet = headers.keySet();
                    for (String key : keySet) {
                        urlConnection.setRequestProperty(key, headers.get(key));
                    }
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
                    httpsURLConnection.setInstanceFollowRedirects(false);
                    httpsURLConnection.setSSLSocketFactory(sslParams.sSLSocketFactory);
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                    int respCode = httpsURLConnection.getResponseCode();
                    if (respCode == 301 || respCode == 302) {
                        httpsURLConnection.disconnect();
                        return null;
                    }
                    if (respCode == 400) {
                        httpsURLConnection.disconnect();
                        return new WebResourceResponse(null, null, null);
                    }
                    if (respCode != 200) {
                        httpsURLConnection.disconnect();
                        return null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            InputStream is = urlConnection.getInputStream();
            String contentType = urlConnection.getContentType();
            String encoding = urlConnection.getContentEncoding();
            if (contentType != null) {
                String mimeType = contentType;
                if (contentType.contains(";")) {
                    mimeType = Arrays.asList(contentType.split(";")).get(0).trim();
                }
                return new WebResourceResponse(mimeType, encoding, is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (urlConnection != null) {
            if (urlConnection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) urlConnection).disconnect();
            } else if (urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).disconnect();
            }
        }
        return new WebResourceResponse(null, null, null);
    }

    public interface WebViewUrlChangeListener {
        void changedUrl(String url);

        void onPageFinsh();

        void loadProgress(int progress);

        void onPageError();

    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();

    }

    private WebResourceResponse getNewResponse(String url, WebResourceRequest mWebResourceRequest) {
        if (url.startsWith("http://") || !url.startsWith("https://app.jinilife.com/") || mWebResourceRequest.getUrl().toString().contains("/activity/attend")) {
            return null;
        }
        try {

            Request.Builder builder = new Request.Builder()
                    .url(url.trim());
            Map<String, String> headers = mWebResourceRequest.getRequestHeaders();
            Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                builder.addHeader(key, headers.get(key));
            }
            Request request = builder.method(mWebResourceRequest.getMethod(), null).build();
            final Response response = httpClient.newCall(request).execute();
            Log.i("DWebView", "code:" + response.code());
            if (response.code() == 200) {
                String conentType = response.header("Content-Type", response.body().contentType().type());
                String temp = conentType.toLowerCase();
                if (temp.contains("charset=utf-8")) {
                    conentType = conentType.replaceAll("(?i)" + "charset=utf-8", "");//不区分大小写的替换
                }
                if (conentType.contains(";")) {
                    conentType = conentType.replaceAll(";", "");
                    conentType = conentType.trim();
                }
                return new WebResourceResponse(
                        conentType,
                        response.header("Content-Encoding", "utf-8"),
                        response.body().byteStream()
                );
            } else {
                return new WebResourceResponse(null, null, null);
            }


        } catch (Exception e) {
            return null;
        }

    }
}
