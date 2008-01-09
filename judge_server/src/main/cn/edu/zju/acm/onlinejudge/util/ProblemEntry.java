package cn.edu.zju.acm.onlinejudge.util;

import cn.edu.zju.acm.onlinejudge.bean.Problem;

public class ProblemEntry {

	private Problem problem = null;
	private byte[] checker = null;
	private byte[] input = null;
	private byte[] output = null;
	private byte[] text = null;
    private byte[] checkerSource = null;
    private byte[] solution = null;	
	private String checkerSourceType = null;
    private String solutionType = null;
    
	
	public byte[] getCheckerSource() {
        return checkerSource;
    }

    public void setCheckerSource(byte[] checkerSource) {
        this.checkerSource = checkerSource;
    }

    public byte[] getSolution() {
        return solution;
    }

    public void setSolution(byte[] solution) {
        this.solution = solution;
    }

    public ProblemEntry() {
		
	}
	
	public Problem getProblem() {
		return problem;
	}
	
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public byte[] getChecker() {
		return checker;
	}
	
	public void setChecker(byte[] checker) {
		this.checker = checker;
	}
	
	public byte[] getInput() {
		return input;
	}
	
	public void setInput(byte[] input) {
		this.input = input;
	}
	
	public byte[] getOutput() {
		return output;
	}
	
	public void setOutput(byte[] output) {
		this.output = output;
	}
	
	public byte[] getText() {
		return text;
	}
	
	public void setText(byte[] text) {
		this.text = text;
	}

    public String getCheckerSourceType() {
        return checkerSourceType;
    }

    public void setCheckerSourceType(String checkerSourceType) {
        this.checkerSourceType = checkerSourceType;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }
}
