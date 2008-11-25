public class P {
    public static void main(String[] args) throws Exception {
        synchronized(args) {
            args.wait();
        }
    }
}
