package cn.edu.zju.acm.onlinejudge.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProblemPackage {

	private ProblemEntry[] problemEntries = null;	
	private Map images = null;;
	private Map usedImages = null;;
	private Map duplicateImages = null;;
	
	
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

	public Map getDuplicateImages() {
		return duplicateImages;
	}

	public void setDuplicateImages(Map duplicateImages) {
		this.duplicateImages = duplicateImages;
	}
}
