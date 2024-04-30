package br.com.alura.screenmatch.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GetApiKey {
    public static String getKey(String key) {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(".env");
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String propertie = properties.getProperty(key);

        return propertie;
    }
    
}
