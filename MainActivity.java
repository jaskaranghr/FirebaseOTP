package com.example.firebaseotp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvHeader;

    private EditText etMobileNumber;

    private Button btnSendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHeader = findViewById(R.id.tvHeader);

        etMobileNumber = findViewById(R.id.etMobileNumber);

        btnSendOTP = findViewById(R.id.btnSendOTP);

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mobileNumber = etMobileNumber.getText().toString();

                if (mobileNumber.isEmpty() || mobileNumber.length()<10 ) {

                    etMobileNumber.setError("Invalid Mobile Number");

                    etMobileNumber.requestFocus();

                    return;

                }

                Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);

                intent.putExtra("mobileNumber", mobileNumber);

                startActivity(intent);

            }
        });

    }
}
