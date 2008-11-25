import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main() {
        Scanner in = new Scanner(System.in);
        PrintWriter out = new PrintWriter(System.out);
        try {
            for (;;) {
                int a = in.nextInt();
                int b = in.nextInt();
                out.println(a + b);
            }
        } catch (NoSuchElementException e) {
        }
        out.close();
    }
}
