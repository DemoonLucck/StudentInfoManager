package com.example.double2.studentinfomanager.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.adapter.ShowAdapter;
import com.example.double2.studentinfomanager.db.StudentDateBaseHelper;


public class CountActivity extends AppCompatActivity {
    private Button btnBack;
    private TextView average;
    private TextView highest;
    private TextView lowest;
    private TextView pass_num;
    private TextView fail_num;
    private StudentDateBaseHelper mStudentDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        mStudentDateBaseHelper = new StudentDateBaseHelper(this, "StudentInfo.db", null, 1);
        mSQLiteDatabase = mStudentDateBaseHelper.getReadableDatabase();
        initView();
        countInfo();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void countInfo(){
        //将数据库中的表项按成绩降序排列
        Cursor mCursor = mSQLiteDatabase.query("student", null, null, null, null, null, "grade desc");
        int size = mCursor.getCount();
        //总分数
        int sum = 0;
        //及格的人数
        int pass = 0;
        //成绩数组
        int[] grades = new int[size];
        //初始化成绩数组grades[i]
        for(int i = 0;i <size;i++){
                mCursor.moveToNext();
                grades[i] = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("grade")));
                sum += grades[i];
        }
        mCursor.close();
        //判断及格人数
        for(int i = 0;i < size;i++) {
            if (grades[i] >= 60 && grades[i + 1] < 60) {
                pass = i + 1;
            } else if (grades[size - 1] >= 60) {
                pass = size;
            } else if (grades[0] < 60){
                pass = 0;
            }
        }
        //计算平均分、最高分、最低分、及格人数、不及格人数
        String averageNum = String.valueOf(sum/size);
        String maxNum = String.valueOf(grades[0]);
        String minNum = String.valueOf(grades[size-1]);
        String passNum = String.valueOf(pass);
        String failNum = String.valueOf(size-pass);

        //显示平均分、最高分、最低分、及格人数、不及格人数
        average.setText(averageNum);
        highest.setText(maxNum);
        lowest.setText(minNum);
        pass_num.setText(passNum);
        fail_num.setText(failNum);
        Toast toast = Toast.makeText(CountActivity.this, "成绩统计成功！", Toast.LENGTH_SHORT);
        MainActivity.showMyToast(toast,500);
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        btnBack = (Button) findViewById(R.id.btn_edit_back);
        average = (TextView) findViewById(R.id.average);
        highest = (TextView) findViewById(R.id.highest);
        lowest = (TextView) findViewById(R.id.lowest);
        pass_num = (TextView) findViewById(R.id.pass_num);
        fail_num = (TextView) findViewById(R.id.fail_num);
    }


}
