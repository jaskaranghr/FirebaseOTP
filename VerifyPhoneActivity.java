package com.example.firebaseotp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private String verificationID;

    private TextView tvMobileNumber;

    private EditText etOTP;

    private Button btnVerify;

    private FirebaseAuth firebaseAuth;

    private String countryCode = "+91";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        firebaseAuth = FirebaseAuth.getInstance();

        tvMobileNumber = findViewById(R.id.tvMobileNumber);

        etOTP = findViewById(R.id.etOTP);

        btnVerify = findViewById(R.id.btnVerify);

        Intent intent = getIntent();

        String mobileNumber = intent.getStringExtra("mobileNumber");

        tvMobileNumber.setText(countryCode + mobileNumber);

        sendVerificationCode(mobileNumber);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = etOTP.getText().toString().trim();

                if (code.isEmpty()) {

                    etOTP.setError("Invalid code");

                    etOTP.requestFocus();

                    return;

                }

                verifyVerificationCode(code);

            }
        });


    }

    private void sendVerificationCode(String mobileNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode + mobileNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {

                etOTP.setText(code);

                verifyVerificationCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationID = s;

        }

    };

    private void verifyVerificationCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
        
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);

                        } else {

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                message = "Invalid code entered";

                               // etOTP.setError(message);
                            }

                            Toast.makeText(VerifyPhoneActivity.this, message,Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }


}
