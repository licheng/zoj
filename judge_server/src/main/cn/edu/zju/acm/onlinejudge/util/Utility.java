package cn.edu.zju.acm.onlinejudge.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Utility {
    
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat formatUrl = new SimpleDateFormat(
    	"'http://www.timeanddate.com/worldclock/fixedtime.html?" +
    	"month='M'&day='d'&year='yyyy'&hour='HH'&min='mm'&sec='ss'&p1=33'");
    
    private Utility() {
      
    }    
    public static boolean validateTimestamp(String timestamp) {
        try {
          return format.parse(timestamp) != null;
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
        
}
