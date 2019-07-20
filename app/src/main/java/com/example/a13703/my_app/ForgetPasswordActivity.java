package com.example.a13703.my_app;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a13703.my_app.util.BmobUtil;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button register;
    private EditText phone;
    private EditText password;
    private EditText re_password;
    private EditText code;
    private Button getCode;
    private View view1;
    private View view2;
    private View view3;

    private static final int EDIT_OK = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (EDIT_OK == msg.what) {
               if(BmobUtil.checkEmail(phone.getText().toString())){
                   register.setText("发送修改密码邮件");
                    hideview();
               }else{
                    register.setText("确认");
                    showview();
               }
            }

        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };

    private void hideview(){
        getCode.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        re_password.setVisibility(View.GONE);
        code.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.GONE);
    }
    private void showview(){
        getCode.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        re_password.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
        view3.setVisibility(View.VISIBLE);
    }
    private void BindView(){
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        getCode = (Button)findViewById(R.id.getCode);
        register = (Button)findViewById(R.id.register);
        phone = (EditText)findViewById(R.id.account1);
        password = (EditText)findViewById(R.id.password);
        re_password = (EditText)findViewById(R.id.re_password);
        code = (EditText)findViewById(R.id.code);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            //输入时的调用
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mHandler.removeCallbacks(mRunnable);
                //800毫秒没有输入认为输入完毕
                mHandler.postDelayed(mRunnable, 800);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!"".equals(phone.getText().toString())){
                    BmobUtil.requestBmobSMs(phone.getText().toString());
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option = register.getText().toString();
                String phone1 = phone.getText().toString();
                String password1 = password.getText().toString();
                String re_password1 = re_password.getText().toString();
                String code1 = code.getText().toString();
                if("".equals(phone1)||"".equals(password1)||"".equals(re_password1)||"".equals(code1)){
                    showToast("以上具为为必填项！");
                }else if(!password.getText().toString().equals(re_password.getText().toString())){
                    showToast("两次密码输入不一致！");
                }else if("确认".equals(option)){
                    BmobUtil.resetPasswordByPhone(code.getText().toString(),password.getText().toString());
                    finish();
                } else if("发送修改密码邮件".equals(option)){
                    BmobUtil.resetPasswordByEmail(phone.getText().toString());
                    finish();
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        BindView();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }
    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
