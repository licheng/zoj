/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.GregorianCalendar;
import java.util.Scanner;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;

public class Sandbox {
    
    static {
        System.loadLibrary("sandbox");
    }

    private static int timeConsumption = 0;

    private static int memoryConsumption = 0;

    private static long baseHeapMemoryConsumption = 0;

    private static MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    private static ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    private static Thread targetThread = new Thread() {

        private SandboxSecurityManager sandboxSecurityManager = new SandboxSecurityManager();

        private Object[] targetArguments = new Object[] { new String[0] };

        private Method mainMethod = initTargetMainMethod();

        public void run() {
            closeLog();
            if (setLimits(timeLimit, outputLimit, 6, uid, gid) < 0) {
                halt(JudgeReply.JUDGE_INTERNAL_ERROR);
            }
            mainMethod.setAccessible(true);
            System.setSecurityManager(sandboxSecurityManager);
            SandboxSecurityManager.targetThread = this;
            try {
                mainMethod.invoke(null, targetArguments);
                System.out.close();
                SandboxSecurityManager.targetThread = null;
                updateConsumptions();
            } catch (InvocationTargetException e) {
                SandboxSecurityManager.targetThread = null;
                Throwable targetException = e.getTargetException();
                logError(printError(targetException));
                if (targetException instanceof OutOfMemoryError) {
                    memoryConsumption = memoryLimit + 1;
                    halt(JudgeReply.MEMORY_LIMIT_EXCEEDED);
                } else {
                    halt(JudgeReply.RUNTIME_ERROR);
                }
            } catch (Exception e) {
                SandboxSecurityManager.targetThread = null;
                logError(printError(e));
                halt(JudgeReply.JUDGE_INTERNAL_ERROR);
            }
        }
    };

    private static int port;

    private static int timeLimit;

    private static int memoryLimit;

    private static int outputLimit;

    private static int uid;

    private static int gid;

    private static Socket socket;

    private static DataOutputStream out;

