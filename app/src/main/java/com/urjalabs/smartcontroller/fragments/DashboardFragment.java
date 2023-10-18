package com.urjalabs.smartcontroller.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.urjalabs.smartcontroller.MainActivity;
import com.urjalabs.smartcontroller.R;
import com.urjalabs.smartcontroller.adapters.CustomDashLoadListAdapter;
import com.urjalabs.smartcontroller.storage.LoadSwitchDBDAO;
import com.urjalabs.smartcontroller.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {


    private LoadSwitchDBDAO mLoadSwitchDBDAO;
    private ListView mLoadTypeList;
    private List<String> loadList = new ArrayList<>();
    private List<Integer> loadCountList = new ArrayList<>();
    private List<Integer> loadImages = new ArrayList<>();
    private CustomDashLoadListAdapter customDashLoadListAdapter;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getResources().getString(R.string.dashboard));
        mLoadSwitchDBDAO = ((MainActivity) this.getActivity()).getLoadSwitchDBDAO();
        String loadArray[] = getActivity().getResources().getStringArray(R.array.load_types);
        initializeViewComponent(view);
        createListView(loadArray);
    }

    private void initializeViewComponent(View view) {
        mLoadTypeList = view.findViewById(R.id.list_dash_loads);
        customDashLoadListAdapter = new CustomDashLoadListAdapter(getActivity(), loadList, loadCountList, loadImages);
        mLoadTypeList.setAdapter(customDashLoadListAdapter);
        mLoadTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment loadFragment = new LoadSwitchFragment();
                Bundle args = new Bundle();
                args.putString(Constants.LOAD_TYPE, loadList.get(i));
                args.putBoolean(Constants.IS_CALLED_DASH,true);
                loadFragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, loadFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void createListView(String loadTyepArray[]) {
        loadList.clear();
        loadCountList.clear();
        loadImages.clear();
        for (int index = 1; index < loadTyepArray.length; index++) {
            int count = mLoadSwitchDBDAO.getLoadCountByType(loadTyepArray[index]);
            if (count > 0) {
                loadList.add(loadTyepArray[index]);
                loadCountList.add(count);
                addLoadImage(loadTyepArray[index]);
            }
        }
        customDashLoadListAdapter.notifyDataSetChanged();
    }

    private void addLoadImage(String roomType) {
        switch (roomType) {
            case ("Bulb"):
                loadImages.add(R.drawable.if_bulb_48);
                break;
            case ("Tubelight"):
                loadImages.add(R.drawable.if_tubelight_48);
                break;
            case ("Fan"):
                loadImages.add(R.drawable.if_fan_48);
                break;
            case ("AC"):
                loadImages.add(R.drawable.if_ac_48);
                break;
            case ("Television"):
                loadImages.add(R.drawable.if_tv_48);
                break;
            case ("Refrigerator"):
                loadImages.add(R.drawable.if_refrigerator_48);
                break;
            case ("Washing Machine"):
                loadImages.add(R.drawable.if_washing_machine_48);
                break;
            case ("Mixer/Juicer"):
                loadImages.add(R.drawable.if_mixer_48);
                break;
            case ("Coffee Machine"):
                loadImages.add(R.drawable.if_cofee);
                break;
            default:
                loadImages.add(R.drawable.if_other_48);
                break;
        }
    }
}
