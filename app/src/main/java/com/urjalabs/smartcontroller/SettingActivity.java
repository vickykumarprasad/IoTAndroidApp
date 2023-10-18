package com.urjalabs.smartcontroller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import 	androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.urjalabs.smartcontroller.models.MqttServerProperties;
import com.urjalabs.smartcontroller.mqtt.MQTTConnection;
import com.urjalabs.smartcontroller.mqtt.MqttConnectionListener;
import com.urjalabs.smartcontroller.storage.MQTTServerPropertySharedPrefDAOImpl;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, MqttConnectionListener {

    private static final String TAG = "SettingActivity";
    private EditText mPrimaryServerText;
    private EditText mSecondaryServerText;
    private EditText mPortNoEditText;
    private EditText mUserEditText;
    private EditText mPasswordEditText;
    private Button mConnect;
    private Button mDisconnect;
    private TextView mStatusTextView;
    private Switch mSwitchAuthReq;
    private MQTTConnection mqttConnection;
    private MQTTServerPropertySharedPrefDAOImpl mServerPropertyDAO;
    private String mPrimaryServer;
    private View mProgressView;
    private View mSettingFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
        mServerPropertyDAO = new MQTTServerPropertySharedPrefDAOImpl(SettingActivity.this);
        mConnect.setOnClickListener(this);
        mDisconnect.setOnClickListener(this);
        //setting preference to used in activity
        mPrimaryServer = SmartControllerUtil.getAppPreference(this).getString(Constants.MQTT_SERVER_PRIMARY, Constants.DEFAULT);
        mStatusTextView.setText(getResources().getString(R.string.mqtt_status_holder, Constants.DISCONNECTED));
        if (!mPrimaryServer.equals(Constants.DEFAULT)) {
            createMqttConnection();
        }
        if (mqttConnection != null && mqttConnection.isConnected()) {
            mStatusTextView.setText(getResources().getString(R.string.mqtt_status_holder, Constants.CONNECTED + " to " + mqttConnection.getServer()));
        }
        mSwitchAuthReq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mUserEditText.setVisibility(View.VISIBLE);
                    mPasswordEditText.setVisibility(View.VISIBLE);
                } else {
                    mUserEditText.setVisibility(View.GONE);
                    mPasswordEditText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initializeView() {
        mPrimaryServerText = findViewById(R.id.input_server_primary);
        mSecondaryServerText = findViewById(R.id.input_server_second);
        mPortNoEditText = findViewById(R.id.input_port_no);
        mUserEditText = findViewById(R.id.input_username);
        mPasswordEditText = findViewById(R.id.input_password);
        mStatusTextView = findViewById(R.id.mqtt_status);
        mConnect = findViewById(R.id.btn_connect);
        mDisconnect = findViewById(R.id.btn_disconnect);
        mSwitchAuthReq = findViewById(R.id.switch_auth_required);
        String primarySer = SmartControllerUtil.getAppPreference(this).getString(Constants.MQTT_SERVER_PRIMARY, Constants.DEFAULT);
        String secServer = SmartControllerUtil.getAppPreference(this).getString(Constants.MQTT_SERVER_SECONDARY, Constants.DEFAULT);
        int portNo = SmartControllerUtil.getAppPreference(this).getInt(Constants.MQTT_PORT, 1883);
        if (!primarySer.equals(Constants.DEFAULT)) {
            mPrimaryServerText.setText(primarySer);
        }
        if (!secServer.equals(Constants.DEFAULT)) {
            mSecondaryServerText.setText(secServer);
        }
        if (portNo == 1883) {
            mPortNoEditText.setText(String.valueOf(portNo));
        }
        mProgressView = findViewById(R.id.connect_progress);
        mSettingFormView = findViewById(R.id.setting_page);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_disconnect:
                showProgress(true);
                disconnect();
                break;
        }
    }

    private void connect() {
        String port = mPortNoEditText.getText().toString();
        int portNo = -1;
        if (!port.isEmpty()) {
            portNo = Integer.parseInt(port);
        }
        MqttServerProperties serverProperties = new MqttServerProperties(mPrimaryServerText.getText().toString()
                , mSecondaryServerText.getText().toString(), portNo);
        serverProperties.setAuthReq(mSwitchAuthReq.isChecked());
        serverProperties.setUserName(mUserEditText.getText().toString());
        serverProperties.setPassword(mPasswordEditText.getText().toString());
        if (validate(serverProperties)) {
            //only first time
            if (mPrimaryServer.equals(Constants.DEFAULT)) {
                saveMqttConnectionDetails(serverProperties);
                createMqttConnection();
            } else {
                if (isReconnectRequired(serverProperties)) {
                    Log.v(TAG, "Reconnect Required");
                    saveMqttConnectionDetails(serverProperties);
                    showProgress(true);
                    mqttConnection.reconnect(SettingActivity.this);
                } else {
                    Log.v(TAG, "Reconnect Not Required");
                    if (!mqttConnection.isConnected()) {
                        mqttConnection.reconnect(SettingActivity.this);
                    }else{
                        mStatusTextView.setText(getResources().getString(R.string.mqtt_status_holder, Constants.CONNECTED + " to " + mqttConnection.getServer()));
                    }
                }
            }
        }
    }

    private boolean isReconnectRequired(MqttServerProperties serverProperties) {
        MqttServerProperties savedProperties = mServerPropertyDAO.getServerProperties();
        if (!savedProperties.getPrimaryServer().equals(Constants.DEFAULT)
                && !savedProperties.getPrimaryServer().equals(serverProperties.getPrimaryServer())) {
            Log.i(TAG,"primary server changed ");
            return true;
        }
        if (!savedProperties.getSecondaryServer().equals(Constants.DEFAULT)
                && !savedProperties.getSecondaryServer().equals(serverProperties.getSecondaryServer())) {
            Log.i(TAG,"secondary server changed ");
            return true;
        }
        if (savedProperties.getAuthReq() != serverProperties.getAuthReq()) {
            Log.i(TAG,"get auth req  changed ");
            return true;
        }
        if (!savedProperties.getUserName().equals(Constants.DEFAULT)
                && !savedProperties.getUserName().equals(serverProperties.getUserName())) {
            Log.i(TAG,"username  changed ");
            return true;
        }
        if (!savedProperties.getPassword().equals(Constants.DEFAULT)
                && !savedProperties.getPassword().equals(serverProperties.getPassword())) {
            Log.i(TAG,"password  changed ");
            return true;
        }
        return false;
    }

    private void saveMqttConnectionDetails(MqttServerProperties serverProperties) {
        mServerPropertyDAO.addServerProperties(serverProperties);
    }

    private boolean validate(MqttServerProperties serverProperties) {
        boolean valid = true;


        if (serverProperties.getPrimaryServer().isEmpty() || serverProperties.getPrimaryServer().length() < 3) {
            mPrimaryServerText.setError("at least 3 characters");
            valid = false;
        } else {
            mPrimaryServerText.setError(null);
        }

        if (serverProperties.getPortNo() == -1) {
            mPortNoEditText.setError("port no is required");
            valid = false;
        } else {
            mPortNoEditText.setError(null);
        }

        if (serverProperties.getAuthReq()) {
            if (serverProperties.getUserName().isEmpty()) {
                mUserEditText.setError("Username is required");
                valid = false;
            } else {
                mUserEditText.setError(null);
            }
            if (serverProperties.getPassword().isEmpty()) {
                mPasswordEditText.setError("Password is required");
                valid = false;
            } else {
                mPasswordEditText.setError(null);
            }
        }
        return valid;
    }

    @Override
    public void onConnectionAttemptResponse(String response) {
        Log.i("Setting", "response at ui" + response);
        mStatusTextView.setText(getResources().getString(R.string.mqtt_status_holder, response));
        showProgress(false);
    }

    @Override
    public void onDisconnectionAttempt(String response) {
        mStatusTextView.setText(getResources().getString(R.string.mqtt_status_holder, response));
        showProgress(false);
        if (mqttConnection.isConnected()){
            mStatusTextView.setText(getResources().getString(R.string.mqtt_status_holder, Constants.CONNECTED + " to " + mqttConnection.getServer()));
        }
    }

    private void createMqttConnection() {
        showProgress(true);
        mqttConnection = MQTTConnection.getInstance(SettingActivity.this, new ArrayList<String>());
        mqttConnection.addConnectionListener(this);
        if(mqttConnection.isConnected())
        showProgress(false);
    }

    private void disconnect() {
        if (mqttConnection != null) {
            mqttConnection.disconnect();
        }
    }
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSettingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mSettingFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSettingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
