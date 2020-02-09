package com.pricetolight.app.main.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import androidx.legacy.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class HueConnectFragmentAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragments = new ArrayList<>();

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
    }

    public HueConnectFragmentAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
