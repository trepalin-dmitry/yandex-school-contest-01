import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskC {
    public static void main(String[] args) throws Exception {
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

    private static RandomAccessFile shopFileReader;
    private final static Map<Long, RowItemBuffered> shopsBuffer = new HashMap<>();
    private final static Map<Long, String> shops = new HashMap<>();
    private static int shopsSize = 0;
    private final static int shopsMaxSize = Integer.MAX_VALUE;

    private static class RowItemBuffered {
        private final int offset;
        private final int length;

        private RowItemBuffered(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        @Override
        public String toString() {
            return "RowItemBuffered{" +
                    "offset=" + offset +
                    ", length=" + length +
                    '}';
        }
    }

    public static String getShopName(long id) throws IOException {
        String name  = shops.get(id);
        if (name == null && !shops.containsKey(id)) {

            var rowItemBuffered = shopsBuffer.get(id);
            if (rowItemBuffered == null) {
                return null;
            }
            shopFileReader.seek(rowItemBuffered.getOffset());
            byte[] bytes = new byte[rowItemBuffered.getLength()];
            shopFileReader.read(bytes);
            name = new String(bytes, StandardCharsets.UTF_8);

            if (shopsSize > shopsMaxSize){
                shops.clear();
                shopsSize = 0;
            }

            addShop(id, name);
        }
        return name;
    }

    private static void addShop(Long id, String name){
        shops.put(id, name);
        shopsSize++;
    }

    public final static char EOL1 = '\n';
    public final static char EOL2 = '\r';
    public final static char SEPARATOR = ',';

    private static void fillShopData(String filename) throws Exception {
        shopFileReader = new RandomAccessFile(filename, "r");
        int offset = 0;
        char currentChar = 0;
        int readResult;
        boolean isEOF = false;
        StringBuilder currentIdStringBuilder = new StringBuilder();
        List<RowItem> rowItems = new ArrayList<>();
        int currentOffset = 0;
        boolean isFirstRow = true;
        boolean isEOL;
        while (!isEOF) {
            readResult = shopFileReader.read();
            offset++;

            if (readResult == -1) {
                isEOF = true;
            } else {
                currentChar = (char) readResult;
            }

            isEOL = currentChar == EOL1 || currentChar == EOL2;

            if (isFirstRow) {
                if (isEOL) {
                    isFirstRow = false;
                }
                continue;
            }

            if (currentChar == SEPARATOR || isEOL || isEOF) {
                if (currentIdStringBuilder.length() > 0) {
                    rowItems.add(new RowItem(currentOffset, currentIdStringBuilder.toString()));
                    currentIdStringBuilder.setLength(0);
                }
                currentOffset = offset;
            } else {
                currentIdStringBuilder.append(currentChar);
            }

            if (isEOL || isEOF) {
                if (rowItems.size() > 0) {
                    if (rowItems.size() != 2) {
                        throw new Exception("rowItems.size() = " + rowItems.size());
                    }

                    Long id = Long.parseLong(rowItems.get(0).getValue());
                    shopsBuffer.put(id, rowItems.get(1));

                    if (shopsSize <= shopsMaxSize){
                        addShop(id, rowItems.get(1).value);
                    }

                    rowItems.clear();
                }
            }
        }
    }


    public static class RowItem extends RowItemBuffered {
        private final String value;

        private RowItem(int offset, String value) {
            super(offset, value.length());
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
