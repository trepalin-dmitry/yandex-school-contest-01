import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class TaskB {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(reader.readLine());
        Integer[][] m = new Integer[n][n];
        for (int i = 0; i < n; i++) {
            m[i] = parseMatrixLine(reader.readLine());
        }
    }

    public static Integer[] parseMatrixLine(String line){
        return Arrays.stream(line.split(" "))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
    }
}
