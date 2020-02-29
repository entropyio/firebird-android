package com.fb.firebird;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fb.firebird.utils.FirebirdUtil;

public class SettingActivity extends AppCompatActivity {

    private EditText hostEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        hostEdit = this.findViewById(R.id.editText_host);
        hostEdit.setText(FirebirdUtil.HTTP_SERVER);
    }

    public void updateSetting(View view) {
        String host = hostEdit.getText().toString();
        FirebirdUtil.HTTP_SERVER = host;
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        SettingActivity.this.finish();
    }
}
