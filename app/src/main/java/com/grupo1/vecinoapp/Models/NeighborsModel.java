package com.grupo1.vecinoapp.Models;

public class NeighborsModel {
    private String nId,nName,nParStatus,nNotes;

    public NeighborsModel(String nId, String nName, String nParStatus, String nNotes) {
        this.nId = nId;
        this.nName = nName;
        this.nParStatus = nParStatus;
        this.nNotes = nNotes;
    }

    public NeighborsModel() {
    }

    public String getnId() {
        return nId;
    }

    public void setnId(String nId) {
        this.nId = nId;
    }

    public String getnName() {
        return nName;
    }

    public void setnName(String nName) {
        this.nName = nName;
    }

    public String getnParStatus() {
        return nParStatus;
    }

    public void setnParStatus(String nParStatus) {
        this.nParStatus = nParStatus;
    }

    public String getnNotes() {
        return nNotes;
    }

    public void setnNotes(String nNotes) {
        this.nNotes = nNotes;
    }

}
