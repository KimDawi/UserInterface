package com.example.userinterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CheckInAndReschedule extends AppCompatActivity implements View.OnClickListener {

    private Button checkInButton;
    private Button rescheduleButton;
    private TextView ticketNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_in_and_reschedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkInButton = (Button) findViewById(R.id.checkInButton);
        checkInButton.setOnClickListener(this);
        rescheduleButton = (Button) findViewById(R.id.rescheduleButton);
        rescheduleButton.setOnClickListener(this);
        ticketNumber = (TextView) findViewById(R.id.ticketNumber);
        ticketNumber.setText(Session.queue_number);

    }


    private void checkIn() {

        handleSSLHandshake();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConfig.BASE_URL + "check_in.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("ResponseLogs", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                String counter = jsonResponse.getString("counter");
                                Session.counter = counter;
                                Intent intent = new Intent(CheckInAndReschedule.this, ProceedToCounter.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CheckInAndReschedule.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CheckInAndReschedule.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckInAndReschedule.this, error + "", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("queue_number", Session.queue_number);

                return params;
            }
        };
        queue.add(stringRequest);

    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }


    private void reschedule() {

        handleSSLHandshake();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConfig.BASE_URL + "reschedule.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("ResponseLogs", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                Intent intent = new Intent(CheckInAndReschedule.this, JoinQueue.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CheckInAndReschedule.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CheckInAndReschedule.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckInAndReschedule.this, error + "", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("queue_number", Session.queue_number);

                return params;
            }
        };
        queue.add(stringRequest);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkInButton) {
            checkIn();

        } else if (v.getId() == R.id.rescheduleButton) {
            reschedule();

        }
    }
}