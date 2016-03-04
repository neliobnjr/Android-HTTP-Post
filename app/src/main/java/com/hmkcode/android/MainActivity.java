package com.hmkcode.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONObject;

import android.os.Build;
import android.os.Build.VERSION;
import android.widget.Toast;
import android.content.Context;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    public static final String	BOARD = Build.BOARD;
    public static final String	BOOTLOADER = Build.BOOTLOADER;
    public static final String	BRAND	= Build.BRAND;
    //public static final String	CPU_ABI = Build.CPU_ABI;
    //public static final String	CPU_ABI2 = Build.CPU_ABI2;
    public static final String	DEVICE	= Build.DEVICE;
    public static final String	DISPLAY	= Build.DISPLAY;
    public static final String	FINGERPRINT	= Build.FINGERPRINT;
    public static final String	HARDWARE = Build.HARDWARE;
    public static final String	HOST = Build.HOST;
    public static final String	ID = Build.ID;
    public static final String	MANUFACTURER = Build.MANUFACTURER;
    public static final String	MODEL = Build.MODEL;
    public static final String	PRODUCT = Build.PRODUCT;
    /*public static final String	RADIO = Build.getRadioVersion();*/
    public static final String	SERIAL = Build.SERIAL;
   /* public static final String[]	SUPPORTED_32_BIT_ABIS = Build.SUPPORTED_32_BIT_ABIS;
    public static final String[]	SUPPORTED_64_BIT_ABIS = Build.SUPPORTED_64_BIT_ABIS;
    public static final String[]	SUPPORTED_ABIS	= Build.SUPPORTED_ABIS;*/
    public static final String	TAGS = Build.TAGS;
    public static final long	TIME = Build.TIME;
    public static final String	TYPE = Build.TYPE;
    public static final String	USER = Build.USER;

    TextView tvIsConnected;
    EditText etName,etCountry,etTwitter;
    Button btnPost;

    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etName = (EditText) findViewById(R.id.etName);
        etCountry = (EditText) findViewById(R.id.etCountry);
        etTwitter = (EditText) findViewById(R.id.etTwitter);
        btnPost = (Button) findViewById(R.id.btnPost);

        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are connected");
        }
        else{
            tvIsConnected.setText("You are NOT connected");
        }

        // add click listener to Button "POST"
        btnPost.setOnClickListener(this);
    }

    public static String POST(String url, Person person){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("appId", person.getName());
            jsonObject.accumulate("userName", person.getCountry());
            jsonObject.accumulate("appOnlineFlag", person.getTwitter());

            jsonObject.accumulate("board", BOARD);
             jsonObject.accumulate("bootloader", BOOTLOADER);
            jsonObject.accumulate("brand", BRAND);
            jsonObject.accumulate("device", DEVICE);
            jsonObject.accumulate("display", DISPLAY);
            jsonObject.accumulate("fingerprint", FINGERPRINT);
            jsonObject.accumulate("hardware", HARDWARE);
            jsonObject.accumulate("host", HOST);
            jsonObject.accumulate("id", ID);
            jsonObject.accumulate("manufacturer", MANUFACTURER);
            jsonObject.accumulate("model", MODEL);
            jsonObject.accumulate("product", PRODUCT);
            jsonObject.accumulate("serial", SERIAL);
            jsonObject.accumulate("tags", TAGS);
            jsonObject.accumulate("time", TIME);
            jsonObject.accumulate("type", TYPE);

           // jsonObject.accumulate("user", USER);


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onClick(View view) {

        person = new Person();
        person.setName(etName.getText().toString());
        person.setCountry(etCountry.getText().toString());
        person.setTwitter(etTwitter.getText().toString());

        switch(view.getId()){
            case R.id.btnPost:
               /* if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();*/
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://192.168.1.34:8080/targetapp/rest/mobileapp/addandroidmobiledata/");

                //new HttpAsyncTask().execute("http://requestb.in/11yd96i1");

                break;
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {



            return POST(urls[0],person);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    /*private boolean validate(){
        if(etName.getText().toString().trim().equals(""))
            return false;
        else if(etCountry.getText().toString().trim().equals(""))
            return false;
        else if(etTwitter.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }*/

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
