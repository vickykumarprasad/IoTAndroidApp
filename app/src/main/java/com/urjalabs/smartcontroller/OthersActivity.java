package com.urjalabs.smartcontroller;

import android.content.SharedPreferences;
import android.os.Bundle;
import 	androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

public class OthersActivity extends AppCompatActivity {
    private EditText mEditTextSeparator;
    private Button mSubmit;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        mEditTextSeparator=findViewById(R.id.input_separator);
        mSubmit=findViewById(R.id.btn_submit);
        mPreferences = SmartControllerUtil.getAppPreference(OthersActivity.this);
        String separator=mPreferences.getString(Constants.SEPARATOR,Constants.DEFAULT);
        if(separator!=Constants.DEFAULT){
            mEditTextSeparator.setText(separator);
        }
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String separator=mEditTextSeparator.getText().toString();
                if(!separator.isEmpty() && separator.length()==1){
                    if(containsSpecialCharacter(separator)){
                        mEditTextSeparator.setError(null);
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putString(Constants.SEPARATOR,separator);
                        Toast.makeText(getApplicationContext(),"Successfully saved separator value",Toast.LENGTH_LONG).show();
                        editor.apply();
                    }else {
                        mEditTextSeparator.setError("Invalid separator, It should be a special char.");
                    }
                }else{
                    mEditTextSeparator.setError("Invalid separator, length should be one.");
                }
            }
        });
    }

    public boolean containsSpecialCharacter(String s) {
        return s != null &&  s.matches("[^A-Za-z0-9 ]");
    }
}
