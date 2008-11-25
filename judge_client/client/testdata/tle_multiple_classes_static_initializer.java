class T {
    static {
        for (int i = 0; i < 10000000; ++i) {
           for (int j = 0; j < 10000000; ++j) {
               if (i * j % 10000000 == 1) {
                   System.out.println("ok");
               }
           } 
        }
    }
}
public class Main {

    static {
        for (int i = 0; i < 10000000; ++i) {
           for (int j = 0; j < 10000000; ++j) {
               if (i * j % 10000000 == 1) {
                   System.out.println("ok");
               }
           } 
        }
    }

    public static void main(String[] args) {
        new T();
    }
}
