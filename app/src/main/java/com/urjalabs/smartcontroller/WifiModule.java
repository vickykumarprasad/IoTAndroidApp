package com.urjalabs.smartcontroller;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import 	androidx.annotation.NonNull;
import 	androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.urjalabs.smartcontroller.util.CustomWebViewActivity;// ???
import com.urjalabs.smartcontroller.util.ScannerView;
import java.util.ArrayList;
import java.util.List;

public class WifiModule extends AppCompatActivity  {

    private WifiManager wifiManager;
    private Spinner listView;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<ScanResult> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private Button btnConnect;
    private ScanResult Ssid = null;
    private Button scanbtn;
    public static TextView scantext;
    private static WifiModule current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_module);
        current = this;
        scanbtn=(Button) findViewById(R.id.scanQr);
         Log.v("Scan",""+scanbtn.toString());
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ScannerView.class));
            }
        });
        buttonScan = findViewById(R.id.ScanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               scanWifi();

            }
        });
        //QR Scanner

        btnConnect = findViewById(R.id.buttonConnect);
        btnConnect.setEnabled(true);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((EditText) findViewById(R.id.editTextTextPassword2)).getText().toString();
                 connectowifi(Ssid.SSID,password,getApplicationContext());

            }
        });

        listView = findViewById(R.id.list);
        listView.setEnabled(false);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
            Toast.makeText(this, "WiFi is Enabled ", Toast.LENGTH_SHORT).show();
        }
        adapter = new WifiArrayAdaptor(this, arrayList);
        listView.setAdapter(adapter);
        //
        //show selected wifinetwork
        //
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = position;
                Ssid = arrayList.get(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        scanWifi();
    }

    private void scanWifi() {
        arrayList.clear();
        listView.setEnabled(false);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                arrayList.add(scanResult);
                adapter.notifyDataSetChanged();
            }
            listView.setEnabled(true);
        }

    };

     public static void connectowifi(String scanResult, String password, Context context) {
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
        builder.setSsid(scanResult);
        builder.setWpa2Passphrase(password);

        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
        NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
        networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
        NetworkRequest networkRequest = networkRequestBuilder.build();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            cm.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {

                    super.onAvailable(network);
                    cm.bindProcessToNetwork(network);
                    current.startActivity(new Intent(context, CustomWebViewActivity.class));

                }
            });
        }

        }
}