package com.urjalabs.smartcontroller.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urjalabs.smartcontroller.MainActivity;
import com.urjalabs.smartcontroller.R;
import com.urjalabs.smartcontroller.adapters.CustomLoadSwitchListAdapter;
import com.urjalabs.smartcontroller.models.Load;
import com.urjalabs.smartcontroller.models.ResponseNode;
import com.urjalabs.smartcontroller.network.RequestBuilder;
import com.urjalabs.smartcontroller.network.RestClient;
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
public class LoadSwitchFragment extends Fragment {


    private static final String TAG = "LoadSwitchFragment";
    private LoadSwitchDBDAO mLoadSwitchDBDAO;
    private ListView mLoadListView;
    private List<Load> mLoadList = new ArrayList<>();
    private CustomLoadSwitchListAdapter customLoadSwitchAdapter;
    private LinearLayout mRoot;

    public LoadSwitchFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_load_switch, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getResources().getString(R.string.loads));
        mLoadSwitchDBDAO = ((MainActivity) this.getActivity()).getLoadSwitchDBDAO();
        String loadType = getArguments().getString(Constants.LOAD_TYPE);
        boolean isCalledFromDash = getArguments().getBoolean(Constants.IS_CALLED_DASH);
        //load image
        initializeViewComponent(view);
        createListView(loadType, isCalledFromDash);
    }

    private void initializeViewComponent(View view) {
        mRoot =  view.findViewById(R.id.fragment_load_switch);
        mLoadListView = view.findViewById(R.id.list_switch_loads);
        customLoadSwitchAdapter = new CustomLoadSwitchListAdapter(getActivity(), mLoadList);
        mLoadListView.setAdapter(customLoadSwitchAdapter);
        mLoadListView.setLongClickable(true);
        mLoadListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, final long l) {
                final Load load = mLoadList.get(position);
                if (SmartControllerUtil.isConnected(getActivity())) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete Load");
                    alert.setMessage("Are you sure you want to delete?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            try {
                                deleteLoad(load, position);
                            } catch (IOException e) {
                                Toast.makeText(getActivity(), getActivity().getResources().
                                        getString(R.string.error_delete_load), Toast.LENGTH_SHORT).show();
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

    private void createListView(String loadType, boolean isCalledFromDash) {
        mLoadList.clear();
        List<Load> loadList;
        if (isCalledFromDash) {
            loadList = mLoadSwitchDBDAO.getLoadsByType(loadType);
        } else {

            loadList = mLoadSwitchDBDAO.getLoadsByDeviceID(getArguments().getString(Constants.DEVICE_ID));
        }
        mLoadList.addAll(loadList);
        customLoadSwitchAdapter.notifyDataSetChanged();
    }

    private void deleteLoad(final Load load, final int position) throws IOException {
        OkHttpClient okHttpClient = RestClient.getAuthenticatedClient();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("id", String.valueOf(load.getSwitchId()));
        HttpUrl url = RequestBuilder.buildURL(Constants.HTTP, queryParam, Constants.PATH_DEL_LOAD);
        Log.v("Url Delete Load", url.toString());
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
                                    Log.v(TAG, "deleting load at db, after successfully delete at server");
                                    mLoadSwitchDBDAO.deleteLoad(load);
                                    mLoadList.remove(position);
                                    customLoadSwitchAdapter.notifyDataSetChanged();
                                    Snackbar snackbar = Snackbar
                                            .make(mRoot, getActivity().getResources().
                                                    getString(R.string.info_delete_load_success), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                } else {
                                    Toast.makeText(getActivity(), getActivity().getResources().
                                            getString(R.string.error_delete_load), Toast.LENGTH_SHORT).show();
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

}
