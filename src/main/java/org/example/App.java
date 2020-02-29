package org.example;

import uk.me.berndporr.iirj.Butterworth;

import java.util.ArrayList;
import java.util.List;

public class App
{

    public static void main( String[] args ) {
        AbsorptionCoefficients absorptionCoefficients = new AbsorptionCoefficients();
        Mes2Hb mes2Hb = new Mes2Hb();
        List<Double> data[] = Utils.readCSVData("/fnirs_raw_1570646110.920795.csv");

        List<Double> ir1 = data[0];
        List<Double> red1 = data[1];
        List<Double> ir2 = data[2];
        List<Double> red2 = data[3];

        double aIr1[] = PreProcessing.getAbsorbance(ir1);
        double aRed1[] = PreProcessing.getAbsorbance(red1);
        double aIr2[] = PreProcessing.getAbsorbance(ir2);
        double aRed2[] = PreProcessing.getAbsorbance(red2);

        double odIr1[] = PreProcessing.getOD(aIr1, 860, 24);
        double odRed1[] = PreProcessing.getOD(aRed1, 660, 24);
        double odIr2[] = PreProcessing.getOD(aIr2, 860, 24);
        double odRed2[] = PreProcessing.getOD(aRed2, 660, 24);


        int frameSize = 10;
        Butterworth butterworth = new Butterworth();
        butterworth.bandPass(2, frameSize, 0.01, 1);

        double odIr1Filtered[] = PreProcessing.butterworthBPFilter(butterworth, odIr1);
        double odRed1Filtered[] = PreProcessing.butterworthBPFilter(butterworth, odRed1);
        double odIr2Filtered[] = PreProcessing.butterworthBPFilter(butterworth, odIr2);
        double odRed2Filtered[] = PreProcessing.butterworthBPFilter(butterworth, odRed2);

        double baselines1[] = mes2Hb.getBaseline(odRed1Filtered, odIr1Filtered, 0, 100);
        double baselines2[] = mes2Hb.getBaseline(odRed2Filtered, odIr2Filtered, 0, 100);

        Triple CH1[] = mes2Hb.convert(odRed1Filtered, odIr1Filtered, baselines1[0], baselines1[1]);
        Triple CH2[] = mes2Hb.convert(odRed2Filtered, odIr2Filtered, baselines2[0], baselines2[1]);

        Triple channelData[][] = new Triple[][]{CH1, CH2};

        int stride = 10, windowSize = 10;
        int truncatedLength = CH1.length - CH1.length % windowSize;

        List<Triple[]> slopeCH1 = new ArrayList<>();
        List<Triple[]> slopeCH2 = new ArrayList<>();
        List<Triple[]> slopeFeatures[] = new List[]{slopeCH1, slopeCH2};

        List<Triple[]> avgCH1 = new ArrayList<>();
        List<Triple[]> avgCH2 = new ArrayList<>();
        List<Triple[]> avgFeatures[] = new List[]{avgCH1, avgCH2};

        for (int idx = 0; idx < channelData.length; idx++) {

            for (int i = 0; i < truncatedLength; i += stride) {

                double hbO[], hbR[], hbT[];
                hbO = new double[stride];
                hbR = new double[stride];
                hbT = new double[stride];
                for (int j = 0; j < stride; j++) {
                    hbO[j] = channelData[idx][i + j].hbO;
                    hbR[j] = channelData[idx][i + j].hbR;
                    hbT[j] = channelData[idx][i + j].hbT;
                }

                Triple slope1H = new Triple(
                        PreProcessing.getSlope(
                                Utils.linspace(stride / 2),
                                Utils.getSlice(hbO, 0, stride / 2)
                        ),
                        PreProcessing.getSlope(
                                Utils.linspace(stride / 2),
                                Utils.getSlice(hbR, 0, stride / 2)
                        ),
                        PreProcessing.getSlope(
                                Utils.linspace(stride / 2),
                                Utils.getSlice(hbT, 0, stride / 2)
                        )
                );
                Triple slope2H = new Triple(
                        PreProcessing.getSlope(
                                Utils.linspace(stride / 2),
                                Utils.getSlice(hbO, stride / 2, stride)
                        ),
                        PreProcessing.getSlope(
                                Utils.linspace(stride / 2),
                                Utils.getSlice(hbR, stride / 2, stride)
                        ),
                        PreProcessing.getSlope(
                                Utils.linspace(stride / 2),
                                Utils.getSlice(hbT, stride / 2, stride)
                        )
                );
                Triple slope1H2H[] = new Triple[]{slope1H, slope2H};
                slopeFeatures[idx].add(slope1H2H);

                Triple avg1H = new Triple(
                        PreProcessing.getMean(
                                Utils.getSlice(hbO, 0, stride / 2)
                        ),
                        PreProcessing.getMean(
                                Utils.getSlice(hbR, 0, stride / 2)
                        ),
                        PreProcessing.getMean(
                                Utils.getSlice(hbT, 0, stride / 2)
                        )
                );
                Triple avg2H = new Triple(
                        PreProcessing.getMean(
                                Utils.getSlice(hbO, stride / 2, stride)
                        ),
                        PreProcessing.getMean(
                                Utils.getSlice(hbR, stride / 2, stride)
                        ),
                        PreProcessing.getMean(
                                Utils.getSlice(hbT, stride / 2, stride)
                        )
                );
                Triple avg1H2H[] = new Triple[]{avg1H, avg2H};
                avgFeatures[idx].add(avg1H2H);
            }
        }

    }
}
