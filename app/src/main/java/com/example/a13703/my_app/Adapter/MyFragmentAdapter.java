package com.example.a13703.my_app.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Choreographer;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 13703 on 2019/6/12.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> fragmentTitles = new ArrayList<>();
    public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList){
        super(fm);
        this.fragmentList = fragmentList;
    }
    public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragments,List<String>fragmentTitles){
        super(fm);
        this.fragmentList = fragments;
        this.fragmentTitles = fragmentTitles;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
    @Override
    public void destroyItem(ViewGroup container,int position,Object object){
        super.destroyItem(container,position,object);
    }
    @Override
    public CharSequence getPageTitle(int position){
        if(fragmentTitles!=null){
            return fragmentTitles.get(position);
        }else{
            return "";
        }
    }
}
