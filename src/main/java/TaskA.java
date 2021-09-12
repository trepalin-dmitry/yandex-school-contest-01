import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            events.add(new OrderEvent(
                    Math.toIntExact((Long) object.get("event_id")),
                    Math.toIntExact((Long) object.get("order_id")),
                    Math.toIntExact((Long) object.get("item_id")),
                    Math.toIntExact((Long) object.get("count")),
                    Math.toIntExact((Long) object.get("return_count")),
                    (String) object.get("status")));
        }

        List<Order> orders = events
                .stream()
                .collect(Collectors.groupingBy(OrderEvent::getOrder_id))
                .entrySet()
                .stream()
                .map(m ->
                {
                    Order order = new Order(m.getKey());

                    List<OrderItem> items = m
                            .getValue()
                            .stream()
                            .collect(Collectors.groupingBy(OrderEvent::getItem_id))
                            .entrySet()
                            .stream()
                            .map(p -> {
                                //noinspection OptionalGetWithoutIsPresent
                                OrderEvent lastEventOrderItem = p.getValue()
                                        .stream()
                                        .max(Comparator.comparingInt(OrderEvent::getEvent_id))
                                        .get();

                                if (Objects.equals(lastEventOrderItem.getStatus(), "OK")) {
                                    return new OrderItem(p.getKey(), lastEventOrderItem.getCount() - lastEventOrderItem.getReturn_count());
                                } else {
                                    return null;
                                }
                            })
                            .filter(f -> f != null && f.getCount() > 0)
                            .collect(Collectors.toList());

                    order.getItems().addAll(items);

                    return order;
                }).filter(f -> f.getItems().size() > 0)
                .collect(Collectors.toList());

        JSONArray outArray = new JSONArray();
        for (Order order : orders) {
            JSONObject object = new JSONObject();
            //noinspection unchecked
            object.put("id", order.getId());
            JSONArray items = new JSONArray();
            for (OrderItem itemSource : order.getItems()) {
                JSONObject item = new JSONObject();
                //noinspection unchecked
                item.put("id", itemSource.getId());
                //noinspection unchecked
                item.put("count", itemSource.getCount());
                //noinspection unchecked
                items.add(item);
            }
            //noinspection unchecked
            object.put("items", items);
            //noinspection unchecked
            outArray.add(object);
        }

        System.out.println(outArray.toJSONString());
        System.out.println();
    }

    public static class OrderEvent {
        private final int event_id;
        private final int order_id;
        private final int item_id;
        private final int count;
        private final int return_count;
        private final String status;

        public OrderEvent(int event_id, int order_id, int item_id, int count, int return_count, String status) {
            this.event_id = event_id;
            this.order_id = order_id;
            this.item_id = item_id;
            this.count = count;
            this.return_count = return_count;
            this.status = status;
        }

        public int getOrder_id() {
            return this.order_id;
        }

        public int getItem_id() {
            return this.item_id;
        }

        public int getCount() {
            return this.count;
        }

        public int getReturn_count() {
            return this.return_count;
        }

        public String getStatus() {
            return this.status;
        }

        public int getEvent_id() {
            return this.event_id;
        }
    }

    public static class Order {
        private final int id;
        private final List<OrderItem> items = new ArrayList<>();

        public Order(int id) {
            this.id = id;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public int getId() {
            return id;
        }
    }

    public static class OrderItem {
        private final int id;
        private final int count;

        public OrderItem(int id, int count) {
            this.id = id;
            this.count = count;
        }

        public int getId() {
            return id;
        }

        public int getCount() {
            return this.count;
        }
    }
}