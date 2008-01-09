package cn.edu.zju.acm.onlinejudge.util;

import java.util.Map;

public class ProblemPackage {

	private ProblemEntry[] problemEntries = null;	
	private Map images = null;;
	
	
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
}
