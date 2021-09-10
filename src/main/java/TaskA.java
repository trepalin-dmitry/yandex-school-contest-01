import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class TaskA {
    public static void main(String[] args) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<OrderEvent> events = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(reader);
        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            OrderEvent event = new OrderEvent();
            event.setEvent_id((Long) object.get("event_id"));
            event.setCount((Long) object.get("count"));
            event.setStatus((String) object.get("status"));
            event.setOrder_id((Long) object.get("order_id"));
            event.setReturn_count((Long) object.get("return_count"));
            event.setItem_id((Long) object.get("item_id"));
            events.add(event);
        }

        List<Order> orders = events.stream().collect(Collectors.groupingBy(OrderEvent::getOrder_id)).entrySet().stream()
                .map(m ->
                {
                    Order order = new Order();
                    order.setId(Math.toIntExact(m.getKey()));
                    //noinspection OptionalGetWithoutIsPresent
                    String status = m.getValue()
                            .stream()
                            .max(Comparator.comparingLong(OrderEvent::getEvent_id))
                            .get()
                            .getStatus();
                    order.setStatus(status);

                    List<OrderItem> items = m.getValue()
                            .stream()
                            .collect(Collectors.groupingBy(OrderEvent::getItem_id))
                            .entrySet()
                            .stream()
                            .map(p -> {
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(p.getKey());
                                //noinspection OptionalGetWithoutIsPresent
                                OrderEvent orderEvent = p.getValue().stream().max(Comparator.comparingLong(OrderEvent::getEvent_id)).get();
                                orderItem.setCount((int) (orderEvent.getCount() - orderEvent.getReturn_count()));
                                return orderItem;
                            })
                            .filter(f -> f.count > 0)
                            .collect(Collectors.toList());

                    order.getItems().addAll(items);

                    return order;
                }).filter(f -> !Objects.equals(f.status, "CANCEL") && f.getItems().size() > 0)
                .collect(Collectors.toList());

        JSONArray outArray = new JSONArray();
        for (Order order : orders) {
            JSONObject object = new JSONObject();
            object.put("id", order.getId());
            JSONArray items = new JSONArray();
            for (OrderItem itemSource : order.getItems()) {
                JSONObject item = new JSONObject();
                item.put("id", itemSource.getId());
                item.put("count", itemSource.getCount());
                items.add(item);
            }
            object.put("items", items);
            outArray.add(object);
        }

        writeJSONString(outArray, System.out, 0);
        System.out.println();
    }

    public static class OrderEvent {
        private long event_id;
        private long order_id;
        private long item_id;
        private long count;
        private long return_count;
        private String status;

        public long getEvent_id() {
            return event_id;
        }

        public void setEvent_id(long event_id) {
            this.event_id = event_id;
        }

        public long getOrder_id() {
            return order_id;
        }

        public void setOrder_id(long order_id) {
            this.order_id = order_id;
        }

        public long getItem_id() {
            return item_id;
        }

        public void setItem_id(long item_id) {
            this.item_id = item_id;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public long getReturn_count() {
            return return_count;
        }

        public void setReturn_count(long return_count) {
            this.return_count = return_count;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Order {
        private long id;
        private String status;
        private List<OrderItem> items = new ArrayList<>();

        public void setStatus(String status) {
            this.status = status;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class OrderItem {
        private Long id;
        private int count;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }
    }

    public static void writeJSONString(List list, PrintStream printStream, int level) throws IOException {
        if (list == null) {
            printStream.print("null");
        } else {
            boolean first = true;
            Iterator iter = list.iterator();

            printStream.print('[');
            level++;

            while (iter.hasNext()) {
                if (first) {
                    first = false;
                } else {
                    printStream.print(',');
                }

                Object value = iter.next();
                if (value == null) {
                    printStream.print("null");
                } else {
                    printStream.println();
                    writeLevel(level, printStream);
                    writeJSONString(value, printStream, level);
                }
            }

            if (list.size() > 0) {
                printStream.println();
            }
            level--;
            writeLevel(level, printStream);
            printStream.print(']');
        }
    }

    private static void writeLevel(int level, PrintStream printStream) {
        for (int i = 0; i < level; i++) {
            printStream.print("    ");
        }
    }

    public static void writeJSONString(Object value, PrintStream printStream, int level) throws IOException {
        if (value == null) {
            printStream.print("null");
        } else if (value instanceof String) {
            printStream.print("\"" + (String) value + "\"");
        } else if (value instanceof Double) {
            printStream.print(!((Double) value).isInfinite() && !((Double) value).isNaN() ? value.toString() : "null");
        } else if (value instanceof Float) {
            printStream.print(!((Float) value).isInfinite() && !((Float) value).isNaN() ? value.toString() : "null");
        } else if (value instanceof Number) {
            printStream.print(value.toString());
        } else if (value instanceof Boolean) {
            printStream.print(value.toString());
        } else if (value instanceof Map) {
            writeJSONString((Map) value, printStream, level);
        } else if (value instanceof JSONAware) {
            printStream.print(((JSONAware) value).toJSONString());
        } else {
            if (value instanceof List)
            {
                writeJSONString((List) value, printStream, level);
            }
            else {
                printStream.print(value);
            }
        }
    }

    public static void writeJSONString(Map map, PrintStream printStream, int level) throws IOException {
        if (map == null) {
            printStream.print("null");
        } else {
            boolean first = true;
            Iterator iter = map.entrySet().iterator();
            printStream.print('{');
            level++;

            while (iter.hasNext()) {
                if (first) {
                    first = false;
                } else {
                    printStream.print(',');
                }

                Map.Entry entry = (Map.Entry) iter.next();
                printStream.println();
                writeLevel(level, printStream);
                writeJSONString(String.valueOf(entry.getKey()), entry.getValue(), printStream, level);
            }

            printStream.println();
            level--;
            writeLevel(level, printStream);
            printStream.print('}');
        }
    }

    private static void writeJSONString(String key, Object value, PrintStream printStream, int level) throws IOException {
        printStream.print('"');
        if (key == null) {
            printStream.print("null");
        } else {
            printStream.print(key);
        }

        printStream.print('"');
        printStream.print(':');
        printStream.print(' ');

        if (value instanceof JSONArray) {
            writeJSONString((JSONArray) value, printStream, level);
        } else {
            writeJSONString(value, printStream, level);
        }

    }
}