package com.liyuan.youga.marrysecretary;


import org.json.JSONObject;

import com.liyuan.youga.marrysecretary.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PayActivity extends Activity {

    private IWXAPI api;
    Button appayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        api = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1");

        appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
                new WeiXinAsyncTask().execute(url);
            }
        });
        Button checkPayBtn = (Button) findViewById(R.id.check_pay_btn);
        checkPayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                Toast.makeText(PayActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class WeiXinAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            appayBtn.setEnabled(false);
            Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                byte[] buf = Util.httpGet(params[0]);
                if (buf != null && buf.length > 0) {
                    String content = new String(buf);
                    Log.e("get server pay params:", content);
                    JSONObject json = new JSONObject(content);
                    if (!json.has("retcode")) {
                        PayReq req = new PayReq();
                        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                        req.appId = json.getString("appid");
                        req.partnerId = json.getString("partnerid");
                        req.prepayId = json.getString("prepayid");
                        req.nonceStr = json.getString("noncestr");
                        req.timeStamp = json.getString("timestamp");
                        req.packageValue = json.getString("package");
                        req.sign = json.getString("sign");
                        req.extData = "app data"; // optional
                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                        api.sendReq(req);
                        return "正常调起支付";
                    } else {
                        Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                        return "返回错误" + json.getString("retmsg");
                    }
                } else {
                    Log.d("PAY_GET", "服务器请求错误");
                    return "服务器请求错误";
                }
            } catch (Exception e) {
                Log.e("PAY_GET", "异常：" + e.getMessage());
                return "异常：" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            appayBtn.setEnabled(true);

            PayReq request = new PayReq();
            request.appId = Constants.APP_ID;
            request.partnerId = "1900000109";
            request.prepayId = "1101000000140415649af9fc314aa427";
            request.packageValue = "Sign=WXPay";
            request.nonceStr = "1101000000140429eb40476f8896f4c9";
            request.timeStamp = "1398746574";
            request.sign = "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
            api.sendReq(request);

            Toast.makeText(PayActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }
}
