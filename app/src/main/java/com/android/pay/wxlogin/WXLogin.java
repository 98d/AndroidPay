package com.android.pay.wxlogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXLogin {

    /**
     * 微信appId
     */
    public final String appId;
    public static String APP_ID;

    /**
     * 上下文对象
     */
    public final Context context;

    /**
     * 微信Secret
     */
    public final String appSecret;
    public static String APP_SECRET;

    /**
     * 登录监听
     */
    public final OnWXLoginListener listener;
    private LoginReceiver loginReceiver;
    public static final String WX_LOGIN_ACTION = "com_android_pay_wx_login";


    /**
     * 微信登录构造函数
     *
     * @param builder 构造器
     */
    public WXLogin(Builder builder) {
        this.context = builder.context;
        this.appId = builder.appId;
        this.appSecret = builder.appSecret;
        this.listener = builder.listener;
        WXLogin.APP_ID = appId;
        WXLogin.APP_SECRET = appSecret;
        if (listener != null && context != null && loginReceiver == null) {
            loginReceiver = new LoginReceiver();
            IntentFilter filter = new IntentFilter(WX_LOGIN_ACTION);
            context.registerReceiver(loginReceiver, filter);
        }
        login(appId);
    }

    public class Builder {


        /**
         * 上下文对象
         */
        private Context context;

        /**
         * 微信Secret
         */
        private String appSecret;

        /**
         * 微信appId
         */
        private String appId;

        private OnWXLoginListener listener;

        public Builder(Context context){
            this.context = context;

        }

        /**
         * 获取上下文对象
         *
         * @return Context
         */
        public Context context() {
            return context;
        }


        /**
         * 获取微信appSecret
         *
         * @return String
         */
        public String appSecret() {
            return appSecret;
        }

        /**
         * 设置微信appSecret
         *
         * @return Builder
         */
        public Builder appSecret(String appSecret) {
            this.appSecret = appSecret;
            return this;
        }

        /**
         * 获取微信AppId
         *
         * @return
         */
        public String appId() {
            return appId;
        }

        /**
         * 设置微信AppId
         *
         * @param appId
         * @return
         */
        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        /**
         * 登录监听
         *
         * @return
         */
        public OnWXLoginListener listener() {
            return listener;
        }

        /**
         * 设置登录监听
         *
         * @param listener
         * @return
         */
        public Builder listener(OnWXLoginListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 构建登录对象
         *
         * @return WxLogin
         */
        public WXLogin build() {
            return new WXLogin(this);
        }
    }

    /**
     * 登录
     *
     * @param appId
     */
    private void login(String appId) {
        WXLogin.APP_ID = appId;
        IWXAPI wxAPI = WXAPIFactory.createWXAPI(context, appId, true);
        wxAPI.registerApp(appId);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = String.valueOf(System.currentTimeMillis());
        wxAPI.sendReq(req);
    }

    private class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            WXUser user = (WXUser) intent.getSerializableExtra("WxUser");
            if (listener != null) {
                listener.onWXLogin(user);
            }
            if (context != null && loginReceiver != null) {
                context.unregisterReceiver(loginReceiver);
            }
        }
    }

}