package com.interjoy.camer2application.view.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.ta.utdid2.android.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.App;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.beans.ActionBean;
import com.wiseweb.watermelon.base.beans.AndroidToWebView;
import com.wiseweb.watermelon.base.beans.ShareInfo;
import com.wiseweb.watermelon.base.beans.ShareInfoH5;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.base.event.ActivityScansNumber;
import com.wiseweb.watermelon.base.view.widget.SharePopupWindow;
import com.wiseweb.watermelon.manager.ActionManager;
import com.wiseweb.watermelon.manager.LoginManager;
import com.wiseweb.watermelon.user.bean.User;
import com.wiseweb.watermelon.utils.GsonUtil;
import com.wiseweb.watermelon.utils.LogUtil;
import com.wiseweb.watermelon.utils.ToastUtil;
import com.wiseweb.watermelon.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import butterknife.BindView;

/**
 * Created by wenwei on 2016/8/8.
 */
public class WebViewsActivity extends BaseCompatActivity implements UMShareListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private String webUrl;
    @BindView(R.id.web_view_activity_detail)
    public BridgeWebView webView;
    @BindView(R.id.web_view_activity_share)
    public ImageView imageShare;
    @BindView(R.id.web_view_activity_back)
    public ImageView activityBack;
    @BindView(R.id.web_view_activity_close)
    public ImageView activityClose;
    @BindView(R.id.progress_bar_web)
    public ProgressBar mProgressBar;

    private int RESULT_CODE = 0;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessages;
    private User user;
    private SharePopupWindow sharePopupWindow;

    private int activityBackWidth, activityCloseWidth;

    private Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                default:
                    if (mProgressBar != null) {
                        int visibility = mProgressBar.getVisibility();
                        if (msg.arg1 < 100) {
                            mProgressBar.setProgress(msg.arg1);
                            if (visibility != View.VISIBLE)
                                mProgressBar.setVisibility(View.VISIBLE);
                        } else {
                            if (visibility == View.VISIBLE)
                                mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        activityBack.measure(widthSpec, heightSpec);
        activityClose.measure(widthSpec, heightSpec);

        activityBackWidth = activityBack.getMeasuredWidth();
        activityCloseWidth = activityClose.getMeasuredWidth();
    }

    private void setTitleLocation(String text) {

        TextPaint textPaint = tvTitle.getPaint();
        float textPaintWidth = textPaint.measureText(text);

        int centerPosition = AppUtil.getScreenWidth(this) / 2;//屏幕横向中点位置
        int paddingLeft = (int) ((centerPosition - activityBackWidth - activityCloseWidth) - (textPaintWidth / 2));
        if (paddingLeft > 0) {
            tvTitle.setPadding(paddingLeft, 0, 0, 0);
        } else {
            tvTitle.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    protected void initListeners() {

        activityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                }
            }
        });

        activityClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null) {
                    finish();
                }
            }
        });

        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(WebViewsActivity.this, getResources().getString(R.string.actdetail_share));
                if (sharePopupWindow == null) {
                    sharePopupWindow = new SharePopupWindow(R.layout.popuwindow_activity_page, WebViewsActivity.this) {
                        @Override
                        public void shareOnClick(SHARE_MEDIA shareMedia) {
                            //分享点击的回调
                            if (mShareAction == null) return;
                            if (mShareInfo != null && mShareInfo.getShareDescWeiBo() != null) {
                                mShareAction.withText(mShareInfo.getShareDescWeiBo());
                            }
                            mShareAction.setPlatform(shareMedia).share();
                        }
                    };
                }
                sharePopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                if (!sharePopupWindow.isShowing()) {
                    sharePopupWindow.showAtLocation(getWindow().getDecorView().getRootView(),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }

            }
        });


        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (webView != null && keyCode == KeyEvent.KEYCODE_BACK
                            && webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        RelativeLayout rlRetry = (RelativeLayout) findViewById(R.id.rl_retry);
        rlRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null) {
                    webView.reload();
                    if (webView.getVisibility() != View.VISIBLE)
                        webView.setVisibility(View.VISIBLE);
                    hideServiceError();
                }
            }
        });
    }

    @Override
    protected void initData() {
        initWebView();
        String webViewUrl = getIntent().getStringExtra("url");
        if (webViewUrl == null) {
            return;
        }
        webUrl = initURL(webViewUrl);
//        webView.loadUrl("http://114.215.102.185:8080/resources/h5/ExampleApp.html");
//        if (isLogin()) {
//            syncCookie(webUrl, "token=" + LoginManager.getLoginManager(WebViewsActivity.this).getUser().getToken());
//        }
        webView.loadUrl(webUrl);
//        webView.loadUrl("file:///android_asset/ExampleApp.html");
        webView.callHandler("shareInfo", "Message From Android", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                LogUtil.d(data);
                if (data != null) {
                    if (imageShare != null) {
                        imageShare.setVisibility(View.VISIBLE);
                    }
                    ShareInfoH5 shareInfoH5 = null;
                    try {
                        shareInfoH5 = GsonUtil.GsonToBean(data, ShareInfoH5.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageShare.setVisibility(View.INVISIBLE);
                        return;

                    }
                    String shareTitle = shareInfoH5.getShare_title();
                    String shareImage = shareInfoH5.getShare_img_url();
                    String shareBigImage = shareInfoH5.getShare_big_img_url();
                    String shareSub = shareInfoH5.getShare_desc();
                    String shareSubWeiBo = shareInfoH5.getShare_desc_weibo();
                    String shareUrl = shareInfoH5.getApp_link();

                    ShareInfo shareInfo = new ShareInfo();

                    if (shareTitle != null) shareInfo.setShareTitle(shareTitle);
                    if (shareSub != null) shareInfo.setShareSub(shareSub);
                    if (shareUrl != null) shareInfo.setShareUrl(shareUrl);
                    if (shareImage != null && !shareImage.equals("")) {
                        shareInfo.setShareImg(shareImage);
                    }
                    if (shareBigImage != null && !shareBigImage.equals(""))
                        shareInfo.setShareBigImage(shareBigImage);
                    if (shareSubWeiBo != null && !shareSubWeiBo.equals("")) {
                        shareInfo.setShareDescWeiBo(shareSubWeiBo);
                    }
                    mShareAction = getShareAction(shareInfo);
                }
            }
        });
        webView.callHandler("activityView", "Message From Android", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                ActivityScansNumber activityScansNumber = GsonUtil.GsonToBean(data, ActivityScansNumber.class);
                EventBus.getDefault().post(activityScansNumber);

            }
        });

        LogUtil.d("加载后的" + webUrl);

    }

    /**
     * 将cookie同步到WebView
     *
     * @param url    WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
//    public boolean syncCookie(String url, String cookie) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            CookieSyncManager.createInstance(WebViewsActivity.this);
//        }
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
//        String newCookie = cookieManager.getCookie(url);
//        return TextUtils.isEmpty(newCookie) ? false : true;
//    }

    /**
     * URL拼接userId和token
     */
    private String initURL(String url) {
        String tempUrl = initUrlCommentPara(url);
        user = LoginManager.getLoginManager(WebViewsActivity.this).getUser();
        if (user != null) {
            String userId = user.getUser_id();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(tempUrl)
                    .append("&user_id=")
                    .append(userId);

            return stringBuilder.toString();

        } else {
            LogUtil.d("未登录");
            return tempUrl;
        }
    }

    private String initUrlCommentPara(String url) {

        if (url.contains("device_id")) {
            return url;
        }
        SparseArray<String> sparseArray = App.sSparseArray;
        StringBuilder stringBuild = new StringBuilder();
        String deviceId = "?device_id=";
        if (url.contains("?")) {
            deviceId = "&device_id=";
        }
        stringBuild.append(url)
                .append(deviceId)
                .append(sparseArray.get(Constant.SDEVICEID))
                .append("&version=")
                .append(sparseArray.get(Constant.SVER))
                .append("&os=")
                .append(sparseArray.get(Constant.SOS))
                .append("&device_type=")
                .append(sparseArray.get(Constant.SDEVICETYPE)).
                append("&secret=").
                append(Util.getSecret());
        return stringBuild.toString();
    }


    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
