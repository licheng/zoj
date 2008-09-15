/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigManager {

    private static Properties config = null;

    synchronized public static String getValue(String key) {
        if (ConfigManager.config == null) {
            ConfigManager.config = new Properties();
            FileInputStream fin = null;
            try {
                String path = ConfigManager.class.getClassLoader().getResource("oj.conf").getFile();
                fin = new FileInputStream(path);
                ConfigManager.config.load(fin);
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
        return ConfigManager.config.getProperty(key);

    }

    synchronized public static String[] getValues(String key) {
        String value = ConfigManager.getValue(key);
        if (value == null) {
            return null;
        }
        return value.split(",");
    }

    public static String getImagePath() {
        return ConfigManager.getValue("image_path");
    }

    public static long getDefaultProblemSetId() {
        String id = ConfigManager.getValue("default_problem_set_id");
        if (id == null) {
            return 1;
        }
        return Long.parseLong(id);
    }

    public static EmailTemplate getEmailTemplate(String name) throws ConfigurationException, IOException {
        if (name == null) {
            throw new IllegalArgumentException("name should not be null");
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("name should not be empty");
        }

        String title = ConfigManager.getValue(name + "_title");
        if (title == null) {
            throw new ConfigurationException(name + "_title is missing");
        }

        String replyTo = ConfigManager.getValue(name + "_replyTo");

        String templateFile = ConfigManager.getValue(name + "_templateFile");
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
