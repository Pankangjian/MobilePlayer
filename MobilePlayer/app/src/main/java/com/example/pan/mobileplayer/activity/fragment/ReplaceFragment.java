package com.example.pan.mobileplayer.activity.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pan.mobileplayer.activity.base.BasePager;

/**
 * Created by pan on 2018/9/9.
 */
public class ReplaceFragment extends Fragment {
    private BasePager insteadPager;

    public ReplaceFragment(BasePager pager){
        this.insteadPager = pager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return insteadPager.rootView;
    }
}
   /*private BasePager insteadPager;
     public ReplaceFragment(BasePager pager){
       this.insteadPager = pager;
   }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
            BasePager insteadPager = getBasePager();
            if(insteadPager!= null){
                //各个页面视图
                return insteadPager.rootView;
            }
            return null;
        }

    public BasePager getBasePager() {
        return insteadPager;
    }
}*/