//            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
//        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setBlockNetworkImage(false);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setDefaultHandler(new DefaultHandler());
        webView.setWebViewClient(new BridgeWebViewClient(webView) {

                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         LogUtil.d(url + "shouldOverrideUrlLoading");

                                         if (url.contains("js-action")) {
                                             try {
                                                 url = URLDecoder.decode(url, "utf-8");
                                             } catch (UnsupportedEncodingException e) {
                                                 e.printStackTrace();
                                             }
                                             url = url.subSequence(10, url.length()).toString();
                                             ActionBean actionType = GsonUtil.GsonToBean1(url, ActionBean.class);
                                             ActionManager.getActionManager().startAction(actionType, WebViewsActivity.this);
                                             return true;
                                         } else if (url.startsWith("intent://")) {
                                             try {
                                                 Context context = view.getContext();
                                                 Intent intent = new Intent().parseUri(url, Intent.URI_INTENT_SCHEME);

                                                 if (intent != null) {
                                                     view.stopLoading();

                                                     PackageManager packageManager = context.getPackageManager();
                                                     ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                                     if (info != null) {
                                                         context.startActivity(intent);
                                                     } else {
                                                         String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                                         view.loadUrl(fallbackUrl);
                                                     }

                                                     return true;
                                                 }
                                             } catch (URISyntaxException e) {
                                                 e.printStackTrace();
                                             }
                                             return true;
                                         } else {
                                             return super.shouldOverrideUrlLoading(view, url);
                                         }
                                     }

                                     @Override
                                     public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                         LogUtil.d("onReceivedErrors");
                                         switch (errorCode) {
                                             case -2:
                                                 hideAll();
                                                 showNetWorkError();
                                                 break;
                                             default:
                                                 hideLoding();
                                                 showServiceError();
                                         }

                                     }

