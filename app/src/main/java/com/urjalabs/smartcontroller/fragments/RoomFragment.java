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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.urjalabs.smartcontroller.MainActivity;
import com.urjalabs.smartcontroller.R;
import com.urjalabs.smartcontroller.adapters.CustomRoomListAdapter;
import com.urjalabs.smartcontroller.models.Room;
import com.urjalabs.smartcontroller.storage.HomeConfigurationDAO;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RoomFragment extends Fragment {
    private ListView mRoomList;
    private Button mAddRoom;
    private TextView mFloorLabel;
    private ArrayAdapter<CharSequence> areaTypeAdapter;
    private List<String> roomListsName = new ArrayList<>();
    private List<String> roomListsType = new ArrayList<>();
    private List<Integer> roomListsImage = new ArrayList<>();
    private CustomRoomListAdapter mCustomRoomListAdapter;
    private String mHomeName;
    private HomeConfigurationDAO mHomeConfigurationDAO;

    public RoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getResources().getString(R.string.my_home));
        mHomeConfigurationDAO = ((MainActivity) this.getActivity()).getHomeConfigurationDAO();
        mHomeName = mHomeConfigurationDAO.getHomeName();
        final int floorNo = getArguments().getInt(Constants.FLOOR_NO, -1);
        initializeViewComponent(view, floorNo);
        mFloorLabel.setText(mHomeName + Constants.CHAR_SLASH + Constants.FLOOR + floorNo);
        if (floorNo != -1) {
            calculateRoomList(floorNo);
        }
        // Create an ArrayAdapter using the string array and a default spinner
        areaTypeAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.room_types,
                        android.R.layout.simple_spinner_item);
        mCustomRoomListAdapter = new CustomRoomListAdapter(getActivity(), roomListsName, roomListsType, roomListsImage);
        mRoomList.setAdapter(mCustomRoomListAdapter);
        mAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialogForRoomAdd(floorNo);
            }
        });
    }


    private void initializeViewComponent(View view, final int floorNO) {
        mRoomList = view.findViewById(R.id.list_room);
        mFloorLabel = view.findViewById(R.id.floor_name);
        mAddRoom = view.findViewById(R.id.btn_add_room);
        mRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment roomFragment = new DeviceFragment();
                Bundle args = new Bundle();
                args.putInt(Constants.FLOOR_NO, floorNO);
                args.putInt(Constants.ROOM_NO, i);
                roomFragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, roomFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void createDialogForRoomAdd(final int floorNo) {
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getActivity().getResources().getString(R.string.add_room));
            final EditText roomName = new EditText(getActivity());
            roomName.setTransformationMethod(PasswordTransformationMethod.getInstance());
            roomName.setHint("Name");
            roomName.setInputType(InputType.TYPE_CLASS_TEXT);
            final Spinner areaType = new Spinner(getActivity(), Spinner.MODE_DIALOG);
            areaType.setAdapter(areaTypeAdapter);
            areaType.setPrompt("Choose Type");
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(roomName);
            ll.addView(areaType);
            ll.setPadding(16, 0, 16, 0);
            //ll.setPadding(16, 0, 16, 0);
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
                    if (validateHome(roomName)) {
                        // save info in Shared Preferences.
                        addRoomData(floorNo, roomName, areaType);
                        alert1.dismiss();
                        //createListView(noOfFloor);
                    }

                }
            });
        }
    }

    private void addRoomData(int floorNo, EditText roomName, Spinner areaType) {
        int noOfRoom =mHomeConfigurationDAO.getNumberOfRooms(floorNo);
        if (noOfRoom == -1) {
            noOfRoom = 1;
        } else {
            noOfRoom = noOfRoom + 1;
        }
        int roomNo = noOfRoom - 1;
        String roomNameString = roomName.getText().toString();
        String areaTypeValue = areaType.getSelectedItem().toString();
        //update room numbers
        mHomeConfigurationDAO.saveOrUpdateNumberOfRooms(floorNo,noOfRoom);
        //update name and type
        mHomeConfigurationDAO.saveOrUpdateRoomConfig(new Room(floorNo,roomNo,roomNameString,areaTypeValue));
        roomListsName.add(roomNameString);
        roomListsType.add(String.valueOf(areaTypeValue));
        addRoomImage(String.valueOf(areaTypeValue));
        mCustomRoomListAdapter.notifyDataSetChanged();
    }

    private boolean validateHome(EditText roomName) {
        boolean isValid = true;
        if (roomName.getText().toString().isEmpty() | roomName.getText().toString().length() < 3) {
            roomName.setError("At least three char required");
            isValid = false;
        }
        return isValid;
    }

    private void calculateRoomList(int floorNo) {
        roomListsName.clear();
        roomListsType.clear();
        roomListsImage.clear();
        int noOfRoom = mHomeConfigurationDAO.getNumberOfRooms(floorNo);
        if (noOfRoom != -1) {
            for (int roomNumber = 0; roomNumber < noOfRoom; roomNumber++) {
                Room room = mHomeConfigurationDAO.getRoomConfig(floorNo, roomNumber);
                if (!SmartControllerUtil.isEmpty(room.getName()) && !SmartControllerUtil.isEmpty(room.getType())) {
                    roomListsName.add(room.getName());
                    roomListsType.add(room.getType());
                    addRoomImage(room.getType());
                }
            }
        }
    }

    private void addRoomImage(String roomType) {
        switch (roomType) {
            case ("Room"):
                roomListsImage.add(R.drawable.if_room);
                break;
            case ("Kitchen"):
                roomListsImage.add(R.drawable.if_kitchen);
                break;
            case ("Bathroom"):
                roomListsImage.add(R.drawable.if_bath);
                break;
            default:
                roomListsImage.add(R.drawable.if_room);
                break;
        }
    }
}
