package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AbsorptionCoefficients {
    String coefficientsFilePath = null;
    JSONParser jsonParser = null;
    JSONObject oxy, dxy;
    FileReader fileReader = null;
    AbsorptionCoefficients() {
        coefficientsFilePath = makeResourcePath("/coefficients.json");
        jsonParser = new JSONParser();

        try{
            fileReader = new FileReader(new File(coefficientsFilePath.substring(1)));
            Object object = jsonParser.parse(
                    fileReader
            );

            JSONObject jsonObject = (JSONObject) object;

            oxy = (JSONObject) jsonObject.get("oxy");
            dxy = (JSONObject) jsonObject.get("dxy");

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Double getCoefficient(String wavelength, String hbType){
        Double wav = Double.parseDouble(wavelength);
        Double lWav = Math.floor(wav);

        Double uWav = lWav + 1.0;
        Double lCoeff, uCoeff;

        if (hbType.equals("oxy")){
            lCoeff = Double.parseDouble(
                    oxy.get(lWav.intValue() + "").toString());

            uCoeff = Double.parseDouble(
                    oxy.get(uWav.intValue() + "").toString());
        }else{
            lCoeff = Double.parseDouble(
                    dxy.get(lWav.intValue() + "").toString());

            uCoeff = Double.parseDouble(
                    dxy.get(uWav.intValue() + "").toString());
        }
        return (uCoeff - lCoeff)/(uWav - lWav)*(wav - lWav)+lCoeff;
    }

    private static String makeResourcePath(String template) {
        return AbsorptionCoefficients.class.getResource(template).getPath();
    }

}
