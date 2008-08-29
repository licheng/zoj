package cn.edu.zju.acm.onlinejudge.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    
    public static EmailTemplate getEmailTemplate(String name) throws ConfigurationException, IOException {
    	if (name == null) {
    		throw new IllegalArgumentException("name should not be null");
    	}
    	if (name.trim().length() == 0) {
    		throw new IllegalArgumentException("name should not be empty");
    	}
    	
    	String title = getValue(name + "_title");
    	if (title == null) {
    		throw new ConfigurationException(name + "_title is missing");
    	}
    	
    	String replyTo = getValue(name + "_replyTo");
    	
    	String templateFile = getValue(name + "_templateFile");
    	if (templateFile == null) {
    		throw new ConfigurationException(name + "_templateFile is missing");
    	}
    	
    	
    	
    	StringBuilder content = new StringBuilder();
    	
    	InputStreamReader reader = null;
    	try {
    		reader = new InputStreamReader(ConfigManager.class.getClassLoader().getResourceAsStream(templateFile));
	    	char[] buffer = new char[10240];
	    	for (;;) {
	    		int l = reader.read(buffer, 0, 10240);
	    		if (l == -1) {
	    			break;
	    		}
	    		content.append(buffer, 0, l);
	    	}
    	} finally {
    		if (reader != null) {
    			try {
    				reader.close();
    			} catch (Exception e) {
    				
    			}
    		}
    	}
    	
    	return new EmailTemplate(title, replyTo, content.toString());

    }
        
}