    public static void main(String[] args) {
        if (args.length != 6) {
            logError("Invalid args length: " + args.length);
            halt(JudgeReply.JUDGE_INTERNAL_ERROR);
        }

        GregorianCalendar gc = new GregorianCalendar();

        try {
            // The purpose of Scaner here is to preload this class so that the target class can use it directly.
            Scanner scanner = new Scanner(args[0]);
            port = scanner.nextInt();
            timeLimit = Integer.parseInt(args[1]);
            memoryLimit = Integer.parseInt(args[2]);
            outputLimit = Integer.parseInt(args[3]);
            uid = Integer.parseInt(args[4]);
            gid = Integer.parseInt(args[5]);
            
            socket = new Socket("127.0.0.1", port);
            out = new DataOutputStream(socket.getOutputStream());

            System.setIn(new BufferedInputStream(new FileInputStream("input")));
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("p.out") {
                public void write(int b) throws IOException {
                    try {
                        super.write(b);
                    } catch (IOException e) {
                        if (e.getMessage().equals("File too large")) {
                            SandboxSecurityManager.targetThread = null;
                            halt(JudgeReply.OUTPUT_LIMIT_EXCEEDED);
                        }
                        throw e;
                    }
                }

                public void write(byte[] b, int off, int len) throws IOException {
                    try {
                        super.write(b, off, len);
                    } catch (IOException e) {
                        if (e.getMessage().equals("File too large")) {
                            SandboxSecurityManager.targetThread = null;
                            halt(JudgeReply.OUTPUT_LIMIT_EXCEEDED);
                        }
                        throw e;
                    }
                }
            })));
            System.setErr(new PrintStream(new BufferedOutputStream(new FileOutputStream("/dev/null"))));
        } catch (Exception e) {
            logError(printError(e));
            halt(JudgeReply.JUDGE_INTERNAL_ERROR);
            return;
        }

        System.gc();
        baseHeapMemoryConsumption = memoryBean.getHeapMemoryUsage().getUsed();

        targetThread.start();
        for (;;) {
            Thread.State state;
            ThreadInfo info = threadBean.getThreadInfo(targetThread.getId());
            if (info == null) {
                state = Thread.State.TERMINATED;
            } else {
                state = info.getThreadState();
            }
            if (state == Thread.State.RUNNABLE || state == Thread.State.NEW || state == Thread.State.TERMINATED) {
                updateConsumptions();
                try {
                    sendRunningMessage(timeConsumption, memoryConsumption);
                } catch (IOException e) {
                    halt(JudgeReply.JUDGE_INTERNAL_ERROR);
                }
                if (state == Thread.State.TERMINATED) {
                    break;
                }
            } else if (SandboxSecurityManager.targetThread != null) {
                logError("Invalid thread state " + state);
                halt(JudgeReply.RUNTIME_ERROR);
            }
            try {
                targetThread.join(1000);
            } catch (InterruptedException e) {
                Runtime.getRuntime().halt(0);
                break;
            }
        }
        closeSocket();
    }

    private static native int setLimits(int timeLimit, int outputLimit, int fileLimit, int uid, int gid);

    private static native void closeLog();

    private static native void logError(String message);

    private synchronized static void updateConsumptions() {
        int m = (int) ((memoryBean.getHeapMemoryUsage().getUsed() - baseHeapMemoryConsumption) / 1000);
        if (m > memoryConsumption) {
            memoryConsumption = m;
        }
        if (targetThread != null) {
            long t = threadBean.getThreadCpuTime(targetThread.getId());
            if (t >= 0) {
                t /= 1000000;
                if (t > timeConsumption && t <= 1000000) {
                    timeConsumption = (int) t;
               }
            }
        }
    }

    private synchronized static void sendRunningMessage(int timeConsumption, int memoryConsumption) throws IOException {
        if (out != null) {
            out.writeInt(timeConsumption);
            out.writeInt(memoryConsumption);
        }
    }

    private static Method initTargetMainMethod() {
        SandboxClassLoader sandboxClassLoader = new SandboxClassLoader();
        Class<?> targetClass = null;
        try {
            for (File f : new File(".").listFiles()) {
                String name = f.getName();
                if (name.endsWith(".class")) {
                    Class<?> c = Class.forName(name.substring(0, name.length() - 6), false, sandboxClassLoader);
                    if (name.equals("Main.class")) {
                        targetClass = c;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            logError(printError(e));
            halt(JudgeReply.RUNTIME_ERROR);
        } catch (NoClassDefFoundError e) {
            logError(printError(e));
            halt(JudgeReply.RUNTIME_ERROR);
        } catch (ClassFormatError e) {
            logError(printError(e));
            halt(JudgeReply.JUDGE_INTERNAL_ERROR);
        } catch (ExceptionInInitializerError e) {
            logError(printError(e));
            halt(JudgeReply.RUNTIME_ERROR);
        } catch (LinkageError e) {
            logError(printError(e));
            halt(JudgeReply.RUNTIME_ERROR);
        }
        if (targetClass == null) {
            logError("No Main.class found");
            halt(JudgeReply.JUDGE_INTERNAL_ERROR);
        }
        Method mainMethod = null;
        try {
            mainMethod = targetClass.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            logError("No main found");
            halt(JudgeReply.RUNTIME_ERROR);
        }
        if (!Modifier.isStatic(mainMethod.getModifiers())) {
            logError("main is not static");
            halt(JudgeReply.RUNTIME_ERROR);
        }
        return mainMethod;
    }

    private static String printError(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private static void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
        }
    }

    private synchronized static void halt(JudgeReply result) {
        updateConsumptions();
        try {
            sendRunningMessage(timeConsumption, memoryConsumption);
        } catch (IOException e) {
        }
        closeSocket();
        Runtime.getRuntime().halt((int) result.getId());
    }
}

