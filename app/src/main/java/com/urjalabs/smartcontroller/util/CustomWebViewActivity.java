package com.urjalabs.smartcontroller.util;
import com.urjalabs.smartcontroller.MainActivity;
import com.urjalabs.smartcontroller.R;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

//webview page

public class CustomWebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_web_view);
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return  false; //open in App only
            }
        });
        Button button = (Button)findViewById(R.id.homebtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomWebViewActivity.this.getApplicationContext(), MainActivity.class);
               CustomWebViewActivity.this.startActivity(intent);
            }
        });
        webView.loadUrl("http://192.168.43.1/");
    }
}