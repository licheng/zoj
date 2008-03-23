package cn.edu.zju.acm.onlinejudge.util;

import java.util.List;
import java.util.Map;

public class ProblemPackage {

	private ProblemEntry[] problemEntries = null;	
	private Map images = null;;
	private Map usedImages = null;;
	
	
	public ProblemPackage() {
		
	}
	
	public ProblemEntry[] getProblemEntries() {
		return problemEntries;
	}
	
	public void setProblemEntries(ProblemEntry[] problemEntries) {
		this.problemEntries = problemEntries;
	}

	public Map getImages() {
		return images;
	}
	
	public void setImages(Map images) {
		this.images = images;
	}

	public Map getUsedImages() {
		return usedImages;
	}

	public void setUsedImages(Map usedImages) {
		this.usedImages = usedImages;
	}
}
