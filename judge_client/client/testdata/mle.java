import java.util.ArrayList;

public class P {
    public static void main(String[] args) {
        ArrayList<Object> lst = new ArrayList<Object>();
        for (int i = 0; i < 100; ++i) {
            lst.add(new byte[1024 * 1024]);
        }
        System.out.println(lst.size());
    }
}
