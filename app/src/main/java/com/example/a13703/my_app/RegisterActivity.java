package com.example.a13703.my_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.util.BmobUtil;

public class RegisterActivity extends AppCompatActivity {

    private Button register;
    private EditText phone;
    private EditText password;
    private EditText re_password;
    private EditText code;
    private Button getCode;

    private void BindView(){
        getCode = (Button)findViewById(R.id.getCode);
        register = (Button)findViewById(R.id.register);
        phone = (EditText)findViewById(R.id.account1);
        password = (EditText)findViewById(R.id.password);
        re_password = (EditText)findViewById(R.id.re_password);
        code = (EditText)findViewById(R.id.code);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone1 = phone.getText().toString();
                String password1 = password.getText().toString();
                String re_password1 = re_password.getText().toString();
                String code1 = code.getText().toString();
                if("".equals(phone1)||"".equals(password1)||"".equals(re_password1)||"".equals(code1)){
                    showToast("以上具为为必填项！");
                }else if(!password1.equals(re_password1)){
                    showToast("两次密码输入不一致！");
                }else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p1);
                    BmobUtil.signUp(phone1,password1,code1,bitmap);
                    finish();
                }
            }
        });
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone1 = phone.getText().toString();
                if(!"".equals(phone1)&&phone1.length()==11) {
                    BmobUtil.requestBmobSMs(phone1);
                }else{
                    showToast("手机号错误！");
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        BindView();
    }
    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
