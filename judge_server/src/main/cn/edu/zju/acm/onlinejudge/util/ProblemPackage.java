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

import java.util.Map;

public class ProblemPackage {

	private ProblemEntry[] problemEntries = null;	
	private Map<String, byte[]> images = null;;
	private Map<String, String> usedImages = null;;
	private Map<String, String> duplicateImages = null;;
	
	
	public ProblemPackage() {
		
	}
	
	public ProblemEntry[] getProblemEntries() {
		return problemEntries;
	}
	
	public void setProblemEntries(ProblemEntry[] problemEntries) {
		this.problemEntries = problemEntries;
	}

	public Map<String, byte[]> getImages() {
		return images;
	}
	
	public void setImages(Map<String, byte[]> images) {
		this.images = images;
	}

	public Map<String, String> getUsedImages() {
		return usedImages;
	}

	public void setUsedImages(Map<String, String> usedImages) {
		this.usedImages = usedImages;
	}

	public Map<String, String> getDuplicateImages() {
		return duplicateImages;
	}

	public void setDuplicateImages(Map<String, String> duplicateImages) {
		this.duplicateImages = duplicateImages;
	}
}
