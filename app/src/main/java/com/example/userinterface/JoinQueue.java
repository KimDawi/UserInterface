package com.example.userinterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class JoinQueue extends AppCompatActivity implements View.OnClickListener {

    private Button join_queue_button;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_queue);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        join_queue_button = findViewById(R.id.join_queue_button);
        join_queue_button.setOnClickListener(this);

        tableLayout = findViewById(R.id.tablequeue);

        // Add rows programmatically
//        addRow(tableLayout, "Counter 1", "Ticket A001");
//        addRow(tableLayout, "Counter 2", "Ticket A002");
//        addRow(tableLayout, "Counter 3", "Ticket A003");

        fetchQueue();
    }

    // Method to add a row to the TableLayout
    private void addRow(TableLayout tableLayout, String counter, String ticket) {
        TableRow tableRow = new TableRow(this);

        // Create Counter TextView
        TextView counterTextView = new TextView(this);
        counterTextView.setText(counter);
        counterTextView.setPadding(8, 8, 8, 8);
        counterTextView.setBackgroundColor(Color.parseColor("#002D69"));
        counterTextView.setGravity(android.view.Gravity.CENTER);

        // Create Ticket TextView
        TextView ticketTextView = new TextView(this);
        ticketTextView.setText(ticket);
        ticketTextView.setPadding(8, 8, 8, 8);
        ticketTextView.setBackgroundColor(Color.parseColor("#002D69"));
        ticketTextView.setGravity(android.view.Gravity.CENTER);

        // Add TextViews to the row
        tableRow.addView(counterTextView);
        tableRow.addView(ticketTextView);

        // Add the row to the TableLayout
        tableLayout.addView(tableRow);
    }


    private void fetchQueue() {

        handleSSLHandshake();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConfig.BASE_URL + "fetch_queue.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("ResponseLogs", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                // Clear existing rows (if any)
                                //tableLayout.removeAllViews();

                                // Extract data array from response
                                for (int i = 0; i < jsonResponse.getJSONArray("data").length(); i++) {
                                    JSONObject queueItem = jsonResponse.getJSONArray("data").getJSONObject(i);

                                    String counter = queueItem.getString("counter");
                                    String ticket = queueItem.getString("queue_number");

                                    // Add rows programmatically
                                    addRow(tableLayout, counter, ticket);
                                }
                            } else {
                                Toast.makeText(JoinQueue.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JoinQueue.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(JoinQueue.this, error + "", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

//                params.put("username", username.getText().toString());


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

    private void joinQueue() {

        handleSSLHandshake();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConfig.BASE_URL + "join_queue.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("ResponseLogs", response);
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                String queueNumber = jsonResponse.getString("queue_number");
                                Session.queue_number = queueNumber;
                                Intent intent = new Intent(JoinQueue.this, CheckInAndReschedule.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(JoinQueue.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JoinQueue.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(JoinQueue.this, error + "", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", Session.user_email);
                params.put("rescheduled", Session.rescheduled);
                return params;
            }
        };
        queue.add(stringRequest);

    }






    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.join_queue_button) {
            joinQueue();


        }
    }
}