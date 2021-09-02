import java.util.Arrays;
import java.util.BitSet;

public class Other01 {
    public static void main(String[] args) {
//        Byte n = (byte)63;
//        System.out.println(Integer.toBinaryString(n));
//        BitSet bs = BitSet.valueOf(new byte[]{n});
//        System.out.println(bs);
//        System.out.println(bs.length());
//
//        long l = bs.toLongArray()[0];
//        System.out.println(l);

        TaskD.fillCodeMaps();

        String line = "shop_name_1";
        BitSet encoded = TaskD.encode(line);
        System.out.println("encoded = " + encoded);
        System.out.println(TaskD.decode(encoded));
    }
}
