package org.example;

public class Triple {
    public double hbO, hbR, hbT;

    public Triple(double hbO, double hbR, double hbT) {
        this.hbO = hbO;
        this.hbR = hbR;
        this.hbT = hbT;
    }

    public double getHbO() {
        return hbO;
    }

    public void setHbO(double hbO) {
        this.hbO = hbO;
    }

    public double getHbR() {
        return hbR;
    }

    public void setHbR(double hbR) {
        this.hbR = hbR;
    }

    public double getHbT() {
        return hbT;
    }

    public void setHbT(double hbT) {
        this.hbT = hbT;
    }

    public void print(){
        System.out.println("Triple:: " + this.hbO + " " + this.hbR  + " " + this.hbT );
    }
}
