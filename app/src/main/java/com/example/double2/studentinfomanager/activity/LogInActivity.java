package com.example.double2.studentinfomanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.db.StudentDateBaseHelper;


public class LogInActivity extends Activity {

    //控件
    private Button btnLogIn;
    private EditText etPassword;
    private EditText etAccount;
    //数据存储
    private SharedPreferences mSharedPreferences;
    private StudentDateBaseHelper mStudentDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_log_in);
        mStudentDateBaseHelper = new StudentDateBaseHelper(this, "StudentInfo.db", null, 1);
        mSQLiteDatabase = mStudentDateBaseHelper.getReadableDatabase();
        initView();
    }

    private void initView() {

        mSharedPreferences = this.getSharedPreferences("student", MODE_PRIVATE);
        etAccount = (EditText) findViewById(R.id.et_login_account);

        etPassword = (EditText) findViewById(R.id.et_login_password);
        btnLogIn = (Button) findViewById(R.id.btn_login_log_in);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0000".equals(etAccount.getText().toString())){
                    //从本地获取到密码，如果没有设置过密码，就默认为1
                    String oldPassword = mSharedPreferences.getString("password", "1");

                    if (oldPassword.equals(etPassword.getText().toString())) {
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LogInActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LogInActivity.this, "登录失败，密码错误。", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    String[] my_account ={etAccount.getText().toString()};
//                    String[] password ={"password"};
//                    Toast.makeText(LogInActivity.this, "123", Toast.LENGTH_SHORT).show();
                    Cursor mCursor = mSQLiteDatabase.query("student", null, "number = ?", my_account, null, null, null);
                    String mypassword="9999";
                    while(mCursor.moveToNext()){
                        mypassword = mCursor.getString(mCursor.getColumnIndex("password"));
                    }
                    if(mypassword.equals(etPassword.getText().toString())){
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LogInActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                        mEditor.putString("account", my_account[0]);
                        mEditor.commit();
                        //Toast.makeText(LogInActivity.this, test, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LogInActivity.this, "登录失败，账号或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
