package com.example.a13703.my_app;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private List<Integer> photo_list;
    private ViewPager viewPager ;

    private void init(){
        photo_list= new ArrayList<>();
        photo_list.add(R.drawable.p1);
        photo_list.add(R.drawable.p2);
        photo_list.add(R.drawable.p3);
        photo_list.add(R.drawable.p4);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //Log.d("WelcomeActivity", "photo_list.size():" + photo_list.size());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_welcome);
        init();
        ViewAdapter viewAdapter = new ViewAdapter(getApplicationContext(),photo_list);
        viewPager.setAdapter(viewAdapter);
    }
    public class ViewAdapter extends PagerAdapter{

        private List<View> data = new ArrayList<>();
        private Context mContext;

        public ViewAdapter(Context context,List<Integer> list) {
            mContext = context;
            for(int i=0;i<list.size();i++){
                View view = LayoutInflater.from(mContext).inflate(R.layout.welcome_page,null);
                ImageView welcome_image = (ImageView) view.findViewById(R.id.welcome_image);
                RadioGroup welcome_group = (RadioGroup) view.findViewById(R.id.welcome_radio);
                Button welcome_button = (Button) view.findViewById(R.id.welcome_start);
                welcome_image.setImageResource(list.get(i));
                for(int j=0;j<list.size();j++){
                    RadioButton radio = new RadioButton(mContext);
                    radio.setLayoutParams(
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    );
                    radio.setButtonDrawable(R.drawable.welcome_radio);
                    radio.setPadding(10,10,10,10);
                    welcome_group.addView(radio);
                }
                ((RadioButton)welcome_group.getChildAt(i)).setChecked(true);
                if(i==list.size()-1){
                    welcome_button.setVisibility(View.VISIBLE);
                    welcome_button.getBackground().setAlpha(150);
                    welcome_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(mContext,"欢迎开启美好生活",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                data.add(view);
            }
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=data.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }

    }

}

