package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CheckInAndReschedule extends AppCompatActivity implements View.OnClickListener {

    private Button checkInButton;
    private Button rescheduleButton;

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

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkInButton) {
            Intent intent = new Intent(this, ProceedToCounter.class);
            startActivity(intent);

        } else if (v.getId() == R.id.rescheduleButton) {
            Intent intent = new Intent(this, QueueSubmission.class);
            startActivity(intent);

        }
    }
}