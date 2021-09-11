import java.io.*;

public class TaskD {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String readLine = bufferedReader.readLine();
        String[] filenames = readLine.split(" ");

        System.out.println("order_id,shop_name,shop_id,cost");

        var shopsReader = new CsvReader(filenames[0]);
        var ordersReader = new CsvReader(filenames[1]);

        long shopId = -1;
        String shopName = null;

        while (ordersReader.ready()) {
            var orderItems = ordersReader.readLine();

            var shopIdCurrent = Long.parseLong(orderItems[1]);
            while (shopIdCurrent > shopId && shopsReader.ready()) {
                var shopItems = shopsReader.readLine();
                shopId = Long.parseLong(shopItems[0]);
                shopName = shopItems[1];
            }

            if (shopIdCurrent == shopId) {
                System.out.println(String.join(",", orderItems[0], shopName, orderItems[1], orderItems[2]));
            }
        }
    }

    public static class CsvReader {
        private final BufferedReader bufferedReader;

        public CsvReader(String filename) throws IOException {
            bufferedReader = new BufferedReader(new FileReader(filename));
            bufferedReader.readLine();
        }

        public boolean ready() throws IOException {
            return bufferedReader.ready();
        }

        public String[] readLine() throws IOException {
            return bufferedReader.readLine().split(",");
        }
    }
}
