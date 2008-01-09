package cn.edu.zju.acm.onlinejudge.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
   
    private static Properties config = null;
    
    synchronized public static String getValue(String key) {
        if (config == null) {
            config = new Properties();
            FileInputStream fin = null;
            try {
                String path = ConfigManager.class.getClassLoader().getResource("oj.conf").getFile();
                fin = new FileInputStream(path);
                config.load(fin);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (fin != null) {
                        fin.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();                    
                }
            }                        
        }
        if (key == null) {
            return null;
        }
        return config.getProperty(key);
        
    }
    synchronized public static String[] getValues(String key) {
        String value =getValue(key);
        if (value == null) {
            return null;
        }
        return value.split(",");        
    }
    
    public static String getImagePath() {
        return getValue("image_path");
    }
        
}
