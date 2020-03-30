package org.example;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import uk.me.berndporr.iirj.Butterworth;

import java.util.List;

public class PreProcessing {

    public static double[] getAbsorbance(List<Double>data){
        double emittorCurrent = 5*10e4;

        double absorbance[] = new double[data.size()];
        for (int i = 0; i < data.size(); i++){
            if(data.get(i) > 0.0) {
                absorbance[i] = data.get(i)/emittorCurrent;
                Utils.print(absorbance[i]+" "+ data.get(i));
                absorbance[i] = -Math.log10(absorbance[i]);
            }
        }
        return absorbance;
    }

    public static double getDPF(int age, int wavelength){
        return  223.3f + 0.05624f * Math.pow(age, 0.8493f) -
                5.723f * 10e-7f * Math.pow(wavelength, 3) +
                0.001245f * Math.pow(wavelength, 2) - 0.9025f * wavelength;
    }

    public static double[] getOD(double absorbance[], int wavelength, int age){
        double dpf = getDPF(age, wavelength);
        double OD[] = new double[absorbance.length];
        for (int i = 0; i < absorbance.length; i++){
            OD[i] = absorbance[i]/dpf;
        }
        return OD;
    }

    public static double[] butterworthBPFilter(Butterworth butterworth, double data[]){
        double filteredData[] = new double[data.length];
        for (int i = 0; i < data.length; i++){
            filteredData[i] = butterworth.filter(data[i]);
        }
        return filteredData;
    }

    public static double getSlope(double x[], double y[]){
        int n = x.length;
        double m, c, sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {

            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += Math.pow(x[i], 2);
        }
        m = (n * sumXY - sumX * sumY) / (n * sumX2 - Math.pow(sumX, 2));
        c = (sumY - m * sumX) / n;
        return m;
    }

    public static double getMean(double data[]){
        double sum = 0;
        for(double value: data){
            sum+=value;
        }
        return sum/data.length;
    }


}
