package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static void print(String str){
        System.out.println(str);
    }

    public static List<Integer> findNonZero(INDArray arr1) {
        List<Integer> indices = new ArrayList<>();

        for(int i = 0; i < arr1.length(); i++) {
            Double value = arr1.getDouble(i);
            if(value > 0) {
                indices.add(i);
            }
        }
        return indices;
    }

    public static List<Integer> findNonZero(double arr1[]) {
        List<Integer> indices = new ArrayList<>();

        for(int i = 0; i < arr1.length; i++) {
            if(arr1[i] > 0.0f) {
                indices.add(i);
            }
        }
        return indices;
    }


    public static List<Float> getFloatList(Object value) {
        float[] array = (float[]) value;
        List<Float> result = new ArrayList<Float>(array.length);
        for (float f : array) {
            result.add(Float.valueOf(f));
        }
        return result;
    }

    public static INDArray removeDiscontinuities(INDArray data, float threshold){
        INDArray std = Nd4j.std(data, 0);
        float ji[] = new float[(int)data.shape()[0]];
        for(int i = 1; i < data.shape()[0]; i++) {
            ji[i - 1] = (data.getFloat(i) - data.getFloat(i - 1)) / std.getFloat(0);
        }
        return data;
    }

    public static double getMean(double data[]){
        double sum = 0.0;
        for(double vaule : data){
            sum+=vaule;
        }
        return sum/data.length;
    }

    public static double[] getSlice(double data[], int start, int end){
        assert end > start;
        double slice[] = new double[end-start];
        for(int i = 0; i < slice.length; i++){
            slice[i] = data[start+i];
        }
        return slice;
    }

    public static Triple[] getSlice(Triple data[], int start, int end){
        assert end > start;
        Triple slice[] = new Triple[end-start];
        for(int i = 0; i < data.length; i++){
            slice[i] = data[start+i];
        }
        return slice;
    }

    public static double[] constMul(double data[], double constant){
        double result[] = new double[data.length];
        for(int i = 0; i < data.length; i++){
            result[i] = data[i]*constant;
        }
        return result;
    }

    public static double[] constDiv(double data[], double constant){
        double result[] = new double[data.length];
        for(int i = 0; i < data.length; i++){
            result[i] = data[i]/constant;
        }
        return result;
    }

    public static double[] dot(double data1[], double data2[]){
        assert data1.length == data2.length;
        double result[] = new double[data1.length];
        for(int i = 0; i < data1.length; i++){
            result[i] = data1[i]*data2[i];
        }
        return result;
    }

    public static double[] vecSub(double data1[], double data2[]){
        assert data1.length == data2.length;
        double result[] = new double[data1.length];
        for(int i = 0; i < data1.length; i++){
            result[i] = data1[i] - data2[i];
        }
        return result;
    }

    public static double[] vecAdd(double data1[], double data2[]){
        assert data1.length == data2.length;
        double result[] = new double[data1.length];
        for(int i = 0; i < data1.length; i++){
            result[i] = data1[i] + data2[i];
        }
        return result;
    }

    public static double[] vecDiv(double data1[], double data2[]){
        assert data1.length == data2.length;
        double result[] = new double[data1.length];
        for(int i = 0; i < data1.length; i++){
            result[i] = data1[i] / data2[i];
        }
        return result;
    }

    public static List<Double>[] readCSVData(String file){
        FileReader filereader = null;
        List<String[]> allData = new ArrayList<>();
        try {
            filereader = new FileReader(makeResourcePath(file));
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();

            allData = csvReader.readAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Double> dataFrame[] = new List[4];
        for(int i = 0; i < dataFrame.length; i++){
            dataFrame[i] = new ArrayList<>();
        }

        for (String[] row : allData) {
            if(row.length > 4){
                for (int i = 3; i <= 6; i++) {
                    dataFrame[i - 3].add(Double.parseDouble(row[i]));
                }
            }
        }

        return dataFrame;
    }

    public static double[] linspace(int range){
        double a[] = new double[range];
        for (int i = 0; i < range; i++){
            a[i] = i+1;
        }
        return a;
    }

    private static String makeResourcePath(String template) {
        return AbsorptionCoefficients.class.getResource(template).getPath();
    }

}
