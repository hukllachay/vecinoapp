package com.grupo1.vecinoapp.Models;

public class InitiativesModel {
    private String iId, iTitle, iDateFrom, iDateTo, iTipo, iStatus, iDescription, uid, iDate, iLat, iLng, iAddress;

    public String getiLat() {
        return iLat;
    }

    public void setiLat(String iLat) {
        this.iLat = iLat;
    }

    public String getiLng() {
        return iLng;
    }

    public void setiLng(String iLng) {
        this.iLng = iLng;
    }

    public String getiAddress() {
        return iAddress;
    }

    public void setiAddress(String iAddress) {
        this.iAddress = iAddress;
    }

    public InitiativesModel(String iId, String iTitle, String iDateFrom, String iDateTo, String iTipo, String iStatus, String iDescription, String uid, String iDate, String iLat, String iLng, String iAddress) {
        this.iId = iId;
        this.iTitle = iTitle;
        this.iDateFrom = iDateFrom;
        this.iDateTo = iDateTo;
        this.iTipo = iTipo;
        this.iStatus = iStatus;
        this.iDescription = iDescription;
        this.uid = uid;
        this.iDate = iDate;
        this.iLat = iLat;
        this.iLng = iLng;
        this.iAddress = iAddress;
    }

    public InitiativesModel(String iId, String iTitle, String iDateFrom, String iDateTo, String iTipo, String iStatus, String iDescription, String uid, String iDate) {
        this.iId = iId;
        this.iTitle = iTitle;
        this.iDateFrom = iDateFrom;
        this.iDateTo = iDateTo;
        this.iTipo = iTipo;
        this.iStatus = iStatus;
        this.iDescription = iDescription;
        this.uid = uid;
        this.iDate = iDate;
    }

    public String getiDate() {
        return iDate;
    }

    public void setiDate(String iDate) {
        this.iDate = iDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getiDescription() {
        return iDescription;
    }

    public void setiDescription(String iDescription) {
        this.iDescription = iDescription;
    }

    public InitiativesModel() {
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getiTitle() {
        return iTitle;
    }

    public void setiTitle(String iTitle) {
        this.iTitle = iTitle;
    }

    public String getiDateFrom() {
        return iDateFrom;
    }

    public void setiDateFrom(String iDateFrom) {
        this.iDateFrom = iDateFrom;
    }

    public String getiDateTo() {
        return iDateTo;
    }

    public void setiDateTo(String iDateTo) {
        this.iDateTo = iDateTo;
    }

    public String getiTipo() {
        return iTipo;
    }

    public void setiTipo(String iTipo) {
        this.iTipo = iTipo;
    }

    public String getiStatus() {
        return iStatus;
    }

    public void setiStatus(String iStatus) {
        this.iStatus = iStatus;
    }
}
