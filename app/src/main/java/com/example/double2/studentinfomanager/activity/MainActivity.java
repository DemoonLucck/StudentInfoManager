package com.example.double2.studentinfomanager.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.adapter.ShowAdapter;
import com.example.double2.studentinfomanager.db.StudentDateBaseHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    //控件
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private RecyclerView rvMain;
    private FloatingActionButton fabtnAdd;
    private FloatingActionButton fabtnSort;
    private FloatingActionButton fabtnCount;
    private TextView tvName;
    //数据存储
    private StudentDateBaseHelper mStudentDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private SharedPreferences mSharedPreferences;
    private static int flag;
    private static boolean changeOrder = false;
    private static int clickCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mStudentDateBaseHelper = new StudentDateBaseHelper(this, "StudentInfo.db", null, 1);
        mSQLiteDatabase = mStudentDateBaseHelper.getReadableDatabase();

        mSharedPreferences = this.getSharedPreferences("student", MODE_PRIVATE);

        setTestData();
        initView();
        receiveFlag();
    }
    private void receiveFlag() {
        Intent intent = this.getIntent();
        flag = intent.getIntExtra("flag", 0);
    }

    //预置学生信息
    private void setTestData() {
        Boolean isFirstStart = mSharedPreferences.getBoolean("is_first_start", true);
        if (isFirstStart) {
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("is_first_start", false);
            mEditor.commit();
            ArrayList<String> number = new ArrayList<>();
            ArrayList<String> name = new ArrayList<>();
            ArrayList<String> grade = new ArrayList<>();
            ArrayList<String> telephone = new ArrayList<>();
            String gender = "女";
            String nativePlace = "云南昆明";
            String specialty = "计算机";
            String birth = "1997年1月1日";
            String password = "1";
            String telephone3="13312345678";

            number.add("16051098");
            name.add("宋钱");
            grade.add("49");
            number.add("16051001");
            name.add("赵一");
            grade.add("54");
            number.add("16051002");
            name.add("钱二");
            grade.add("23");
            number.add("16051003");
            name.add("孙三");
            grade.add("78");
            number.add("16051004");
            name.add("李四");
            grade.add("99");
            number.add("16051005");
            name.add("周五");
            grade.add("42");
            number.add("16051006");
            name.add("吴六");
            grade.add("34");
            number.add("16051007");
            name.add("郑七");
            grade.add("96");
            number.add("16051008");
            name.add("王八");
            grade.add("12");

            for (int i = 0; i < number.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("number", number.get(i));
                values.put("name", name.get(i));
                values.put("gender", gender);
                values.put("native_place", nativePlace);
                values.put("specialty", specialty);
                values.put("grade", grade.get(i));
                values.put("birth", birth);
                values.put("password",password);
                values.put("telephone", telephone3);

                mSQLiteDatabase.insert("student", null, values);
            }
        }
    }


    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //将状态栏颜色设置为与toolbar一致
            getWindow().setStatusBarColor(getResources().getColor(R.color.holo_blue_light));
        }
        setToolBar();
        setNavigationView();
        rvMain = (RecyclerView) findViewById(R.id.rv_main);
        rvMain.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        fabtnAdd = (FloatingActionButton) findViewById(R.id.fabtn_main_add);
        fabtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag == 0){
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    intent.putExtra("type", EditActivity.TYPE_ADD);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabtnSort = (FloatingActionButton) findViewById(R.id.fabtn_main_sort);
        //点击排序按钮
        fabtnSort.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                sort();
                changeOrder = !(changeOrder);
                Toast toast = Toast.makeText(MainActivity.this, "按"+(changeOrder?"升序":"降序")+"排序成功", Toast.LENGTH_SHORT);
                showMyToast(toast,500);
            }
        });
        fabtnCount = (FloatingActionButton) findViewById(R.id.fabtn_main_count);
        fabtnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CountActivity.class);
                startActivity(intent);
            }
        });

    }

    protected void onStart() {
        super.onStart();
        refreshRecyclerView();
    }
    //排序函数
    private void sort() {
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> grade = new ArrayList<>();
        Cursor mCursor;
        if(!changeOrder){
            mCursor = mSQLiteDatabase.query("student",null,null,null,null,null,"grade ASC");
        }else {
            mCursor = mSQLiteDatabase.query("student", null, null, null, null, null, "grade DESC");
        }
        int size = mCursor.getCount() < ShowAdapter.maxSize ? mCursor.getCount() : ShowAdapter.maxSize;


        while (true) {
            if (size-- == 0)
                break;
            mCursor.moveToNext();
            number.add(mCursor.getString(mCursor.getColumnIndex("number")));
            name.add(mCursor.getString(mCursor.getColumnIndex("name")));
            grade.add(mCursor.getString(mCursor.getColumnIndex("grade")));
        }
        mCursor.close();
        rvMain.setAdapter(new ShowAdapter(MainActivity.this, number, name, grade,flag));

    }
    //遍历数据库来显示列表
    private void refreshRecyclerView() {
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> grade = new ArrayList<>();

        Cursor mCursor = mSQLiteDatabase.query("student", null, null, null, null, null, null);
        //mCursor.getCount() 返回mCursor中的行数
        int size = mCursor.getCount() < ShowAdapter.maxSize ? mCursor.getCount() : ShowAdapter.maxSize;

        while (true) {
            if (size-- == 0)
                break;
            //移动到数据库下一行
            mCursor.moveToNext();
            number.add(mCursor.getString(mCursor.getColumnIndex("number")));
            name.add(mCursor.getString(mCursor.getColumnIndex("name")));
            grade.add(mCursor.getString(mCursor.getColumnIndex("grade")));
        }
        mCursor.close();

        //设置列表
        rvMain.setAdapter(new ShowAdapter(MainActivity.this, number, name, grade,flag));
    }

    private void setNavigationView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
        mNavigationView = (NavigationView) findViewById(R.id.nv_main_menu);
        setupDrawerContent(mNavigationView);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle("学生信息");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchAction();
                return false;
            }
        });
    }
    //显示菜单视图
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent,intent2;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_my_info:
                                intent=new Intent(MainActivity.this,MyInfoActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_password:
                                changePasswordDialog();
                                break;
                            case R.id.nav_logout:
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("确认退出吗？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent1 = new Intent(MainActivity.this,LogInActivity.class);
                                        startActivity(intent1);
                                    }
                                    }).setNegativeButton("取消", null)
                                        .create()
                                        .show();

                                break;
                            case R.id.nav_search:
                                searchAction();
                                break;
                            case R.id.nav_add:
                                if(flag == 0){
                                    intent2 = new Intent(MainActivity.this, EditActivity.class);
                                    intent2.putExtra("type", EditActivity.TYPE_ADD);
                                    startActivity(intent2);
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
    //菜单-修改密码功能
    private void changePasswordDialog() {
        final TableLayout tlPassword = (TableLayout) getLayoutInflater().inflate(R.layout.dialog_main_password, null);
        final EditText etOldPassword = (EditText) tlPassword.findViewById(R.id.et_main_old_password);
        final EditText etNewPassword = (EditText) tlPassword.findViewById(R.id.et_main_new_password);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("修改登录密码")
                .setView(tlPassword)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String myaccount = mSharedPreferences.getString("account", "9998");
                        if(myaccount.equals("9998")){
                            Toast.makeText(MainActivity.this, "账号获取失败", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String[] my_account ={myaccount};
                            Cursor mCursor = mSQLiteDatabase.query("student", null, "number = ?", my_account, null, null, null);
                            String mypassword="9999";
                            while(mCursor.moveToNext()){
                                mypassword = mCursor.getString(mCursor.getColumnIndex("password"));
                            }
                            if(mypassword.equals(etOldPassword.getText().toString())){
                                String newpassword = etNewPassword.getText().toString();
                                ContentValues values = new ContentValues();
                                values.put("password",newpassword);
                                mSQLiteDatabase.update("student",values,"number = ?",my_account);
                                Toast.makeText(MainActivity.this, "修改密码成功！", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "原密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }
    //菜单-查询信息功能
    private void searchAction() {
        final String[] arrayGender = new String[]{"学号", "姓名"};
        if (flag == 1){
                Toast toast = Toast.makeText(MainActivity.this, "没有权限", Toast.LENGTH_SHORT);
                showMyToast(toast,500);
        }else {
                new AlertDialog.Builder(MainActivity.this)
                .setTitle("搜索类型")
                .setItems(arrayGender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        switch (which) {
                            case 0:
                                intent.putExtra("search_type", SearchActivity.TYPE_SEARCH_NUMBER);
                                break;
                            case 1:
                                intent.putExtra("search_type", SearchActivity.TYPE_SEARCH_NAME);
                                break;
                        }
                            startActivity(intent);

                    }
                })
                .create()
                .show();
             }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getName();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //获取账户姓名来显示在导航栏
    private void getName() {
        String myaccount = mSharedPreferences.getString("account", "9998");
        String myname = "管理员";
        if(flag == 1){
            if (myaccount.equals("9998")) {
                Toast.makeText(MainActivity.this, "账号获取失败", Toast.LENGTH_SHORT).show();
            } else {
                String[] my_account = {myaccount};
                Cursor mCursor = mSQLiteDatabase.query("student", null, "number = ?", my_account, null, null, null);
                while (mCursor.moveToNext()) {
                    myname = mCursor.getString(mCursor.getColumnIndex("name"));
                }

            }
        }
        tvName = (TextView) findViewById(R.id.username);
        tvName.setText(myname);

    }
    public static void showMyToast(final Toast toast, final int cnt) {
        final Timer timer;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

}