package com.urjalabs.smartcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urjalabs.smartcontroller.adapters.CustomListAdapter;
import com.urjalabs.smartcontroller.models.Device;
import com.urjalabs.smartcontroller.models.Load;
import com.urjalabs.smartcontroller.models.ResponseNode;
import com.urjalabs.smartcontroller.models.Room;
import com.urjalabs.smartcontroller.network.RequestBuilder;
import com.urjalabs.smartcontroller.network.RestClient;
import com.urjalabs.smartcontroller.storage.DBManager;
import com.urjalabs.smartcontroller.storage.DatabaseHelper;
import com.urjalabs.smartcontroller.storage.DeviceDAO;
import com.urjalabs.smartcontroller.storage.HomeConfigurationSharedPrefDAO;
import com.urjalabs.smartcontroller.storage.LoadSwitchDBDAO;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AssociateLoadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AssociateLoadActivity";
    private Spinner mFloorSpinner;
    private Spinner mRoomSpinner;
    private Spinner mDeviceSpinner;
    private Spinner mLoadTypeSpinner;
    private List<String> floorList = new ArrayList<>();
    private List<String> roomList = new ArrayList<>();
    private List<String> deviceList = new ArrayList<>();
    private CustomListAdapter mCustomRoomListAdapter;
    private CustomListAdapter mCustomDeviceListAdapter;
    private String mHomeName;
    private LoadSwitchDBDAO mLoadSwitchDBDAO;
    private HomeConfigurationSharedPrefDAO mHomeConfigurationDAO;
    private EditText mLoadName;
    private EditText mLoadId;
    private DBManager mDBManager;
    private DeviceDAO mDeviceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associate_load);
        initializeView();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //create shared prfrence
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_HOME_PRFRENCE, Context.MODE_PRIVATE);
        mHomeConfigurationDAO = new HomeConfigurationSharedPrefDAO(sharedPreferences);
        mHomeName = mHomeConfigurationDAO.getHomeName();
        mDBManager = new DBManager(this);
        //assign after open
        mLoadSwitchDBDAO = mDBManager.getLoadSwitchDBDAO();
        mDeviceDAO = mDBManager.getDeviceDBDAO();
        mDBManager.open();
        createAllListView();
    }

    private void createAllListView() {
        createFloorListView();
        createRoomListView();
        createDeviceListView();
        createLoadTypeListView();
    }

    private void initializeView() {
        mFloorSpinner = findViewById(R.id.floor_spinner);
        mFloorSpinner.setFocusable(true);
        mFloorSpinner.setFocusableInTouchMode(true);
        mRoomSpinner = findViewById(R.id.room_spinner);
        mRoomSpinner.setFocusable(true);
        mRoomSpinner.setFocusableInTouchMode(true);
        mDeviceSpinner = findViewById(R.id.device_spinner);
        mDeviceSpinner.setFocusable(true);
        mDeviceSpinner.setFocusableInTouchMode(true);
        mLoadTypeSpinner = findViewById(R.id.load_type_spinner);
        mLoadTypeSpinner.setFocusable(true);
        mLoadTypeSpinner.setFocusableInTouchMode(true);
        mLoadName = findViewById(R.id.load_name);
        mLoadId = findViewById(R.id.load_id);
        Button mButtonAddLoad = findViewById(R.id.btn_add_load);
        mButtonAddLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePage()) {
                    if (SmartControllerUtil.isConnected(AssociateLoadActivity.this)) {
                        try {
                            associateLoad();
                        } catch (IOException e) {
                            Log.e(TAG, "error in associating load", e);
                        }
                    } else {
                        SmartControllerUtil.createAlertDialog(AssociateLoadActivity.this, getResources().getString(R.string.warn_alert),
                                getResources().getString(R.string.error_offline)).show();
                    }

                }
            }
        });
        mLoadId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    boolean isExist = mLoadSwitchDBDAO.isValueExistInLoad(DatabaseHelper.KEY_SWITCH_ID, mLoadId.getText().toString());
                    if (isExist) {
                        mLoadId.setError(getResources().getString(R.string.error_switch_id_already_exist));
                    } else {
                        mLoadId.setError(null);
                    }
                }
            }
        });
    }

    private void createFloorListView() {
        int noOfFloor = mHomeConfigurationDAO.getNumberOfFloor();
        floorList.add(Constants.SELECT_FLOOR);
        if (noOfFloor != -1) {
            for (int i = 0; i < noOfFloor; i++) {
                floorList.add("Floor " + i);
            }
        }
        CustomListAdapter mCustomFloorListAdapter = new CustomListAdapter(getApplicationContext(), floorList);
        mFloorSpinner.setAdapter(mCustomFloorListAdapter);
        mFloorSpinner.setOnItemSelectedListener(this);
    }

    private void createRoomListView() {
        roomList.add(Constants.SELECT_ROOM);
        mCustomRoomListAdapter = new CustomListAdapter(getApplicationContext(), roomList);
        mRoomSpinner.setAdapter(mCustomRoomListAdapter);
        mRoomSpinner.setOnItemSelectedListener(this);
    }

    private void createDeviceListView() {
        deviceList.add(Constants.SELECT_DEVICE);
        mCustomDeviceListAdapter = new CustomListAdapter(getApplicationContext(), deviceList);
        mDeviceSpinner.setAdapter(mCustomDeviceListAdapter);
        mDeviceSpinner.setOnItemSelectedListener(this);
    }

    private void createLoadTypeListView() {
        String loadArray[] = getResources().getStringArray(R.array.load_types);
        CustomListAdapter mCustomLoadTypeListAdapter = new CustomListAdapter(getApplicationContext(), Arrays.asList(loadArray));
        mLoadTypeSpinner.setAdapter(mCustomLoadTypeListAdapter);
        mLoadTypeSpinner.setOnItemSelectedListener(this);
    }

    private void updateRoomListView(int floorNo) {
        roomList.clear();
        int numberOfRoom = mHomeConfigurationDAO.getNumberOfRooms(floorNo);
        if (numberOfRoom != -1) {
            roomList.add(Constants.SELECT_ROOM);
            for (int roomNumber = 0; roomNumber < numberOfRoom; roomNumber++) {
                Room room = mHomeConfigurationDAO.getRoomConfig(floorNo, roomNumber);
                if (!SmartControllerUtil.isEmpty(room.getName())
                        && !SmartControllerUtil.isEmpty(room.getType())) {
                    roomList.add(room.getName() + " : " + room.getType());
                }
            }
        } else {
            roomList.add(Constants.SELECT_ROOM);
        }

        mCustomRoomListAdapter.notifyDataSetChanged();
    }

    private void updateDeviceListView(int floorNo, int roomNo) {
        deviceList.clear();
        List<Device> devices = mDeviceDAO.getDevicesInRoom(floorNo, roomNo);
        if (devices.isEmpty()) {
            deviceList.add(Constants.SELECT_DEVICE);
        } else {
            deviceList.add(Constants.SELECT_DEVICE);
            for (Device device : devices) {
                if (!SmartControllerUtil.isEmpty(device.getName())
                        && !SmartControllerUtil.isEmpty(device.getDeviceId())) {
                    deviceList.add(device.getName() + " : " + device.getDeviceId());
                }
            }
        }

        mCustomDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int index, long l) {
        if (index > 0) {
            int id = parent.getId();
            switch (id) {
                case R.id.floor_spinner:
                    updateRoomListView(index - 1);
                    break;
                case R.id.room_spinner:
                    updateDeviceListView((int) mFloorSpinner.getSelectedItemId() - 1, index - 1);
                    break;
                case R.id.device_spinner:
                    // your stuff here
                    break;
                case R.id.load_type_spinner:
                    // your stuff here
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onPause() {
        mDBManager.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mDBManager.open();
        super.onResume();
    }

    private boolean validatePage() {
        boolean valid = true;
        if (mFloorSpinner.getSelectedItemPosition() == 0 || mRoomSpinner.getSelectedItemPosition() == 0
                || mDeviceSpinner.getSelectedItemPosition() == 0 || mLoadTypeSpinner.getSelectedItemPosition() == 0) {
            SmartControllerUtil.createAlertDialog(this, getResources()
                    .getString(R.string.warn_alert), getResources().getString(R.string.warn_select_req_value)).show();
            valid = false;
        }
        if (mLoadId.getText().toString().isEmpty()) {
            mLoadId.setError(getResources().getString(R.string.error_switch_id_req));
            Log.e(TAG, getResources().getString(R.string.error_switch_id_req));
            valid = false;
        } else {
            boolean isExist = mLoadSwitchDBDAO.isValueExistInLoad(DatabaseHelper.KEY_SWITCH_ID, mLoadId.getText().toString());
            if (isExist) {
                mLoadId.setError(getResources().getString(R.string.error_switch_id_already_exist));
                valid = false;
            } else {
                mLoadId.setError(null);
            }
        }
        if (mLoadId.getText().toString().isEmpty() || mLoadName.getText().toString().length() < 3) {
            mLoadName.setError(getResources().getString(R.string.error_name_req));
            valid = false;
        } else {
            mLoadName.setError(null);
        }
        return valid;
    }

    private void associateLoad() throws IOException {
        int floorNo = mFloorSpinner.getSelectedItemPosition() - 1;
        String roomInfo[] = mRoomSpinner.getSelectedItem().toString().split(Constants.COLON);
        String deviceInfo[] = mDeviceSpinner.getSelectedItem().toString().split(Constants.COLON);
        String loadType = mLoadTypeSpinner.getSelectedItem().toString();
        final String name = mLoadName.getText().toString();
        final Load load = new Load();
        load.setName(mLoadName.getText().toString());
        load.setType(loadType);
        load.setSwitchId(Integer.parseInt(mLoadId.getText().toString()));
        String description = SmartControllerUtil.getLoadDescriptionKey(mHomeName.trim(), floorNo, roomInfo[0].trim()
                , deviceInfo[0].trim(), name);
       // load.setDescription(description);
        load.setDeviceId(deviceInfo[1].trim());
        load.setFloorNo(floorNo);
        load.setSchedule("0 0 19 * * ?");
        load.setStatus(Constants.SWITCH_ON);
        //TODO need to remove
        SharedPreferences preferences = SmartControllerUtil.getAppPreference(AssociateLoadActivity.this);
        String separator=preferences.getString(Constants.SEPARATOR,Constants.DEFAULT);
        if(separator!=Constants.DEFAULT){
            description=description.replaceAll("/",separator);
        }
        final String topicn=description;
        load.setDescription(description);
        load.setTopicName(description);
        OkHttpClient okHttpClient = RestClient.getAuthenticatedClient();
        final ObjectMapper mapper = new ObjectMapper();
        HttpUrl url = RequestBuilder.buildURL(Constants.HTTP, null, Constants.PATH_ADD_LOAD);
        Log.v(TAG, "URL ASSOCIATE LOAD " + url.toString());
        Request request = RestClient.POST(url, mapper.writeValueAsString(load));
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                SmartControllerUtil.createAlertDialog(AssociateLoadActivity.this, getResources().
                        getString(R.string.alert_error), getResources().getString(R.string.error_associate_load));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    if (response.code() == Constants.STATUS_CODE_SUCCESS) {
                        final ResponseNode responseNode = mapper.readValue(myResponse, ResponseNode.class);
                        AssociateLoadActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (responseNode.getHttpStatusCode() == Constants.STATUS_CODE_SUCCESS && (responseNode.getData() != null && responseNode.getData().equalsIgnoreCase(Constants.STATUS_SUCCESS))) {
                                    Log.v(TAG, "Adding load to db, after successfully add at server");
                                    int id = mLoadSwitchDBDAO.addLoad(load);
                                    if (id != -1) {
                                        load.setId(id);
                                    }
                                    clearSelection();
                                    SmartControllerUtil.createAlertDialog(AssociateLoadActivity.this,
                                            getResources().getString(R.string.alert_info),
                                            getResources().getString(R.string.info_load_associate_success, name, topicn)).show();
                                } else {
                                    SmartControllerUtil.createAlertDialog(AssociateLoadActivity.this, getResources().
                                            getString(R.string.alert_error), getResources().getString(R.string.error_associate_load));
                                }

                            }
                        });
                    } else {
                        throw new IOException("Server Error: " + response.code());
                    }
                } else {
                    throw new IOException("Server Error: " + response.code());
                }
            }
        });
    }

    private void clearSelection() {
        mLoadTypeSpinner.setSelection(0);
        mLoadName.setText("");
        mLoadId.setText("");
        mLoadTypeSpinner.requestFocus();
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
}
