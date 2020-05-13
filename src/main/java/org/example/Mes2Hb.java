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

    Triple[] convert(double[] rMesData, double[] irMesData, double redBaseline, double irBaseline){
        assert rMesData.length == irMesData.length;


        float redWavelength = 690.0f, irWavelength = 830.0f;
        Double oxyRed, oxyIr, dxyRed, dxyIr;

        oxyRed = absorptionCoefficients.getCoefficient(redWavelength+"", "oxy");
        oxyIr = absorptionCoefficients.getCoefficient(irWavelength+"", "oxy");
        dxyRed = absorptionCoefficients.getCoefficient(redWavelength+"", "dxy");
        dxyIr = absorptionCoefficients.getCoefficient(irWavelength+"", "dxy");
        double aRed[] = new double[rMesData.length];
        List<Integer> redPositions = Utils.findGtZero(Utils.constMul(rMesData, redBaseline));
        for (int i = 0; i < aRed.length; i++){
            if (redPositions.contains(i)){
                aRed[i] = Math.log(redBaseline/rMesData[i]);
            }else{
                aRed[i] = 0;
            }
        }

        double aIr[] = new double[irMesData.length];
        List<Integer> irPositions = Utils.findGtZero(Utils.constMul(irMesData, irBaseline));
        for (int i = 0; i < aIr.length; i++){
            // Utils.print(irBaseline + " " +" "+irMesData[i] + Math.log(irBaseline/irMesData[i]));
            if (irPositions.contains(i)){
                aIr[i] = Math.log(irBaseline/irMesData[i]);
            }else{
                aIr[i] = 0;
            }
            //Utils.print(aIr[i] + "");
        }

        Triple result[] = new Triple[rMesData.length];

        double hbO, hbR, hbT;

        double hbODenominator = oxyRed*dxyIr - oxyIr*dxyRed;
        double hbRDenominator = dxyRed*oxyIr - dxyIr*oxyRed;

        if(hbODenominator != 0 && hbRDenominator !=0){
            for(int i = 0; i < rMesData.length; i++){
                hbO = (aRed[i]*dxyIr - aIr[i]*dxyRed)/hbODenominator;
                hbR = (aRed[i]*oxyIr - aIr[i]*oxyRed)/hbRDenominator;
                hbT = hbO+hbR;
                Utils.print(aRed[i]*dxyIr - aIr[i]*dxyRed +" "+aRed[i] + " " + aIr[i] + " " + dxyIr + " " + dxyRed + " " + oxyIr + " " + oxyRed );
                result[i] = new Triple(hbO, hbR, hbT);
            }

        }

        return result;
    }
}
