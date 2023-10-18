package com.urjalabs.smartcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.urjalabs.smartcontroller.models.OTP;
import com.urjalabs.smartcontroller.models.UserCredential;
import com.urjalabs.smartcontroller.storage.DBManager;
import com.urjalabs.smartcontroller.storage.OTPDBDAO;
import com.urjalabs.smartcontroller.storage.UserDBDAO;

import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ForgotPassEmailCheck extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_email_check);
        Button button = (Button) findViewById(R.id.mailbutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailText = (EditText) findViewById(R.id.editTextTextPersonName);
                String email = emailText.getText().toString();

                boolean userExists = ForgotPassEmailCheck.this.generateOtp(email);
                if (!userExists){
                    return;
                }
                Intent intent = new Intent(ForgotPassEmailCheck.this.getApplicationContext(), ForgotPassOTP.class);
                intent.putExtra("email",email );
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); //ADDITIONAL CODE
                ForgotPassEmailCheck.this.startActivity(intent);
            }
        });

    }

    private boolean generateOtp(String email){
        DBManager dbManager = new DBManager(ForgotPassEmailCheck.this.getApplicationContext());
        OTPDBDAO otpdbdao = dbManager.getOtpdbdao();
        UserDBDAO userDBDAO = dbManager.getUserDBDAO();
        dbManager.open();
        otpdbdao.clean();
        UserCredential userCredential = userDBDAO.getUser(email);
        if (userCredential == null || userCredential.getEmail() == null){
            Toast.makeText(ForgotPassEmailCheck.this, "User does not exist", Toast.LENGTH_SHORT).show();
            dbManager.close();
            return false;
        }
        int otpFromTable = otpdbdao.checkEmail(email); //same email send one more time
        if (otpFromTable!=-1){
            dbManager.close();
            this.sendEmail(otpFromTable,email);
            return true;
        }
        int generatedOtp = (int)(Math.random()*9000)+1000;
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setOtp(generatedOtp);
        otpdbdao.addOTP(otp);
        this.sendEmail(generatedOtp,email);


        dbManager.close();
        return true;
    }
    private void sendEmail (int otp,String email){
        final String username = "testdemourjalab@gmail.com"; //Company mail id that send otp
        final String password = "34ck1Ls6fUTm80zK";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-relay.sendinblue.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from-email@rediffmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Your OTP for Password reset");
            message.setText("OTP : " + otp);
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        //Your code goes here
                        Transport.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
                thread.start();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}