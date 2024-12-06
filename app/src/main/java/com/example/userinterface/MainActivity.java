package com.example.userinterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button signupButton;
    private Button loginButton;

    private EditText username, login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signupButton = (Button) findViewById(R.id.signupButton);
        signupButton.setOnClickListener(this);
        loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(this);

        username = (EditText) findViewById(R.id.username);
        login_password = (EditText) findViewById(R.id.login_password);

    }


    private void login() {

        handleSSLHandshake();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConfig.BASE_URL + "login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("ResponseLogs", response);
                            JSONObject json_response = new JSONObject(response);
                            String status = json_response.getString("status");
                            if (status.equals("success")) {
                                Session.user_email = username.getText().toString();
                                Intent intent = new Intent(MainActivity.this, JoinQueue.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON Parsing", response);
                            Toast.makeText(MainActivity.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error + "", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("username", username.getText().toString());
                params.put("password", login_password.getText().toString());

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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signupButton) {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        } else if (v.getId() == R.id.LoginButton) {
            login();
        }
    }
}