package com.example.double2.studentinfomanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.db.StudentDateBaseHelper;

import static java.lang.System.exit;

public class EditActivity extends Activity implements View.OnClickListener {
    //控件
    private Button btnBack;
    private Button btnSure;
    private EditText etNumber;
    private EditText etName;
    private EditText etNativePlace;
    private EditText etSpecialty;
    private EditText etgrade;
    private EditText ettelephone;
    private TextView tvGender;
    private TableRow trGender;
    private TextView tvBirth;
    private TableRow trBirth;
    private TextView tvDelete;
    private ImageButton tvPhoneCall;
    //数据存储
    private StudentDateBaseHelper mStudentDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    //变量与常量
    public static final int TYPE_ADD = 111;
    public static final int TYPE_EDIT = 222;
    private int currentType;//判断当前是需要增加还是修改
    private String initNumber;//此处用来记录原来在数据库中的存储条目（如果编辑的时候把学号修改了，那么学号就不能代表原来的条目了）
    private String telephoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit);

        mStudentDateBaseHelper = new StudentDateBaseHelper(this, "StudentInfo.db", null, 1);
        mSQLiteDatabase = mStudentDateBaseHelper.getReadableDatabase();

        receiveType();
        initView();
        receiveInfo();
    }

    //获取当前的编辑类型，是增添或者编辑
    private void receiveType() {
        Intent intent = this.getIntent();
        currentType = intent.getIntExtra("type", TYPE_ADD);
    }

    //从数据库获取学生信息显示到界面上
    private void receiveInfo() {

        if (currentType == TYPE_EDIT) {
            Intent intent = this.getIntent();
            initNumber = intent.getStringExtra("number");
            String name;
            String gender;
            String birth;
            String nativePlace;
            String specialty;
            String grade;


            Cursor mCursor = mSQLiteDatabase.query("student", null, "number=?", new String[]{initNumber}, null, null, null);

            if (mCursor.moveToNext()) {
                name = mCursor.getString(mCursor.getColumnIndex("name"));
                gender = mCursor.getString(mCursor.getColumnIndex("gender"));
                birth = mCursor.getString(mCursor.getColumnIndex("birth"));
                nativePlace = mCursor.getString(mCursor.getColumnIndex("native_place"));
                specialty = mCursor.getString(mCursor.getColumnIndex("specialty"));
                grade = mCursor.getString(mCursor.getColumnIndex("grade"));
                telephoneNumber = mCursor.getString(mCursor.getColumnIndex("telephone"));


                etNumber.setText(initNumber);
                etName.setText(name);
                tvGender.setText(gender);
                tvBirth.setText(birth);
                etNativePlace.setText(nativePlace);
                etSpecialty.setText(specialty);
                etgrade.setText(grade);
                ettelephone.setText(telephoneNumber);
            }
        }
    }


    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        btnBack = (Button) findViewById(R.id.btn_edit_back);
        btnSure = (Button) findViewById(R.id.btn_edit_sure);
        etNumber = (EditText) findViewById(R.id.et_edit_number);
        etName = (EditText) findViewById(R.id.et_edit_name);
        etNativePlace = (EditText) findViewById(R.id.et_edit_native_place);
        etSpecialty = (EditText) findViewById(R.id.et_edit_specialty);
        etgrade = (EditText) findViewById(R.id.et_edit_grade);
        ettelephone = (EditText) findViewById(R.id.et_edit_telephone);
        tvGender = (TextView) findViewById(R.id.tv_edit_gender);
        trGender = (TableRow) findViewById(R.id.tr_edit_gender);
        tvBirth = (TextView) findViewById(R.id.tv_edit_birth);
        trBirth = (TableRow) findViewById(R.id.tr_edit_birth);
        tvDelete = (TextView) findViewById(R.id.tv_edit_delete);
        tvPhoneCall = (ImageButton) findViewById(R.id.phoneCall);

        btnBack.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        trGender.setOnClickListener(this);
        trBirth.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvPhoneCall.setOnClickListener(this);

        switch (currentType) {
            case TYPE_ADD:
                //如果是增添信息模式，就让删除按钮和拨打电话按钮不可见
                tvDelete.setVisibility(View.GONE);
                tvPhoneCall.setVisibility(View.GONE);

                break;
            case TYPE_EDIT:
                tvDelete.setVisibility(View.VISIBLE);
                tvPhoneCall.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_back:
                finish();
                break;
            case R.id.btn_edit_sure:
                btnSureAction();
                break;
            case R.id.tr_edit_gender:
                trGenderAction();
                break;
            case R.id.tr_edit_birth:
                trBirthAction();
                break;
            case R.id.tv_edit_delete:
                tvDeleteAction();
                break;
            case R.id.phoneCall:
                callPhone(telephoneNumber);
                break;
        }
    }
    //拨打电话
    public void callPhone(String telephoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + telephoneNumber);
        intent.setData(data);
        startActivity(intent);
    }

    private void trGenderAction() {
        final String[] arrayGender = new String[]{"男", "女"};
        new AlertDialog.Builder(EditActivity.this)
                .setTitle("修改性别")
                .setItems(arrayGender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvGender.setText(arrayGender[which]);
                    }
                })
                .create()
                .show();
    }

    private void tvDeleteAction() {
        new AlertDialog.Builder(EditActivity.this)
                .setTitle("权限修改")
                .setMessage("确认删除此学生信息？\n学号：" + initNumber)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSQLiteDatabase.delete("student", "number=?", new String[]{initNumber});
                        finish();
                        Toast.makeText(EditActivity.this, "该学生信息删除成功！", Toast.LENGTH_SHORT).show();
                    }
                })
                //“取消”的button我们没有设置点击效果，直接设为null就可以了
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void trBirthAction() {
        final DatePicker dpBirth = (DatePicker) getLayoutInflater().inflate(R.layout.dialog_edit_birth, null);

        new AlertDialog.Builder(EditActivity.this)
                .setTitle("修改出生日期")
                .setView(dpBirth)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //将Activity中的textview显示AlertDialog中EditText中的内容
                        //用Toast显示一下
                        tvBirth.setText(dpBirth.getYear() + "年" + dpBirth.getMonth() + "月" + dpBirth.getDayOfMonth() + "日");
                    }
                })
                //“取消”的button我们没有设置点击效果，直接设为null就可以了
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void btnSureAction() {
        String number = etNumber.getText().toString();
        String name = etName.getText().toString();
        String gender = tvGender.getText().toString();
        String nativePlace = etNativePlace.getText().toString();
        String specialty = etSpecialty.getText().toString();
        String grade = etgrade.getText().toString();
        String birth = tvBirth.getText().toString();
        String telephone=ettelephone.getText().toString();

        if (notNull(number, name, gender, nativePlace, specialty, grade, birth,telephone)) {

                ContentValues values = new ContentValues();
                values.put("number", number);
                values.put("name", name);
                values.put("gender", gender);
                values.put("native_place", nativePlace);
                values.put("specialty", specialty);
                //检查输入的成绩grade是否超过100 ，修改grade 的表示方式
                if(gradeType(grade).equals("typeError")) return;
                grade = gradeType(grade);
                values.put("grade", grade);
                values.put("birth", birth);
                //检查输入的电话号码是否为11位数字以外的格式
                if(!isPhoneNumber(telephone)){
                    Toast.makeText(EditActivity.this, "请输入11位数字作为电话号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                values.put("telephone", telephone);

                switch (currentType) {
                    case TYPE_ADD:
                        mSQLiteDatabase.insert("student", null, values);
                        Toast.makeText(EditActivity.this, "添加数据成功", Toast.LENGTH_SHORT).show();
                        break;
                    case TYPE_EDIT:
                        mSQLiteDatabase.update("student", values, "number=?", new String[]{initNumber});
                        Toast.makeText(EditActivity.this, "数据修改成功", Toast.LENGTH_SHORT).show();
                        break;
                }

                finish();

        } else {
            Toast.makeText(EditActivity.this, "数据不可以为空！", Toast.LENGTH_SHORT).show();
        }
    }

    //如果任何一个EditText中为空，就返回flase
    private boolean notNull(String number, String name, String gender, String nativePlace,
                            String specialty, String grade, String birth,String telephone) {
        if (number.equals(""))
            return false;
        if (name.equals(""))
            return false;
        if (gender.equals(""))
            return false;
        if (nativePlace.equals(""))
            return false;
        if (specialty.equals(""))
            return false;
        if (grade.equals(""))
            return false;
        if (birth.equals(""))
            return false;
        if (telephone.equals(""))
            return false;
        return true;
    }

    private String gradeType(String grade) {
        if (Integer.valueOf(grade) < 10) {
            grade = 0 + grade;
            return grade;
        } else if (Integer.valueOf(grade) > 100) {
            Toast.makeText(EditActivity.this, "分数不能超过100！", Toast.LENGTH_SHORT).show();
            return "typeError";
        } else if (Integer.valueOf(grade) == 100) {
            grade = "满分";
            return grade;
        }else return grade;
    }

    private boolean isPhoneNumber(String phoneNum){
        if(phoneNum.length() == 11){
            for(int i = phoneNum.length();--i>=0;){
                if(!Character.isDigit(phoneNum.charAt(i))){
                    return false;
                }
            }
            return true;
        }
            return false;
    }

}
