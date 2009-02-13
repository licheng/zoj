package cn.edu.zju.acm.onlinejudge.form;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

public class ContestRefImportForm extends ActionForm implements Serializable  {
	String TAname;
	String TAphone;
	String TAemail;
	private String contestId;
	
	public String getTAname(){
		return TAname;
	}
	
	public void setTAname(String TAname) {
		this.TAname = TAname;
	}
	
	public String getTAphone(){
		return TAphone;
	}
	
	public void setTAphone(String TAphone) {
		this.TAphone = TAphone;
	}
	
	public String getTAemail(){
		return TAemail;
	}
	
	public void setTAemail(String TAemail) {
		this.TAemail = TAemail;
	}
	public String getContestId() {
        return this.contestId;
    }

    public void setcontestId(String contestId) {
        this.contestId = contestId;
    }
}
