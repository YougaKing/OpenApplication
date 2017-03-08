package com.liyuan.youga.marrysecretary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String alipay = "_input_charset=utf-8&body=%E8%B6%8A%E7%9A%93%E5%A9%9A%E5%93%81&notify_url=http%3A%2F%2Ftest.yl.cgsoft.net%2Fxmsapishopb%2FCartshop%2Fnotifyurl%2Ftoken%2F43378e1b35ae7858e82eba2b27ddefd7&out_trade_no=610539783252662465&partner=2088911677351587&payment_type=1&seller_id=2975475158%40qq.com&service=mobile.securitypay.pay&subject=%E7%BB%93%E5%A9%9A%E5%B0%8F%E7%A7%98%E4%B9%A6%E5%95%86%E5%9F%8E%E8%AE%A2%E5%8D%95&total_fee=520&sign=V%2FMTAgMyx62UkuEcGUeEuEBSzRqzxIku1dz8jpD5SpnCEahqSDP%2FncftB87ro%2B4ypbND8sBxLaCpW4sS3A1tzwcRIkDdJXKRI370PPJ%2BVGdt5uPH1E%2BDaY5A0bridETJITOBtPGy2lJrtIoGKpcFXuZFdnmgEY9K1reDi88ma6A%3D&sign_type=RSA";
        pay(alipay);
    }


    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(MainActivity.this);
                // 调用支付接口，获取支付结果
                final String result = alipay.pay(orderInfo, true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        PayResult payResult = new PayResult(result);
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        Log.e("resultInfo", payResult.toString());
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        } else {
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Toast.makeText(MainActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    public class PayResult {
        private String resultStatus;
        private String result;
        private String memo;

        public PayResult(String rawResult) {

            if (TextUtils.isEmpty(rawResult))
                return;

            String[] resultParams = rawResult.split(";");
            for (String resultParam : resultParams) {
                if (resultParam.startsWith("resultStatus")) {
                    resultStatus = gatValue(resultParam, "resultStatus");
                }
                if (resultParam.startsWith("result")) {
                    result = gatValue(resultParam, "result");
                }
                if (resultParam.startsWith("memo")) {
                    memo = gatValue(resultParam, "memo");
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }

        private String gatValue(String content, String key) {
            String prefix = key + "={";
            return content.substring(content.indexOf(prefix) + prefix.length(),
                    content.lastIndexOf("}"));
        }

        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }

        /**
         * @return the memo
         */
        public String getMemo() {
            return memo;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }
}
