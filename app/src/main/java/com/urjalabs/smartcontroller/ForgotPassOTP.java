package com.urjalabs.smartcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.urjalabs.smartcontroller.storage.DBManager;
import com.urjalabs.smartcontroller.storage.OTPDBDAO;

public class ForgotPassOTP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_otp);
        Bundle extra = getIntent().getExtras();
        String email = extra.getString("email");
        Button verify = (Button) findViewById(R.id.otpverify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText otpText = (EditText)ForgotPassOTP.this.findViewById(R.id.otptext);
                int otpFromText = Integer.parseInt(otpText.getText().toString());
                DBManager dbManager = new DBManager(ForgotPassOTP.this.getApplicationContext());
                OTPDBDAO otpdbdao = dbManager.getOtpdbdao();
                dbManager.open();
                if (!otpdbdao.verifyOTP(otpFromText,email)){
                    Toast.makeText(ForgotPassOTP.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                    dbManager.close();
                    return;
                }
                otpdbdao.deleteOtp(email);
                dbManager.close();
                Intent intent = new Intent(ForgotPassOTP.this.getApplicationContext(),ForgotPassReset.class);
                intent.putExtra("email",email);
                ForgotPassOTP.this.startActivity(intent);
            }
        });

    }
}