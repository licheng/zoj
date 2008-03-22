package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.io.IOException;
import java.io.InputStream;

public class StreamReader {
    private InputStream in;
    private byte buf[] = new byte[4];

    public StreamReader(InputStream in) {
        this.in = in;
    }

    public int readInt() throws IOException {
        return 0;
    }

    public int readByte() {
        return 0;
    }
}
