package com.urjalabs.smartcontroller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.urjalabs.smartcontroller.models.UserCredential;
import com.urjalabs.smartcontroller.storage.DBManager;
import com.urjalabs.smartcontroller.storage.UserDBDAO;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText mNameText;
    private EditText mEmailText;
    private EditText mMobileNoText;
    private EditText mHouseNo;
    private EditText mStreet;
    private EditText mLocation;
    private EditText mCity;
    private Spinner mStates;
    private Spinner mCountries;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;
    private Button mSignupButton;
    private TextView mLoginLink;
    private View mProgressView;
    private View mLoginFormView;
    private static final String AT_LEAST_TWO_CHAR="at least two character";
    private String selectedState;
    private String selectedCountry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setUpLayoutView();
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
        createAdaptersForSpinners();
        mStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedState=(String) parent.getItemAtPosition(position);
                Log.v("states", selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        mCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedCountry=(String) parent.getItemAtPosition(position);
                Log.v("country", selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void createAdaptersForSpinners() {
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> statesAdapter = ArrayAdapter
                .createFromResource(this, R.array.india_states,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mStates.setAdapter(statesAdapter);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter
                .createFromResource(this, R.array.countries,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mCountries.setAdapter(countryAdapter);
    }

    private void setUpLayoutView() {
        mNameText = (EditText) findViewById(R.id.input_name);
        mEmailText =(EditText)findViewById(R.id.input_email);
        mMobileNoText =(EditText)findViewById(R.id.input_mobile);
        mHouseNo=(EditText)findViewById(R.id.input_house_no);
        mStreet=(EditText)findViewById(R.id.input_street);
        mLocation=(EditText)findViewById(R.id.input_area);
        mCity=(EditText)findViewById(R.id.input_city);
        mStates=(Spinner)findViewById(R.id.state);
        mCountries=(Spinner)findViewById(R.id.countries);
        mPasswordText =(EditText)findViewById(R.id.input_password);
        mConfirmPasswordText=(EditText)findViewById(R.id.input_confirm_password);
        mSignupButton = (Button) findViewById(R.id.btn_signup);
        mLoginLink = (TextView) findViewById(R.id.link_login);
        mProgressView = findViewById(R.id.sign_up_progress);
        mLoginFormView = findViewById(R.id.sign_up_form);
    }
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        // }
//        else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
    }
    public void signup() {
        Log.d(TAG, "Signup");
        String name  = mNameText.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String phone =  mMobileNoText.getText().toString();
        String house =  mHouseNo.getText().toString();
        String street =  mStreet.getText().toString();
        String location = mLocation.getText().toString();
        String city =  mCity.getText().toString();
        String state =  mStates.getSelectedItem().toString();
        String country =  mCountries.getSelectedItem().toString();
        String confirmPassword = mConfirmPasswordText.getText().toString();

        if (!validate( name,  email, phone, password, confirmPassword, house,
              street,  location,   city )) {
            onSignupFailed();
            return;
        }

        mSignupButton.setEnabled(false);

        showProgress(true);
        UserCredential userCredential = new UserCredential();
        userCredential.setName(name);
        userCredential.setEmail(email);
        userCredential.setPassword(password);
        userCredential.setPhone(phone);
        userCredential.setHouse_no(house);
        userCredential.setStreet(street);
        userCredential.setLocation(location);
        userCredential.setCity(city);
        userCredential.setState(state);
        userCredential.setCountry(country);
        DBManager dbManager = new DBManager(this.getApplicationContext());
        UserDBDAO userDBDAO = dbManager.getUserDBDAO();
        dbManager.open();
        try {


        userDBDAO.addUser(userCredential);
        onSignupSuccess();
        showProgress(false);
        } catch(Exception e){
            e.printStackTrace();
            onSignupFailed();
        }
        dbManager.close();
    }


    public void onSignupSuccess() {
        mSignupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "SignUp failed", Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true);
    }

    public boolean validate(String name, String email,String mobile,String password,String confirmPassword,String houseNo,
                            String street,  String location,  String city   ) {
        boolean valid = true;

        //

  // Validation check
        //

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
            valid = false;
        } else {
            mNameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            mEmailText.setError(null);
        }
        if (mobile.isEmpty() || !android.util.Patterns.PHONE.matcher(mobile).matches() || mobile.length() > 10) {
            mMobileNoText.setError("enter a valid mobile no");
            valid = false;
        } else {
            mMobileNoText.setError(null);
        }
        if (houseNo.isEmpty()) {
            mHouseNo.setError("house no is required");
            valid = false;
        } else {
            mHouseNo.setError(null);
        }
        if (street.isEmpty() || street.length() < 2) {
            mStreet.setError(AT_LEAST_TWO_CHAR);
            valid = false;
        } else {
            mStreet.setError(null);
        }
        if (location.isEmpty() || location.length() < 2) {
            mLocation.setError(AT_LEAST_TWO_CHAR);
            valid = false;
        } else {
            mLocation.setError(null);
        }
        if (city.isEmpty() || city.length() < 2) {
            mCity.setError(AT_LEAST_TWO_CHAR);
            valid = false;
        } else {
            mCity.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }
        if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
           mConfirmPasswordText.setError("password and confirm password mismatch");
            valid = false;
        } else {
            mConfirmPasswordText.setError(null);
        }

        return valid;
    }
}
