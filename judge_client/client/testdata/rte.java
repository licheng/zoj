import java.io.FileOutputStream;

public class P {
    public static void main(String[] args) throws Exception {
        new FileOutputStream("/tmp/1").write(0);
    }
}
