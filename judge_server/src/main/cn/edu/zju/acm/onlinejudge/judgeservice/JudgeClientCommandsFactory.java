/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
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
package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JudgeClientCommandsFactory {
    private static int CMD_PING = 100;
    private static int CMD_JUDGE = 1;
    private static int CMD_DATA = 2;
    private static int CMD_COMPILE = 3;
    private static int CMD_TESTCASE = 4;
    private static int CMD_REMOVE_PROBLEM = 5;
    private static int CMD_INFO = 6;

    public static byte[] createPingCommand() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        try {
            out.writeInt(CMD_PING);
            out.flush();
        } catch (IOException e) {
            // Impossible
            e.printStackTrace();
        }
        return buf.toByteArray();
    }

    public static byte[] createInfoCommand() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        try {
            out.writeInt(CMD_INFO);
            out.flush();
        } catch (IOException e) {
            // Impossible
            e.printStackTrace();
        }
        return buf.toByteArray();
    }

    public static byte[] createJudgeCommand(long problemId, int problemRevision, long submissionId) {
        CommandBuilder builder = new CommandBuilder();
        builder.appendInt(CMD_JUDGE);
        builder.appendInt((int) submissionId);
        builder.appendInt((int) problemId);
        builder.appendInt(problemRevision);
        return builder.getBytes();
    }

    public static byte[] createDataCommand(int size) {
        CommandBuilder builder = new CommandBuilder();
        builder.appendInt(CMD_DATA);
        builder.appendInt(size);
        return builder.getBytes();
    }

    public static byte[] createCompileCommand(int compiler, int size) {
        CommandBuilder builder = new CommandBuilder();
        builder.appendInt(CMD_COMPILE);
        builder.appendInt(compiler);
        builder.appendInt(size);
        return builder.getBytes();
    }

    public static byte[] createTestCaseCommand(int testcase, int timeLimit, int memoryLimit, int outputLimit) {
        CommandBuilder builder = new CommandBuilder();
        builder.appendInt(CMD_TESTCASE);
        builder.appendInt(testcase);
        builder.appendInt(timeLimit);
        builder.appendInt(memoryLimit);
        builder.appendInt(outputLimit);
        return builder.getBytes();
    }

    private static class CommandBuilder {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        int checksum = 0;

        public byte[] getBytes() {
            try {
                this.appendInt(this.checksum);
                this.out.flush();
            } catch (IOException e) {
                // Impossible
                e.printStackTrace();
            }
            return buf.toByteArray();
        }

        public int getCheckSum(int value) {
            return (value & 0xff) + ((value >> 8) & 0xff) + ((value >> 16) & 0xff) + ((value >> 24) & 0xff);
        }

        public void appendInt(int value) {
            try {
                this.checksum += this.getCheckSum(value);
                out.writeInt(value);
            } catch (IOException e) {
                // Impossible
                e.printStackTrace();
            }
        }
    }
}
