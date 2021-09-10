import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TaskC {
    public static void main(String[] args) throws IOException, ParseException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String firstLine = bufferedReader.readLine();
        String[] firstLineParts = firstLine.split(" ");
        int t = Integer.parseInt(firstLineParts[0]);
        int k = Integer.parseInt(firstLineParts[1]);

        List<Date> errDateTimes = new ArrayList<>();
        String result = null;
        while (bufferedReader.ready()) {
            String string = bufferedReader.readLine();

            if (result != null){
                continue;
            }

            Line line = parseLine(string);
            if (line.isError()) {
                errDateTimes.add(line.getDate());
            }

            Date start = new Date(line.getDate().getTime() - (t - 1) * 1000L);
            for (int i = 0; i < errDateTimes.size(); i++) {
                while (true) {
                    if (errDateTimes.size() > i && errDateTimes.get(i).before(start)) {
                        errDateTimes.remove(i);
                    } else {
                        break;
                    }
                }
            }

            if (errDateTimes.size() >= k) {
                result = line.getDateTimeString();
            }
        }

        if (result == null){
            result = "-1";
        }
        System.out.println(result);
    }

    private static Line parseLine(String line) throws ParseException {
        String dateTimeString = line.substring(1, 20);
        boolean error = Objects.equals(line.split(" ")[2], "ERROR");
        return new Line(error, dateTimeString);
    }

    private static class Line {
        private static final DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private final boolean error;
        private final Date date;
        private final String dateTimeString;

        private Line(boolean error, String dateTimeString) throws ParseException {
            this.error = error;
            this.dateTimeString = dateTimeString;
            this.date = dateTimeFormatter.parse(dateTimeString);
        }

        public boolean isError() {
            return error;
        }

        public String getDateTimeString() {
            return dateTimeString;
        }

        public Date getDate() {
            return date;
        }
    }
}
