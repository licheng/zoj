package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;

public class MockClient {
    public static final String[] NO_SUCH_PROBLEM = new String[] { "" + "" };

    public static final String[] SAVE_INTERNAL_ERROR = new String[] { "" + JudgeReply.JUDGE_INTERNAL_ERROR.getId() };

    public static final String[] SAVE_SERVER_ERROR = new String[] { "" + "",
            "" + "" };

    public static final String[] SAVE_SUCCESS = new String[] { "" + "",
            "" + "" };

    public static final String[] JUDGE_INTERNAL_ERROR = new String[] { "" + "",
            "" + JudgeReply.JUDGE_INTERNAL_ERROR.getId() };

    public static final String[] COMPILATION_SERVER_ERROR = SAVE_SERVER_ERROR;

    public static final String[] COMPILATION_ERROR = new String[] { "" + "",
            "" + JudgeReply.COMPILATION_ERROR.getId(), "compilation error" };

    public static final String[] RUNNING_SERVER_ERROR = new String[] { "" + "",
            "" + JudgeReply.RUNNING.getId(), "0 0", "-1 -1", "" + "" };

    public static final String[] RUNTIME_ERROR = new String[] { "" + "",
            "" + JudgeReply.RUNNING.getId(), "1.1 2", "-1 -1", "" + JudgeReply.RUNTIME_ERROR.getId() };

    public static final String[] JUDGE_SERVER_ERROR = new String[] { "" + "",
            "" + JudgeReply.RUNNING, "1.1 2", "-1 -1", "" + JudgeReply.JUDGING.getId(), "" + "" };

    public static final String[] ACCEPTED = new String[] { "" + "",
            "" + JudgeReply.RUNNING.getId(), "1.1 2", "-1 -1", "" + JudgeReply.JUDGING.getId(),
            "" + JudgeReply.ACCEPTED.getId() };

    private ServerSocket serverSocket;

    private List<String[]> replies = Collections.synchronizedList(new LinkedList<String[]>());

    private byte[] file;

    private String[] command;

    private Thread thread;

    private Set<Socket> sockets = new HashSet<Socket>();

    public MockClient() throws Exception {
        this(false);
    }

    public MockClient(final boolean concurrent) throws Exception {
        serverSocket = new ServerSocket(0);
        thread = new Thread() {
            public synchronized void run() {
                while (!isInterrupted()) {
                    final Socket socket;
                    try {
                        socket = serverSocket.accept();
                    } catch (IOException e) {
                        break;
                    }
                    if (concurrent) {
                        new Thread() {
                            public void run() {
                                process(socket);
                            }
                        }.start();
                    } else {
                        process(socket);
                    }
                }
            }

            private void process(Socket socket) {
                synchronized (sockets) {
                    sockets.add(socket);
                }
                try {
                    InputStream in = socket.getInputStream();
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    String s = "";
                    for (int i = 0; i < 64; i++) {
                        int next = in.read();
                        s += (char) next;
                    }
                    command = s.trim().split(" ");
                    String[] reply = null;
                    if (replies != null && replies.size() > 0) {
                        reply = replies.remove(0);
                    }
                    if (reply == null) {
                        reply = new String[0];
                    }
                    if (reply.length > 0) {
                        out.println(reply[0]);
                        out.flush();
                    }
                    ArrayList<Byte> bytes = new ArrayList<Byte>();
                    for (;;) {
                        int next = in.read();
                        if (next < 0) {
                            break;
                        }
                        bytes.add((byte) next);
                    }
                    file = new byte[bytes.size()];
                    for (int i = 0; i < bytes.size(); i++) {
                        file[i] = bytes.get(i);
                    }
                    for (int i = 1; i < reply.length; i++) {
                        out.println(reply[i]);
                    }
                    out.flush();
                } catch (Throwable e) {
                } finally {
                    synchronized (sockets) {
                        sockets.remove(socket);
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
        };
        thread.start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void stop() throws IOException {
        thread.interrupt();
        serverSocket.close();
        synchronized (sockets) {
            for (Socket socket : sockets) {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    public void addReply(String[] replies) {
        this.replies.add(replies);
    }

    public List<String[]> getReplies() {
        return this.replies;
    }

    public byte[] getFile() {
        return file;
    }

    public String[] getCommand() {
        return command;
    }
}