package com.example.newbike.view;

import com.flyco.tablayout.listener.CustomTabEntity;

public class ImageTabEntity implements CustomTabEntity {

    private String title;
    private int selectedIcon;
    private int unSelectIcon;

    public ImageTabEntity(String title, int selectedIcon, int unSelectIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectIcon = unSelectIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectIcon;
    }
}
