package com.urjalabs.smartcontroller.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.urjalabs.smartcontroller.MainActivity;
import com.urjalabs.smartcontroller.R;
import com.urjalabs.smartcontroller.adapters.CustomFloorListAdapter;
import com.urjalabs.smartcontroller.storage.HomeConfigurationDAO;
import com.urjalabs.smartcontroller.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyHomeFragment extends Fragment {
    TextView mHomeName;
    ListView mFloorList;
    Button mConfigureHome;
    private HomeConfigurationDAO mHomeConfigurationDAO;
    public MyHomeFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.myhome_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getResources().getString(R.string.my_home));
        mHomeConfigurationDAO = ((MainActivity) this.getActivity()).getHomeConfigurationDAO();
        initializeViewComponent(view);
        int noFloor = mHomeConfigurationDAO.getNumberOfFloor();
        if (noFloor == -1) {
            mConfigureHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog();
                }
            });
        } else {
            String homeName = mHomeConfigurationDAO.getHomeName();
            mHomeName.setText(getActivity().getResources().getString(R.string.house_name_place_holder,homeName));
            mConfigureHome.setVisibility(View.GONE);
            createListView(noFloor);
        }
    }

    private void initializeViewComponent(View view) {
        mHomeName = view.findViewById(R.id.home_name);
        mFloorList = view.findViewById(R.id.list_floor);
        mConfigureHome = view.findViewById(R.id.configure_home);
        mFloorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Fragment roomFragment=new RoomFragment();
                Bundle args = new Bundle();
                args.putInt(Constants.FLOOR_NO, i);
                roomFragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, roomFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void createDialog() {
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getActivity().getResources().getString(R.string.home_config));
            final EditText homeName = new EditText(getActivity());
            homeName.setTransformationMethod(PasswordTransformationMethod.getInstance());
            homeName.setHint("Name of house");
            homeName.setInputType(InputType.TYPE_CLASS_TEXT);
            final EditText numberOfFloors = new EditText(getActivity());
            numberOfFloors.setTransformationMethod(PasswordTransformationMethod.getInstance());
            numberOfFloors.setHint("No. of floors");
            numberOfFloors.setInputType(InputType.TYPE_CLASS_NUMBER);
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(homeName);
            ll.addView(numberOfFloors);
            ll.setPadding(16, 0, 16, 0);
            alertDialog.setView(ll);
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
                    if (validateHome(homeName, numberOfFloors)) {
                        // save info in Shared Preferences.
                        int noOfFloor = Integer.parseInt(numberOfFloors.getText().toString());
                        mHomeConfigurationDAO.saveOrUpdateNumberOfFloor(noOfFloor);
                        mHomeConfigurationDAO.saveOrUpdateHomeName(homeName.getText().toString());
                        mHomeName.setText(getActivity().getResources().getString(R.string.house_name_place_holder,homeName.getText().toString()));
                        mConfigureHome.setVisibility(View.GONE);
                        createListView(noOfFloor);
                        alert1.dismiss();
                    }
                }
            });
        }
    }

    private void createListView(int noOfFloor) {
        String array[] = new String[noOfFloor];
        for (int i = 0; i < noOfFloor; i++) {
            array[i] = "Floor" + i;
        }
        CustomFloorListAdapter customFloorListAdapter = new CustomFloorListAdapter(getActivity(), array);
        mFloorList.setAdapter(customFloorListAdapter);
    }

    private boolean validateHome(EditText homeName, EditText floorNo) {
        boolean isValid = true;
        if (homeName.getText().toString().isEmpty() | homeName.getText().toString().length() < 3) {
            homeName.setError("At least three char required");
            isValid = false;
        }
        if (floorNo.getText().toString().isEmpty()) {
            floorNo.setError("No of floor is required");
            isValid = false;
        }
        return isValid;
    }
}
