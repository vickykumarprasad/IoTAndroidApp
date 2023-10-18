package com.urjalabs.smartcontroller.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.urjalabs.smartcontroller.CaptureActivityPortrait;
import com.urjalabs.smartcontroller.MainActivity;
import com.urjalabs.smartcontroller.R;
import com.urjalabs.smartcontroller.adapters.CustomDeviceListAdapter;
import com.urjalabs.smartcontroller.models.Device;
import com.urjalabs.smartcontroller.models.ResponseNode;
import com.urjalabs.smartcontroller.models.Room;
import com.urjalabs.smartcontroller.network.RequestBuilder;
import com.urjalabs.smartcontroller.network.RestClient;
import com.urjalabs.smartcontroller.storage.DeviceDAO;
import com.urjalabs.smartcontroller.storage.HomeConfigurationDAO;
import com.urjalabs.smartcontroller.storage.LoadSwitchDBDAO;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment {

    private static final String TAG = "DeviceFragment";
    private ListView mDeviceListView;
    private Button mAddDevice;
    private TextView mRoomLabel;
    private LoadSwitchDBDAO mLoadSwitchDBDAO;
    private DeviceDAO mDeviceDAO;
    private List<Device> mDeviceList = new ArrayList<>();
    //    private List<String> deviceListsName = new ArrayList<>();
//    private List<String> deviceListID = new ArrayList<>();
    private CustomDeviceListAdapter mCustomDeviceListAdapter;
    private String mHomeName;
    private LinearLayout mRoot;
    private EditText mDeviceIDEditText;
    private IntentIntegrator mQRScan;

    public DeviceFragment() {
        // Required empty public constructors
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getResources().getString(R.string.my_home));
        initializeViewComponent(view);
        HomeConfigurationDAO mHomeConfigurationDAO = ((MainActivity) this.getActivity()).getHomeConfigurationDAO();
        mLoadSwitchDBDAO = ((MainActivity) this.getActivity()).getLoadSwitchDBDAO();
        mDeviceDAO = ((MainActivity) this.getActivity()).getDBManager().getDeviceDBDAO();
        mHomeName = mHomeConfigurationDAO.getHomeName();
        final int floorNo = getArguments().getInt(Constants.FLOOR_NO, -1);
        final int roomNo = getArguments().getInt(Constants.ROOM_NO, -1);
        Log.v(TAG, Constants.ROOM_NO + roomNo);
        Room room = mHomeConfigurationDAO.getRoomConfig(floorNo, roomNo);
        mRoomLabel.setText(mHomeName + Constants.CHAR_SLASH + Constants.FLOOR + floorNo + Constants.CHAR_SLASH + room.getName());
        if (floorNo != -1) {
            calculateDeviceList(floorNo, roomNo);
        }
        mCustomDeviceListAdapter = new CustomDeviceListAdapter(getActivity(), mDeviceList);
        mDeviceListView.setAdapter(mCustomDeviceListAdapter);
        mAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SmartControllerUtil.isConnected(getActivity())) {
                    createDialogForDeviceAdd(floorNo, roomNo);
                } else {
                    SmartControllerUtil.createAlertDialog(getActivity(), getResources().getString(R.string.warn_alert),
                            getResources().getString(R.string.error_offline)).show();
                }
            }
        });
        addLongItemClickListener();
    }


    private void initializeViewComponent(View view) {
        mRoot = view.findViewById(R.id.root_fragment_device);
        mDeviceListView = view.findViewById(R.id.list_devices);
        mRoomLabel = view.findViewById(R.id.room_name);
        mAddDevice = view.findViewById(R.id.btn_add_device);
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Device device = mDeviceList.get(i);
                int loadCount = mLoadSwitchDBDAO.getLoadCountByDeviceID(device.getDeviceId());
                if (loadCount > 0) {
                    Fragment loadFragment = new LoadSwitchFragment();
                    Bundle args = new Bundle();
                    args.putString(Constants.DEVICE_ID, device.getDeviceId());
                    args.putBoolean(Constants.IS_CALLED_DASH, false);
                    loadFragment.setArguments(args);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, loadFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
        //initializing scan object
        mQRScan = IntentIntegrator.forSupportFragment(DeviceFragment.this);
        mQRScan.setPrompt("Scan Device Mac ID QR Code");
        mQRScan.setOrientationLocked(false);
        mQRScan.setBeepEnabled(true);
        mQRScan.setCaptureActivity(CaptureActivityPortrait.class);
    }

    private void addLongItemClickListener() {
        mDeviceListView.setLongClickable(true);
        mDeviceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, final long l) {
                if (SmartControllerUtil.isConnected(getActivity())) {
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete Device");
                    alert.setMessage("Are you sure you want to delete?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Device device = mDeviceList.get(position);
                            try {
                                deleteDevice(device, position);
                            } catch (IOException e) {
                                Toast.makeText(getActivity(), getActivity().getResources().
                                        getString(R.string.error_deleting_device), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close dialog
                            dialog.cancel();
                        }
                    });
                    alert.show();
                } else {
                    SmartControllerUtil.createAlertDialog(getActivity(), getResources().getString(R.string.warn_alert),
                            getResources().getString(R.string.error_offline)).show();
                }
                return true;
            }
        });
    }

    private void createDialogForDeviceAdd(final int floorNo, final int roomNo) {
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.device_dialog,null);
            alertDialog.setTitle(getActivity().getResources().getString(R.string.add_device));
            alertDialog.setView(dialogView);
            final EditText deviceName = dialogView.findViewById(R.id.input_device_name);
            final ImageButton scanQRCode=dialogView.findViewById(R.id.btn_scan_qr_code);
            scanQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    takePicture();
                }
            });
            mDeviceIDEditText = dialogView.findViewById(R.id.input_device_id);
            //ll.setPadding(16, 0, 16, 0);

            alertDialog.setPositiveButton(R.string.confirm,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            alertDialog.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert1 = alertDialog.create();
            alert1.show();
            Button okButton = alert1.getButton(DialogInterface.BUTTON_POSITIVE);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateDevice(deviceName, mDeviceIDEditText)) {
                        // save info in Shared Preferences.
                        addDeviceData(floorNo, roomNo, deviceName, mDeviceIDEditText);
                        alert1.dismiss();
                        //createListView(noOfFloor);
                    }

                }
            });
        }
    }

    private void addDeviceData(int floorNo, int roomNumber, EditText deviceName, EditText deviceID) {
        String deviceNameValue = deviceName.getText().toString();
        String deviceIDValue = deviceID.getText().toString();
        String deviceDescriptionKey = SmartControllerUtil.getDeviceSharedPreferenceKey(mHomeName, floorNo, roomNumber, deviceIDValue);
        try {
            addDeviceDataAtServer(floorNo, roomNumber, deviceIDValue, deviceNameValue, deviceDescriptionKey);
            mCustomDeviceListAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity().getApplicationContext(), getActivity().getResources().getString(R.string.info_add_device_success), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.v(TAG, "Error in adding data", e);
        }
        //add
        //update number of devices
//        mHomeConfigurationDAO.saveOrUpdateNumberOfDevices(floorNo, roomNumber, noOfDevice);
//        //update name and type
//        mHomeConfigurationDAO.saveOrUpdateDeviceConfig(mHomeName, new Device(floorNo, roomNumber, deviceNo, deviceIDValue, deviceNameValue, deviceDescriptionKey));
//        deviceListsName.add(deviceNameValue);
//        deviceListID.add(deviceIDValue);


    }

    private void addDeviceDataAtServer(final int floorNo, final int roomNumber, final String deviceID, final String deviceName, final String description) throws IOException {
        OkHttpClient okHttpClient = RestClient.getAuthenticatedClient();
        HttpUrl url = RequestBuilder.buildURL(Constants.HTTP, null, Constants.PATH_ADD_DEVICE);
        final ObjectMapper mapper = new ObjectMapper();
        Device device = new Device(deviceID, deviceName, description);
        Log.v(TAG, "URL " + url.toString());
        Request request = RestClient.POST(url, mapper.writeValueAsString(device));
        Log.v(TAG, "Request Header" + request.headers().get("Cookie"));
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartControllerUtil.createAlertDialog(getActivity(), "Alert", getActivity().getResources().getString(R.string.error_add_device)).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    if (response.code() == Constants.STATUS_CODE_SUCCESS) {
                        final ResponseNode responseNode = mapper.readValue(myResponse, ResponseNode.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (responseNode.getHttpStatusCode() == Constants.STATUS_CODE_SUCCESS && (responseNode.getData() != null && responseNode.getData().equalsIgnoreCase(Constants.STATUS_SUCCESS))) {
                                    Device deviceNew = new Device(floorNo, roomNumber, deviceID, deviceName, description);
                                    int id =  mDeviceDAO.createDevice(deviceNew);
                                    if (id != -1) {
                                        deviceNew.setId(id);
                                        mDeviceList.add(deviceNew);
                                        Log.v(TAG, "Device added in db");
                                    } else {
                                        Log.e(TAG, "Error adding device in db");
                                    }
                                    mCustomDeviceListAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity().getApplicationContext(), "Device registered successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    SmartControllerUtil.createAlertDialog(getActivity(), getResources().
                                            getString(R.string.alert_error), getActivity().getResources().getString(R.string.error_add_device));
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

    private boolean validateDevice(EditText deviceName, EditText deviceID) {
        boolean isValid = true;
        if (deviceName.getText().toString().isEmpty() | deviceName.getText().toString().length() < 3) {
            deviceName.setError("At least three char required");
            isValid = false;
        }
        if (deviceID.getText().toString().isEmpty() | deviceID.getText().toString().length() != 32) {
            deviceID.setError("Device ID required length is 32 supplied length " + deviceID.getText().toString().length());
            isValid = false;
        }
        return isValid;
    }

    private void calculateDeviceList(int floorNo, int roomNo) {
        mDeviceList.clear();
        mDeviceList = mDeviceDAO.getDevicesInRoom(floorNo, roomNo);
//        int noOfDevice = mHomeConfigurationDAO.getNumberOfDevice(floorNo, roomNo);
//        if (noOfDevice != -1) {
//            for (int deviceNumber = 0; deviceNumber < noOfDevice; deviceNumber++) {
//                Device device = mHomeConfigurationDAO.getDeviceConfig(floorNo, roomNo, deviceNumber);
//                if (!SmartControllerUtil.isEmpty(device.getName()) && !SmartControllerUtil.isEmpty(device.getDeviceId())) {
//                    deviceListsName.add(device.getName());
//                    deviceListID.add(device.getDeviceId());
//                }
//            }
//        }
    }

    private void deleteDevice(final Device device, final int position) throws IOException {
        OkHttpClient okHttpClient = RestClient.getAuthenticatedClient();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("id", String.valueOf(device.getDeviceId()));
        HttpUrl url = RequestBuilder.buildURL(Constants.HTTP, queryParam, Constants.PATH_DEL_DEVICE);
        Log.v("Url Delete device", url.toString());
        Request request = RestClient.createDeleteRequest(url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                SmartControllerUtil.createAlertDialog(getActivity(), getResources().
                        getString(R.string.alert_error), getResources().getString(R.string.error_delete_load));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    if (response.code() == Constants.STATUS_CODE_SUCCESS) {
                        ObjectMapper mapper = new ObjectMapper();
                        final ResponseNode responseNode = mapper.readValue(myResponse, ResponseNode.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (responseNode.getHttpStatusCode() == Constants.STATUS_CODE_SUCCESS && (responseNode.getData() != null && responseNode.getData().equalsIgnoreCase(Constants.STATUS_SUCCESS))) {
                                    Log.v(TAG, "deleting device at db, after successfully delete at server");
                                    mDeviceDAO.deleteDevice(device);
                                    mLoadSwitchDBDAO.deleteLoadByDeviceID(device.getDeviceId());
                                    mDeviceList.remove(position);
                                    mCustomDeviceListAdapter.notifyDataSetChanged();
                                    Snackbar snackbar = Snackbar
                                            .make(mRoot, getActivity().getResources().
                                                    getString(R.string.info_delete_device_success), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                } else {
                                    Toast.makeText(getActivity(), getActivity().getResources().
                                            getString(R.string.error_deleting_device), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                Log.v(TAG,"Result : "+result.getContents());
                //if qr contains data
                    //converting the data to json
                    //JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    if( mDeviceIDEditText!=null){
                        if(result.getContents().length()==0){
                          mDeviceIDEditText.setText(result.getContents());
                            mDeviceIDEditText.setError(null);
                        }else{
                            mDeviceIDEditText.setError("Invalid QR Code, Device ID length is short, scanned length " + result.getContents().length());
                        }
                    }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void takePicture() {
        //initiating the qr code scan
        mQRScan.initiateScan();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
