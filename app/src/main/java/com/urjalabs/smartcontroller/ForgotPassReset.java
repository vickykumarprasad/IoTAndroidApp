package com.urjalabs.smartcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.urjalabs.smartcontroller.storage.DBManager;
import com.urjalabs.smartcontroller.storage.UserDBDAO;

public class ForgotPassReset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_reset);
        Bundle extra =  getIntent().getExtras();
        String email = extra.getString("email");
        Button reset = (Button) findViewById(R.id.mailbutton2);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passText = (EditText) findViewById(R.id.passmail);
                EditText confirmPassText = (EditText) findViewById(R.id.passmail2);
                String pass =  passText.getText().toString();
                String cnf = confirmPassText.getText().toString();
                if (!pass.equals(cnf)){
                    Toast.makeText(ForgotPassReset.this, "Password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isPasswordValid(pass)) {

                    Toast.makeText(ForgotPassReset.this, "Password too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                DBManager dbManager = new DBManager(ForgotPassReset.this.getApplicationContext());
                UserDBDAO userDBDAO = dbManager.getUserDBDAO();
                dbManager.open();
                int updates = userDBDAO.updatePassword(email,pass);
                if (updates<=0){
                    Toast.makeText(ForgotPassReset.this, "Update Failed! please try again", Toast.LENGTH_SHORT).show();
                    dbManager.close();
                    return;
                }
                dbManager.close();
                Toast.makeText(ForgotPassReset.this, "Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassReset.this.getApplicationContext(),LoginActivity.class);
                ForgotPassReset.this.startActivity(intent);


            }
        });
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}