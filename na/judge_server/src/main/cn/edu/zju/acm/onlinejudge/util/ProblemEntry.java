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
        return this.checkerSource;
    }

    public void setCheckerSource(byte[] checkerSource) {
        this.checkerSource = checkerSource;
    }

    public byte[] getSolution() {
        return this.solution;
    }

    public void setSolution(byte[] solution) {
        this.solution = solution;
    }

    public ProblemEntry() {

    }

    public Problem getProblem() {
        return this.problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public byte[] getChecker() {
        return this.checker;
    }

    public void setChecker(byte[] checker) {
        this.checker = checker;
    }

    public byte[] getInput() {
        return this.input;
    }

    public void setInput(byte[] input) {
        this.input = input;
    }

    public byte[] getOutput() {
        return this.output;
    }

    public void setOutput(byte[] output) {
        this.output = output;
    }

    public byte[] getText() {
        return this.text;
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    public String getCheckerSourceType() {
        return this.checkerSourceType;
    }

    public void setCheckerSourceType(String checkerSourceType) {
        this.checkerSourceType = checkerSourceType;
    }

    public String getSolutionType() {
        return this.solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }
}
