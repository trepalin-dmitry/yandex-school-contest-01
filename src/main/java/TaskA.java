import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskA {
    public static void main(String[] args) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine = reader.readLine();

        List<OrderEvent> events = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(inputLine);;
        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            OrderEvent event = new OrderEvent();
            event.setEvent_id((Long) object.get("event_id"));
            event.setCount((Long) object.get("count"));
            event.setStatus((String) object.get("status"));
            event.setOrder_id((Long) object.get("order_id"));
            event.setReturn_count((Long) object.get("return_count"));
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
                            .entrySet().stream().map(p -> {
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(Math.toIntExact(p.getKey()));
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
        System.out.println(outArray);
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
        private int id;
        private String status;
        private List<OrderItem> items = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public void setItems(List<OrderItem> items) {
            this.items = items;
        }
    }

    public static class OrderItem {
        private int id;
        private int count;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }
    }
}