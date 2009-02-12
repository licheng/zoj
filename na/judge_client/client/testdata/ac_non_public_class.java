import java.util.NoSuchElementException;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            for (;;) {
                int a = in.nextInt();
                int b = in.nextInt();
                System.out.println(a + b);
            }
        } catch (NoSuchElementException e) {
        }
    }
}
