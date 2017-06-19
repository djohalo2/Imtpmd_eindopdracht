package com.example.djohalo2.imtpmd_eindopdracht.Models;

public class Vak {

    private String naam;
    private double cijfer;
    private int jaar;
    private int ec;
    private boolean gehaald;
    private boolean gevolgd;
    private boolean keuzevak;
    private int periode;

    public Vak() {

    }

    public int getJaar() {
        return jaar;
    }

    public void setJaar(int jaar) {
        this.jaar = jaar;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public double getCijfer() {
        return cijfer;
    }

    public void setCijfer(double cijfer) {
        this.cijfer = cijfer;
    }

    public int getEc() {
        return ec;
    }

    public void setEc(int ec) {
        this.ec = ec;
    }

    public boolean isGehaald() {
        return gehaald;
    }

    public void setGehaald(boolean gehaald) {
        this.gehaald = gehaald;
    }

    public boolean isGevolgd() {
        return gevolgd;
    }

    public void setGevolgd(boolean gevolgd) {
        this.gevolgd = gevolgd;
    }

    public boolean isKeuzevak() {
        return keuzevak;
    }

    public void setKeuzevak(boolean keuzevak) {
        this.keuzevak = keuzevak;
    }

    public int getPeriode() {
        return periode;
    }

    public void setPeriode(int periode) {
        this.periode = periode;
    }

    @Override
    public String toString() {
        return naam + " " + ec;
    }
}
