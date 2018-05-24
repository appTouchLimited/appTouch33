package uk.co.apptouch.apptouch30;


import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;


import android.widget.Button;


import com.braintreepayments.api.BraintreeFragment;


public class FragmentItemThree extends Fragment {


    View myView;
    private String mAuthorization;
    private BraintreeFragment mBraintreeFragment;
    private Button pay;

    public static FragmentItemThree newInstance() {
        FragmentItemThree fragment = new FragmentItemThree();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_item_three, container, false);


        pay = myView.findViewById(R.id.payBtn);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetTokenTask().execute();
            }
        });

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(getActivity(), "sandbox_yzv5vq45_zf39nyvdzr5y8666");
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        return myView;
    }





    public void doTransaction(String token) {
        mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
            @Override
            public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                Log.i("TAG", "payment method nounce");
            }
        });
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        Log.i("TAG", "enabled " + dropInRequest.isPayPalEnabled());
        dropInRequest.amount("22.22");
        dropInRequest.collectDeviceData(false);
        dropInRequest.requestThreeDSecureVerification(true);
        dropInRequest.tokenizationKey(token);
        dropInRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
        startActivityForResult(dropInRequest.getIntent(getActivity()), 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentMethodNonce = result.getPaymentMethodNonce().getNonce();
                // send paymentMethodNonce to your server
                new DoTransaction().execute(paymentMethodNonce, "22.22");

                Log.i("paymentMethodNonce", paymentMethodNonce);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // canceled
            } else {
                // an error occurred, checked the returned exception
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.i("exception", exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            return getToken();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                doTransaction(jsonObject.getString("token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private String getToken() {
            URL url;
            HttpURLConnection urlConnection = null;
            String response = "";
            try {
                url = new URL(String.format("%spayments/token", AppGlobals.BASE_URL));

                urlConnection = (HttpURLConnection) url
                        .openConnection();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    response = readStream(urlConnection.getInputStream());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response;
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private class DoTransaction extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {
            return writeData(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }
    }

    private String writeData(String nounce, String amount) {
        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            url = new URL(String.format("%spayments/pay", AppGlobals.BASE_URL));

            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("payment_method_nonce", nounce)
                    .appendQueryParameter("amount", amount);
            String data = builder.build().getEncodedQuery();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                response = readStream(urlConnection.getInputStream());
            }
            os.close();
            urlConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }


}