//                                     @TargetApi(23)
//                                     @Override
//                                     public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                                         LogUtil.d("onReceivedErrors23");
//                                         switch (error.getErrorCode()) {
//                                             case -2:
//                                                 hideAll();
//                                                 showNetWorkError();
//                                                 break;
//                                             default:
//                                                 hideLoding();
//                                                 showServiceError();
//                                                 break;
//                                         }

//                                     }

                                     @Override
                                     public void onPageFinished(WebView view, String url) {
//                                         webView.setVisibility(View.VISIBLE);
                                         super.onPageFinished(view, url);
                                     }

                                 }
        );
        webView.setWebChromeClient(new WebChromeClient() {
                                       @Override
                                       public void onProgressChanged(WebView view, int newProgress) {
                                           super.onProgressChanged(view, newProgress);
                                           LogUtil.d("onProgressChanged");
                                           Message msg = Message.obtain();
                                           msg.arg1 = newProgress;
                                           sHandler.sendMessage(msg);
                                       }

                                       @Override
                                       public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]>
                                               filePathCallback, FileChooserParams fileChooserParams) {
                                           mUploadMessages = filePathCallback;
                                           pickFile();
                                           return true;
                                       }

                                       @Override
                                       public void onReceivedTitle(WebView view, String title) {
                                           super.onReceivedTitle(view, title);
                                           if (title != null) {
                                               if (title.equals("活动")) {
                                                   return;
                                               }
                                               if (title.contains("watermelon")) {
                                                   title = "详情";
                                               }

                                               tvTitle.setText(title);
                                               setTitleLocation(title);
                                           }
                                       }

                                       @SuppressWarnings("unused")
                                       public void openFileChooser(ValueCallback<Uri> uploadMsg, String
                                               AcceptType, String
                                                                           capture) {
                                           this.openFileChooser(uploadMsg, AcceptType);
                                       }

                                       @SuppressWarnings("unused")
                                       public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                                           mUploadMessage = uploadMsg;
                                           pickFile();
                                       }

                                       @SuppressWarnings("unused")
                                       public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                                           this.openFileChooser(uploadMsg, "");
                                       }

                                   }

        );

        //Register a Java handler function so that js can call
        webView.registerHandler("getAndroidSystem", new

                BridgeHandler() {
                    @Override
                    public void handler(String data, CallBackFunction function) {
                        Log.i("111", "handler = submitFromWeb, data from web = " + data);
                        function.onCallBack(Build.VERSION.SDK_INT + "");
                    }
                }
        );


        webView.registerHandler("getAndroidKey", new

                BridgeHandler() {
                    @Override
                    public void handler(String data, CallBackFunction function) {
                        Log.i("111", "handler = submitFromWeb, data from web = " + data);
                        String response = Util.getSecret();
                        AndroidToWebView androidToWebView = new AndroidToWebView();
                        androidToWebView.setSecret(response.trim());
                        if (isLogin()) {
                            androidToWebView.setToken(LoginManager.getLoginManager(WebViewsActivity.this).getUser().getToken());
                        } else {
                            androidToWebView.setToken("");
                        }
                        function.onCallBack(GsonUtil.GsonString(androidToWebView).toString().trim());
                        LogUtil.d(GsonUtil.GsonString(androidToWebView).toString().trim());
                    }
                }
        );
    }

    private void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null == mUploadMessages) {
                    return;
                }
                Uri[] results = null;
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
                mUploadMessages.onReceiveValue(results);
                mUploadMessages = null;
            } else {
                if (null == mUploadMessage) {
                    return;
                }

                Uri result = data == null ? null : data.getData();
                LogUtil.d(result.toString());
                String imagePath = Util.getPath(WebViewsActivity.this, result);
                LogUtil.d(imagePath + "路径");
                if (!TextUtils.isEmpty(imagePath)) {
                    result = Uri.parse("file:///" + imagePath);
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else if (requestCode == RESULT_CODE && resultCode == RESULT_CANCELED) {
            finish();
            Intent intent = new Intent(this, WebViewsActivity.class);
            intent.putExtra("url", webUrl);
            startActivity(intent);
        }

        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(Constant.PUBLISH_TOPIC_OK);
        sHandler.removeCallbacksAndMessages(null);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        destoryWebView();
        System.gc();
        super.onDestroy();
    }


    @Subscribe
    public void onEvent(String e) {
        if (e.equals("login_success")) {
            if (webView != null) {
                Intent webViewsActivity = new Intent(this,
                        WebViewsActivity.class);
                webViewsActivity.putExtra("url", webUrl);
                startActivity(webViewsActivity);
                finish();

            }

        } else if (e.equals(Constant.SINA_TRANSMIT_OK)) {
            webView.reload();
        } else if (e.equals(Constant.PUBLISH_TOPIC_OK)) {
            webView.reload();
        }
        if (e.equals(Constant.CLOSE_WEBVIEW)) {
            finish();
        }

    }

    //清空webview
    private void destoryWebView() {
        if (webView != null) {
            LogUtil.d("清空webView");
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.stopLoading();
            webView.clearHistory();
            webView.clearCache(true);
            webView.loadUrl("about:blank"); // clearView()
            webView.destroy();
            webView = null;
        }
    }


    private ShareAction mShareAction;
    private ShareInfo mShareInfo;
    private UMImage umImage;

    public ShareAction getShareAction(ShareInfo shareInfo) {

        //TODO getShareAction();
        if (shareInfo != null) {
            mShareInfo = shareInfo;
            String title = "";
            String sub = "";
            String url = "";
            this.mShareAction = new ShareAction(this);
            if (!StringUtils.isEmpty(shareInfo.getShareImg())) {
                umImage = new UMImage(WebViewsActivity.this, shareInfo.getShareImg());
            } else {
                umImage = new UMImage(WebViewsActivity.this, R.mipmap.ic_launcher);
            }
            this.mShareAction.withMedia(umImage);
            if (shareInfo.getShareTitle() != null) {
                title = shareInfo.getShareTitle();
                this.mShareAction.withTitle(title);
            }
            if (shareInfo.getShareSub() != null) {
                sub = shareInfo.getShareSub();
                this.mShareAction.withText(sub);
            }
            if (shareInfo.getShareUrl() != null) {
                url = shareInfo.getShareUrl();
                this.mShareAction.withTargetUrl(url);
            }
            if (title.equals("") && sub.equals("") && url.equals("")) {
                if (imageShare != null) {
                    imageShare.setVisibility(View.INVISIBLE);
                }
                return null;
            } else {
                this.mShareAction.setCallback(this);
                return this.mShareAction;
            }
        } else
            return null;
    }


    @Override
    public void onResult(SHARE_MEDIA share_media) {
        LogUtil.d("success,platform", share_media + "");
        ToastUtil.showShort("分享成功~");

    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        ToastUtil.showShort("分享失败~");
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        ToastUtil.showShort("取消分享~");

    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (webView != null) {
            webView.onResume();
        }
        super.onResume();
    }


}



