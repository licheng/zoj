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

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;


public class Utility {
    
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat formatUrl = new SimpleDateFormat(
    	"'http://www.timeanddate.com/worldclock/fixedtime.html?" +
    	"month='M'&day='d'&year='yyyy'&hour='HH'&min='mm'&sec='ss'&p1=33'");
    
    private static Logger logger = Logger.getLogger(Utility.class);
    
    private Utility() {
      
    }    
    public static boolean validateTimestamp(String timestamp) {
        try {
        	Date date = format.parse(timestamp);
        	
        	Calendar c = Calendar.getInstance();
        	c.setTime(date);
        	int year = c.get(Calendar.YEAR);
        	if (year < 1901 || year > 3000) {
        		return false;
        	}
        	return true;
        } catch (ParseException e) {
        	return false;
        }
    }
    
    public static Date parseTimestamp(String timestamp) throws ParseException {
        return format.parse(timestamp);
    }
    
    public static String toTimestamp(Date timestamp) {
        return format.format(timestamp);
    }
    
    public static String toTimeUrl(Date timestamp) {
        return formatUrl.format(timestamp);
    }
    public static String toTimestampWithTimeZone(Date timestamp) {
        return format.format(timestamp) + " (GMT+8)";
    }
    
    
    public static boolean validateTime(String time) {
        String[] ss = time.split(":");
        if (ss.length != 3) {
            return false;
        }
        
        long h = Long.parseLong(ss[0]);
        long m = Long.parseLong(ss[1]);
        long s = Long.parseLong(ss[2]);
        
        if (h < 0 || m < 0 || s < 0 || m >= 60 || s >= 60) {
            return false; 
        }
        
        return true;
        
        
    }
    
    public static long parseTime(String time) throws ParseException {
        if (!validateTime(time)) {
            throw new ParseException("invalid time: " + time, 0);
        }        
        String[] ss = time.split(":");
        long h = Long.parseLong(ss[0]);
        long m = Long.parseLong(ss[1]);
        long s = Long.parseLong(ss[2]);
        
        return h * 3600 + m * 60 + s;
    }
    
    public static String toTime(long time) {
        return time / 3600 + ":" + time % 3600 / 60 + ":" + time % 60;
    }
    
    public static String toTextTime(long time) {
    	long h = time / 3600;
    	long m = time % 3600 / 60;
    	long s = time % 60;
    	StringBuilder sb = new StringBuilder();
    	if (h > 0) {
    		sb.append(h);
    		sb.append(" Hour");
    		if (h > 1) {
    			sb.append("s");
    		}
    		sb.append(" ");
    	}
    	if (m > 0) {
    		sb.append(m);
    		sb.append(" Minute");
    		if (m > 1) {
    			sb.append("s");
    		}
    		sb.append(" ");
    	}
    	if (s > 0) {
    		sb.append(s);
    		sb.append(" Second");
    		if (s > 1) {
    			sb.append("s");
    		}
    		sb.append(" ");
    	}
    	
        return sb.toString();
    }
    
    public static int parseInt(String value) {
    	if (value == null) {
    		return -1;
    	}
    	try {
    		return Integer.parseInt(value);    		
    	} catch (Exception e) {
    		return -1;
    	}
    }
    
    public static long parseLong(String value) {
    	if (value == null) {
    		return -1;
    	}
    	try {
    		return Long.parseLong(value);    		
    	} catch (Exception e) {
    		return -1;
    	}
    }
    
    public static long parseLong(String value, long min, long max) {
        long ret = parseLong(value);
        if (ret < min) {
            ret = min;
        }
        if (ret > max) {
            ret = max;
        }
        return ret;
    }
    
    public static void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error("Fail to close socket", e);
        }
    }        
}
