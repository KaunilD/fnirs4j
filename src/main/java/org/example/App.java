package org.example;
import sun.nio.ch.Util;
import uk.me.berndporr.iirj.Butterworth;

import java.util.ArrayList;
import java.util.List;

public class App
{

    public static void main( String[] args )
    {
        AbsorptionCoefficients absorptionCoefficients = new AbsorptionCoefficients();
        Mes2Hb mes2Hb = new Mes2Hb();
        List<Double> data[] = Utils.readCSVData("/fnirs_raw_1570646110.920795.csv");

        List<Double> ir1    = data[0];
        List<Double> red1   = data[1];
        List<Double> ir2    = data[2];
        List<Double> red2   = data[3];

        double aIr1 [] = PreProcessing.getAbsorbance(ir1);
        double aRed1[] = PreProcessing.getAbsorbance(red1);
        double aIr2 [] = PreProcessing.getAbsorbance(ir2);
        double aRed2[] = PreProcessing.getAbsorbance(red2);

        double odIr1    [] = PreProcessing.getOD(aIr1, 860, 24);
        double odRed1   [] = PreProcessing.getOD(aRed1, 660, 24);
        double odIr2    [] = PreProcessing.getOD(aIr2, 860, 24);
        double odRed2   [] = PreProcessing.getOD(aRed2, 660, 24);

        int frameSize = 10;
        Butterworth butterworth = new Butterworth();
        butterworth.bandPass(2, frameSize, 0.01, 1);

        double odIr1Filtered    [] = PreProcessing.butterworthBPFilter(butterworth, odIr1);
        double odRed1Filtered   [] = PreProcessing.butterworthBPFilter(butterworth, odRed1);
        double odIr2Filtered    [] = PreProcessing.butterworthBPFilter(butterworth, odIr2);
        double odRed2Filtered   [] = PreProcessing.butterworthBPFilter(butterworth, odRed2);

        double baselines1[] = mes2Hb.getBaseline(odRed1Filtered, odIr1Filtered, 0, 100);
        double baselines2[] = mes2Hb.getBaseline(odRed2Filtered, odIr2Filtered, 0, 100);

        double hbORT1[][] = mes2Hb.convert(odRed1Filtered, odIr1Filtered, baselines1[0], baselines1[1]);
        double hbORT2[][] = mes2Hb.convert(odRed2Filtered, odIr2Filtered, baselines2[0], baselines2[1]);

        int stride = 10, windowSize = 10;
        int truncatedLength = hbORT1.length - hbORT1.length%windowSize;
        List<Double> slopeFeature1[] = new List[2];
        slopeFeature1[0] = new ArrayList<Double>();
        slopeFeature1[1] = new ArrayList<Double>();
        List<Double> slopeFeature2[] = new List[2];
        slopeFeature2[0] = new ArrayList<Double>();
        slopeFeature2[1] = new ArrayList<Double>();


        for(int i = 0; i < truncatedLength; i+=stride ){
            double mc[] = PreProcessing.getSlope(Utils.linspace(10), Utils.getSlice(hbORT1[0], i, i+stride));
        }
    }
}
