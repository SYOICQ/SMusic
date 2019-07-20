package com.example.a13703.my_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a13703.my_app.Inter_kou.OnquerListener;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.util.BmobUtil;

import cn.bmob.v3.exception.BmobException;

public class EditSignNatureActivity extends AppCompatActivity {

    private ImageView edit_signature_back;
    private TextView edit_signature_save;
    private EditText new_signature;
    private MyApplication App;

    private void init(){
        App = MyApplication.getInstance();
        edit_signature_back = (ImageView)findViewById(R.id.edit_signature_back);
        edit_signature_save = (TextView)findViewById(R.id.edit_signature_save);
        new_signature = (EditText)findViewById(R.id.new_signature);
        edit_signature_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edit_signature_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newsignature = new_signature.getText().toString();
                if(!"".equals(newsignature)){
                     BmobUtil.updateSignature(App.currentUser.getObjectId(),newsignature);
                     BmobUtil.queryInformation(App.currentUser.getObjectId(), new OnquerListener() {
                        @Override
                        public void OnqueryListenerSucess(Person person, BmobException e) {
                            App.currentUser = person;
                        }
                    });
                    finish();
                }else
                    BmobUtil.showToast("不能为空!");
            }
        });
    }

    private void refresh(){
        if(!"".equals(App.currentUser.getPersonal_signature())){
            new_signature.setText(App.currentUser.getPersonal_signature());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sign_nature);
        init();
    }
    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }
}
