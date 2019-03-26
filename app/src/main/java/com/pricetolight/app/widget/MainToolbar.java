package com.pricetolight.app.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.pricetolight.R;
import com.pricetolight.databinding.ViewMainToolbarBinding;

public class MainToolbar extends FrameLayout {

    ViewMainToolbarBinding binding;

    public MainToolbar(Context context) {
        super(context);
        init();
    }

    public MainToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.view_main_toolbar, this, true);
    }

    public Toolbar getToolbar() {
        return binding.viewToolbar;
    }

    public void setTitle(String title) {
        if (title == null || title == "") {
            binding.toolbarTitle.setVisibility(INVISIBLE);
        } else {
            binding.toolbarTitle.setText(title);
        }
    }


    public void setRightToolbarIcon(Drawable toolbarIcon) {
        if (toolbarIcon == null) {
            binding.rightIcon.setVisibility(GONE);
        }
        binding.rightIcon.setVisibility(VISIBLE);
        binding.rightIcon.setBackground(toolbarIcon);
    }


    public View getRightToolbarIcon() {
        return binding.rightIcon;
    }


    public void setLeftToolbarIcon(Drawable toolbarIcon) {
        if (toolbarIcon == null) {
            binding.leftIcon.setVisibility(GONE);
        }
        binding.leftIcon.setVisibility(VISIBLE);
        binding.leftIcon.setBackground(toolbarIcon);
    }


    public View getLeftToolbarIcon() {
        return binding.leftIcon;
    }

    public void setActionkey(String actionKeyTitle) {
        binding.actionKey.setVisibility(VISIBLE);
        binding.actionKey.setText(actionKeyTitle);
    }

    public View getActionkey() {
        return binding.actionKey;
    }


}