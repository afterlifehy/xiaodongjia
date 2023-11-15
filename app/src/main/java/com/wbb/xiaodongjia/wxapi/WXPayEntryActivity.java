package com.wbb.xiaodongjia.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wbb.base.help.WxSharManager;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WxSharManager.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            String result = "";
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = "成功";
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    result = "错误";
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = "取消";
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = "被拒";
                    break;
                default:
                    result = "未知";
                    break;
            }
        }
    }
}