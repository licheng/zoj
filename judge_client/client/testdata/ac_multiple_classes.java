import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        final PrintWriter out = new PrintWriter(System.out);
        new Runnable() {
            public void run() {
                try {
                    for (;;) {
                        int a = in.nextInt();
                        int b = in.nextInt();
                        out.println(a + b);
                    }
                } catch (NoSuchElementException e) {
                }
            }
        }.run();
        out.close();
    }
}
