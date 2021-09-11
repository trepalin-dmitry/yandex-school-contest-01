import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TaskF {

    private static int size = 0;
    private final static Map<String, Long> map = new TreeMap<>();
    private final static Map<Long, String> reverseMap = new TreeMap<>();

    public static void main(String[] args) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        var lineItems = reader.readLine().split(" ");
        int n = Integer.parseInt(lineItems[0]);
        int m = Integer.parseInt(lineItems[1]);
        var operations = new ArrayList<Operation>();
        for (int i = 0; i < n; i++) {
            lineItems = reader.readLine().split(" ");
            String id = lineItems[0];
            Long time = Long.parseLong(lineItems[1]);

            var currentTime = map.get(id);
            if (currentTime != null) {
                if (currentTime < time) {
                    map.put(id, time);
                    reverseMap.remove(currentTime);
                    reverseMap.put(time, id);
                    operations.add(new Operation(i, OperationType.UPDATE, id));
                }
                continue;
            }

            map.put(id, time);
            reverseMap.put(time, id);
            size++;

            if (size > m) {
                var removeTime = reverseMap.keySet().stream().findFirst().orElseThrow();
                var removeId = reverseMap.remove(removeTime);
                map.remove(removeId);
                size--;

                if (Objects.equals(removeId, id)) {
                    continue;
                }

                operations.add(new Operation(i, OperationType.DELETE, removeId));
            }

            operations.add(new Operation(i, OperationType.PUT, id));
        }

        for (Operation operation : operations) {
            System.out.println(operation);
        }
    }

    public enum OperationType {
        PUT,
        UPDATE,
        DELETE
    }

    public static class Operation {
        private final int index;
        private final OperationType type;
        private final String id;

        public Operation(int index, OperationType type, String id) {
            this.index = index + 1;
            this.type = type;
            this.id = id;
        }

        @Override
        public String toString() {
            return index + " " + type + " " + id;
        }
    }
}
