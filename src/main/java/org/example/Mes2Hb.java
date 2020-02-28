package org.example;

import java.util.List;

public class Mes2Hb {

    AbsorptionCoefficients absorptionCoefficients;

    Mes2Hb(){
        absorptionCoefficients = new AbsorptionCoefficients();
    }

    double[] getBaseline(double[] rMesData, double[] irMesData, int lIdx, int uIdx){
        assert rMesData.length == irMesData.length;

        double redSlice[] = Utils.getSlice(rMesData, lIdx, uIdx);
        double iRSlice[] = Utils.getSlice(irMesData, lIdx, uIdx);

        double baselines[] = new double[]{Utils.getMean(redSlice), Utils.getMean(iRSlice)};

        return baselines;
    }

    double[][] convert(double[] rMesData, double[] irMesData, double redBaseline, double irBaseline){
        assert rMesData.length == irMesData.length;


        float redWavelength = 690.0f, irWavelength = 830.0f;
        Double oxyRed, oxyIr, dxyRed, dxyIr;

        oxyRed = absorptionCoefficients.getCoefficient(redWavelength+"", "oxy");
        oxyIr = absorptionCoefficients.getCoefficient(irWavelength+"", "oxy");
        dxyRed = absorptionCoefficients.getCoefficient(redWavelength+"", "dxy");
        dxyIr = absorptionCoefficients.getCoefficient(irWavelength+"", "dxy");

        double aRed[] = new double[rMesData.length];
        List<Integer> redPositions = Utils.findNonZero(Utils.constMul(rMesData, redBaseline));
        for (int i = 0; i < aRed.length; i++){
            if (redPositions.contains(i)){
                aRed[i] = Math.log(redBaseline/rMesData[i]);
            }
        }

        double aIr[] = new double[irMesData.length];
        List<Integer> irPositions = Utils.findNonZero(Utils.constMul(irMesData, irBaseline));
        for (int i = 0; i < aIr.length; i++){
            if (irPositions.contains(i)){
                aIr[i] = Math.log(irBaseline/irMesData[i]);
            }
        }

        double hbO[], hbR[], hbT[];
        hbO = new double[rMesData.length];
        hbR = new double[rMesData.length];
        hbT = new double[rMesData.length];

        // (a_red*dxy_ir - a_ir*dxy_red)/(oxy_red*dxy_ir - oxy_ir*dxy_red)
        if(oxyRed*dxyIr - oxyIr*dxyRed != 0){
            hbO = Utils.constDiv(
                Utils.vecSub(Utils.constMul(aRed, dxyIr),  Utils.constMul(aIr, dxyRed)),
                oxyRed*dxyIr - oxyIr*dxyRed
            );
        }
        // (a_red*oxy_ir - a_ir*oxy_red)/(dxy_red*oxy_ir - dxy_ir*oxy_red)
        if(dxyRed*oxyIr - dxyIr*oxyRed != 0){
            hbR = Utils.constDiv(
                    Utils.vecSub(Utils.constMul(aRed, oxyIr),  Utils.constMul(aIr, oxyRed)),
                    dxyRed*oxyIr - dxyIr*oxyRed
            );
        }

        hbT = Utils.vecAdd(hbO, hbR);

        double result[][] = new double[3][hbO.length];
        result[0] = hbO;
        result[1] = hbR;
        result[2] = hbT;
        return result;
    }
}
