package cn.edu.zju.acm.onlinejudge.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.judgeserver.JudgeService;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;
import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;

public class ImageManager {

	private final Cache imageCache;
	
	/**
	 * ContestManager.
	 */
	private static ImageManager instance = null; 

	
    /**
     * <p>Constructor of ContestManager class.</p>
     * @throws PersistenceCreationException 
     *
     */
    private ImageManager() throws PersistenceCreationException {
    	imageCache = new Cache(60000, 30);      	
    }
    
    /**
     * Gets the singleton instance.
     * @return the singleton instance.
     * @throws PersistenceCreationException 
     */
    public static ImageManager getInstance() throws PersistenceCreationException {
    	if (instance == null) {
    		synchronized (ImageManager.class) {
    			if (instance == null) {
    				instance = new ImageManager();
    			}
    		}
    	}
    	return instance;
    }
    
   
    
    public byte[] getImage(String name) {
    	if (name == null || name.trim().length() == 0) {
        	return null;
        }
    	
    	synchronized (imageCache) {
    		byte[] image = (byte[]) imageCache.get(name);
    		if (image == null) {
    			image = getImageFile(name);
    			imageCache.put(name, image);
    		}
    		return image;
    	}
    }
    
    private byte[] getImageFile(String name) {  
    	
    	File file = new File(ConfigManager.getImagePath(), name);
    	System.out.println("**" + name);
    	FileInputStream in = null;
        try {
	        if (!file.isFile() || !file.canRead()) {
	        	System.out.println("**fail");
	        	return null;
	        }
	        
	        in = new FileInputStream(file);
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        byte[] buffer = new byte[102400];
	        while (true) {
	        	int l = in.read(buffer);
	        	if (l == -1) {
	        		break;
	        	}
	        	out.write(buffer, 0, l);
	        }
	        return out.toByteArray();
        } catch (Exception e) {
        	
        	e.printStackTrace();        	
        } finally {
        	if (in != null) {
        		try {
        			in.close();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
        }
        
        return null;
    }
   
    
    
    
}
