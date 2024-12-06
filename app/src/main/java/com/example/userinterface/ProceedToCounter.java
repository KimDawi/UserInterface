package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProceedToCounter extends AppCompatActivity implements View.OnClickListener {


    private Button feedbackButton;
    private TextView ticketNumber;
    private TextView counterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_proceed_to_counter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        feedbackButton = (Button) findViewById(R.id.feedbackButton);
        feedbackButton.setOnClickListener(this);
        ticketNumber = (TextView) findViewById(R.id.ticketNumber);
        ticketNumber.setText(Session.queue_number);
        counterInfo = (TextView) findViewById(R.id.counterInfo);
        counterInfo.setText("PROCEED TO COUNTER: " + Session.counter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.feedbackButton) {
            Intent intent = new Intent(this, FeedbackSubmission.class);
            startActivity(intent);

        }
    }
}