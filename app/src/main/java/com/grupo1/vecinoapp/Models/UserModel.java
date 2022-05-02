package com.grupo1.vecinoapp.Models;

public class UserModel {
    private String uid,uName,uEmail,uPassword,uDNI,uAddress,uImage,uDate;
    private boolean selected;

    public UserModel(String uid, String uName, String uEmail, String uPassword, String uDNI, String uAddress, String uImage, String uDate) {
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPassword = uPassword;
        this.uDNI = uDNI;
        this.uAddress = uAddress;
        this.uImage = uImage;
        this.uDate = uDate;
    }

    public String getuDate() {
        return uDate;
    }

    public void setuDate(String uDate) {
        this.uDate = uDate;
    }

    public String getuImage() {
        return uImage;
    }

    public UserModel(String uid, String uName, String uEmail, String uPassword, String uDNI, String uAddress, String uImage) {
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPassword = uPassword;
        this.uDNI = uDNI;
        this.uAddress = uAddress;
        this.uImage = uImage;
    }

    public void setuImage(String uImage) {
        this.uImage = uImage;
    }

    public UserModel() {
    }
    public UserModel(boolean selected) {
        this.selected = selected;
    }

    public UserModel(String uid, String uName, String uEmail, String uPassword, String uDNI, String uAddress) {
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPassword = uPassword;
        this.uDNI = uDNI;
        this.uAddress = uAddress;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuPassword() {
        return uPassword;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public String getuDNI() {
        return uDNI;
    }

    public void setuDNI(String uDNI) {
        this.uDNI = uDNI;
    }

    public String getuAddress() {
        return uAddress;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setuAddress(String uAddress) {
        this.uAddress = uAddress;
    }
}
