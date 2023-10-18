package com.urjalabs.smartcontroller.util;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.urjalabs.smartcontroller.WifiModule;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerView extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    ZXingScannerView scannerView;
    private String ssid;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void handleResult(Result rawResult) {
        JSONObject result = convertToJSON(rawResult.getText());
        if(result!=null){
            try {
                ssid = result.getString("ssid");
                password = result.getString("password");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ssid!=null && password!=null)
            WifiModule.connectowifi(ssid,password,getApplicationContext());

    }

    public static JSONObject convertToJSON(String obj){
        try {
            return new JSONObject(obj );
        }catch (Exception ex) {
            Log.e("exception ::: ", ex.toString());
        }
        return null;
    }
}