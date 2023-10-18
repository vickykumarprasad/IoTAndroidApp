package com.urjalabs.smartcontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import 	androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import 	androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.urjalabs.smartcontroller.fragments.DashboardFragment;
import com.urjalabs.smartcontroller.fragments.MyAccountFragment;
import com.urjalabs.smartcontroller.fragments.MyHomeFragment;
import com.urjalabs.smartcontroller.mqtt.MQTTConnection;
import com.urjalabs.smartcontroller.storage.DBManager;
import com.urjalabs.smartcontroller.storage.HomeConfigurationDAO;
import com.urjalabs.smartcontroller.storage.HomeConfigurationSharedPrefDAO;
import com.urjalabs.smartcontroller.storage.LoadSwitchDBDAO;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private HomeConfigurationDAO mHomeConfigurationDAO;
    private LoadSwitchDBDAO mLoadSwitchDBDAO;
    private NavigationView navigationView;
    private DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //home configuration dao
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_HOME_PRFRENCE, Context.MODE_PRIVATE);
        mHomeConfigurationDAO = new HomeConfigurationSharedPrefDAO(sharedPreferences);
        mDBManager = new DBManager(this);
        mLoadSwitchDBDAO = mDBManager.getLoadSwitchDBDAO();
        mDBManager.open();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //to choose which screen need to be shown
        chooseFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            //super.onBackPressed();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            } else {
                moveTaskToBack(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_others){
            Intent intent = new Intent(getApplicationContext(), OthersActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id, false);

        if (id == R.id.nav_quick_action) {
            // Handle the camera action
        } else if (id == R.id.nav_automation) {

        } else if (id == R.id.nav_link_device) {
            Intent intent = new Intent(getApplicationContext(), AssociateLoadActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            finish();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else if(id==R.id.nav_wifi_config){
            Intent intent = new Intent(getApplicationContext(),WifiModule.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displaySelectedScreen(int itemId, boolean isAddToBackStack) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_dashboard:
                fragment = new DashboardFragment();
                break;
            case R.id.nav_home:
                fragment = new MyHomeFragment();
                break;
            case R.id.nav_account:
                fragment = new MyAccountFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            if (isAddToBackStack) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }
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
        //chooseFragment();
    }

    public HomeConfigurationDAO getHomeConfigurationDAO() {
        return mHomeConfigurationDAO;
    }

    public LoadSwitchDBDAO getLoadSwitchDBDAO() {
        return mLoadSwitchDBDAO;
    }

    private void chooseFragment() {
        //to choose which screen need to be shown
        int totalNumberOfLoads = mLoadSwitchDBDAO.getTotalLoadCount();
        if (totalNumberOfLoads > 0) {
            navigationView.getMenu().getItem(0).setChecked(true);
            String primaryServer = SmartControllerUtil.getAppPreference(MainActivity.this)
                    .getString(Constants.MQTT_SERVER_PRIMARY, Constants.DEFAULT);
            if (!primaryServer.equals(Constants.DEFAULT)) {
                MQTTConnection.getInstance(MainActivity.this, mLoadSwitchDBDAO.getAllTopics());
            }
            displaySelectedScreen(R.id.nav_dashboard, false);
        } else {
            navigationView.getMenu().getItem(1).setChecked(true);
            displaySelectedScreen(R.id.nav_home, false);
        }
    }

    public DBManager getDBManager() {
        return mDBManager;
    }
}
