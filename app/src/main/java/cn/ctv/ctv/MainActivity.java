package cn.ctv.ctv;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.dj.ctv.R;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity {
    private String download_url;
    private String mac = Common.getMac();
    private String web_url = "http://10.0.1.124:8080/villageserver_android/";
    private WebView webView;
    private String v_url;
    Handler mHandler = new Handler();
    Runnable runDownload = new Runnable() {
        @Override
        public void run() {
            if (download_url != null) {
                try {
                    updateVersion(download_url);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
    }

    /**
     * 检查版本
     */
    private void checkVersion() {

        int version_code = Common.getVersionCode(this);
        String urlString = Common.getApiUrl(this)
                + "";
        HttpUtil.get(urlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        webView.clearCache(true);
        webView.clearHistory();
        mac = Common.getMac();
        if (web_url != null && mac != null) {
            webView.loadUrl(web_url + "?mac=" + mac);
        }
    }

    /**
     * 初始化webview
     */
    private void init() {
        webView = (WebView) findViewById(R.id.wvContent);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebChromeClient() {
            /**
             * 显示自定义视图，无此方法视频不能播放
             */
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
            }
            /*
             * private void onPageFinished(WebView view, String url) { // TODO
			 * Auto-generated method stub webView.loadUrl(
			 * "javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}}"
			 * ); }
			 */
        });
        webView.getSettings().setBlockNetworkImage(false);// 解决图片不显示
        // 在js中调用本地java方法
        webView.addJavascriptInterface(new JsInterface(), "nativeMethod");
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();
        System.out.println(mWebBackForwardList.getSize());
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.clearCache(true);

            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.goBack();

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
        } else {
            finish();
        }
        return false;
    }

    private class JsInterface {
        private Context mContext;

        @JavascriptInterface
        public void toActivity(final String n1, final String n2) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (n1 != null && n2 != null) {
                        Log.e("111", n1 + n2);
                        Intent mintent = new Intent();
                        mintent.setComponent(new ComponentName(n1, n2));
                        startActivity(mintent);
                    }
                }
            });
        }

        // 在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
        @JavascriptInterface
        public void showInfoFromJs(final String url) {

            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String video_url = url;

                    Intent intent = new Intent(MainActivity.this,
                            PlayerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("playUrl", video_url);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            MainActivity.this.v_url = url;
        }
    }


    public File updateVersion(String httpUrl) {
        System.out.println("downLoadFile>>>" + httpUrl);
        final String fileName = "com.dj.ctv.apk";
        String dirPath = "/sdcard/capk/";
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File oldFile = new File(dirPath + fileName);
        if (oldFile.exists()) {
            oldFile.delete();
        }
        final File file = new File(dirPath + fileName);
        try {
            URL url = new URL(httpUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                if (null == conn) {
                    return null;
                }
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                double count = 0;
                if (conn.getResponseCode() >= 400) {
                    // 连接超时
                } else {
                    while (count <= 100) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                            }
                        } else {
                            break;
                        }
                    }
                }
                conn.disconnect();
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
        return file;
    }
}
