import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

public class TaskD {
    private final static Map<Long, BitSet> shopsBuffer = new HashMap<>();
    private static final Map<Character, Byte> encodeMap = new HashMap<>();
    private static final Map<Byte, Character> decodeMap = new HashMap<>();
    private static final int BitSetLength = 6;

    public static void main(String[] args) throws Exception {
        fillCodeMaps();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String readLine = bufferedReader.readLine();
        String[] filenames = readLine.split(" ");

        fillShopData(filenames[0]);

        System.out.println("order_id,shop_name,shop_id,cost");

        BufferedReader reader = new BufferedReader(new FileReader(filenames[1]));
        boolean skipFirstLine = true;
        while (reader.ready()) {
            String line = reader.readLine();
            if (skipFirstLine) {
                skipFirstLine = false;
                continue;
            }
            String[] items = line.split(",");
            String shopName = getShopName(Long.parseLong(items[1]));
            if (shopName != null) {
                System.out.println(String.join(",", items[0], shopName, items[1], items[2]));
            }
        }
    }

    public static String getShopName(long id) {
        BitSet bitSet = shopsBuffer.get(id);
        if (bitSet == null) {
            return null;
        }
        return decode(bitSet);
    }

    private static void fillShopData(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        boolean skipFirstLine = true;
        while (reader.ready()) {
            String line = reader.readLine();
            if (skipFirstLine) {
                skipFirstLine = false;
                continue;
            }
            String[] items = line.split(",");
            shopsBuffer.put(Long.parseLong(items[0]), encode(items[1]));
        }
    }

    public static void fillCodeMaps() {
        encodeMap.put('A', (byte)0);
        encodeMap.put('B', (byte)1);
        encodeMap.put('C', (byte)3);
        encodeMap.put('D', (byte)4);
        encodeMap.put('E', (byte)5);
        encodeMap.put('F', (byte)6);
        encodeMap.put('G', (byte)7);
        encodeMap.put('H', (byte)8);
        encodeMap.put('I', (byte)9);
        encodeMap.put('J', (byte)10);
        encodeMap.put('K', (byte)11);
        encodeMap.put('L', (byte)12);
        encodeMap.put('M', (byte)13);
        encodeMap.put('N', (byte)14);
        encodeMap.put('O', (byte)15);
        encodeMap.put('P', (byte)16);
        encodeMap.put('Q', (byte)17);
        encodeMap.put('R', (byte)18);
        encodeMap.put('S', (byte)19);
        encodeMap.put('T', (byte)20);
        encodeMap.put('U', (byte)21);
        encodeMap.put('V', (byte)22);
        encodeMap.put('W', (byte)23);
        encodeMap.put('X', (byte)24);
        encodeMap.put('Y', (byte)25);
        encodeMap.put('Z', (byte)26);
        encodeMap.put('a', (byte)27);
        encodeMap.put('b', (byte)28);
        encodeMap.put('c', (byte)29);
        encodeMap.put('d', (byte)30);
        encodeMap.put('e', (byte)31);
        encodeMap.put('f', (byte)32);
        encodeMap.put('g', (byte)33);
        encodeMap.put('h', (byte)34);
        encodeMap.put('i', (byte)35);
        encodeMap.put('j', (byte)36);
        encodeMap.put('k', (byte)37);
        encodeMap.put('l', (byte)38);
        encodeMap.put('m', (byte)39);
        encodeMap.put('n', (byte)40);
        encodeMap.put('o', (byte)41);
        encodeMap.put('p', (byte)42);
        encodeMap.put('q', (byte)43);
        encodeMap.put('r', (byte)44);
        encodeMap.put('s', (byte)45);
        encodeMap.put('t', (byte)46);
        encodeMap.put('u', (byte)47);
        encodeMap.put('v', (byte)48);
        encodeMap.put('w', (byte)49);
        encodeMap.put('x', (byte)50);
        encodeMap.put('y', (byte)51);
        encodeMap.put('z', (byte)52);
        encodeMap.put('0', (byte)53);
        encodeMap.put('1', (byte)54);
        encodeMap.put('2', (byte)55);
        encodeMap.put('3', (byte)56);
        encodeMap.put('4', (byte)57);
        encodeMap.put('5', (byte)58);
        encodeMap.put('6', (byte)59);
        encodeMap.put('7', (byte)60);
        encodeMap.put('8', (byte)61);
        encodeMap.put('9', (byte)62);
        encodeMap.put('_', (byte)63);

        for (Map.Entry<Character, Byte> entry : encodeMap.entrySet()) {
            decodeMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static BitSet encode(String string) {
        var charArray = string.toCharArray();
        BitSet result = new BitSet(charArray.length * BitSetLength);
        int index = 0;
        for (char c : charArray) {
            BitSet bitSet = BitSet.valueOf(new byte[]{encodeMap.get(c)});
            for (int j = 0; j < BitSetLength; j++) {
                result.set(index++, bitSet.get(j));
            }
        }
        return result;
    }

    public static String decode(BitSet bitSet) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bitSet.length(); i += BitSetLength) {
            BitSet currentBitSet = new BitSet(BitSetLength);
            for (int j = 0; j < BitSetLength; j++) {
                currentBitSet.set(j, bitSet.get(i + j));
            }
            stringBuilder.append(decodeMap.get(currentBitSet.toByteArray()[0]));
        }
        return stringBuilder.toString();
    }
}
