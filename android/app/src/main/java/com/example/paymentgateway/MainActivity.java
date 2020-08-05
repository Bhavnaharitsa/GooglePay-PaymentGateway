package com.example.paymentgateway;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FlutterActivity {

    public static final String GPAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    private static final String CHANNEL = "flutter.native/helper";

    //    EditText name, upiId, amount, note;
//    TextView msg;
//    Button pay;
    Uri uri;
    String approvalRefNo;
    private String TAG = this.getClass().getSimpleName();

    public static String payerName, UpiId, msgNote, sendAmount, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        GeneratedPluginRegistrant.registerWith(getFlutterEngine());
        new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
                new MethodChannel.MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                        if (call.method.equals("pay")) {
                            String greetings = pay();
                            result.success(greetings);
                        }
                    }
                });

        //initialising default value


    }

    private String pay() {
        Log.d(TAG, "pay: " + "Method called!");
        payerName = "Jashaswee Jena";
        UpiId = "7008306123@ybl";
        msgNote = "donation";
        sendAmount = "10";
        boolean success = false;
        if (!payerName.equals("") && !UpiId.equals("") && !msgNote.equals("") && !sendAmount.equals("")) {
            uri = getUpiPaymentUri(payerName, UpiId, msgNote, sendAmount);
            payWithGpay(GPAY_PACKAGE_NAME);
            success = true;
        } else {
            Toast.makeText(MainActivity.this, "Fill all above details and try again.", Toast.LENGTH_SHORT).show();
        }

        if (success) {
            Log.d(TAG, "pay: " + "Payment Success");
            return "Payment Success!";
        } else {
            Log.d(TAG, "pay: " + "Fill all above details and try again.");
            return "Oopsie! Try again";
        }


    }

    private static Uri getUpiPaymentUri(String name, String upiId, String note, String amount) {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //  .appendQueryParameter("url", "your-transaction-url")
                .build();
    }

    private void payWithGpay(String packageName) {
        if (isAppInstalled(this, packageName)) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            // intent.setPackage(packageName);
            startActivityForResult(intent, 0);

        } else {
            Toast.makeText(MainActivity.this, "Google pay is not installed. Please istall and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
            approvalRefNo = data.getStringExtra("txnRef");
        }
        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(MainActivity.this, "Transaction successful. " + approvalRefNo, Toast.LENGTH_SHORT).show();
            //msg.setText("Transaction successful of ₹" + sendAmount);
            //msg.setTextColor(Color.GREEN);

        } else {
            Toast.makeText(MainActivity.this, "Transaction cancelled or failed please try again.", Toast.LENGTH_SHORT).show();
            //msg.setText("Transaction Failed of ₹" + sendAmount);
            //msg.setTextColor(Color.RED);
        }

    }


    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private String helloFromNativeCode() {
        return "Hello from Native Android Code";
    }

}
