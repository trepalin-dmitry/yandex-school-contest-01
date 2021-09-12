import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class TaskB {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(reader.readLine());
        var matrix = new BitSet[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = Arrays.stream(reader.readLine().split(" "))
                    .map(Integer::parseInt)
                    .map(m -> BitSet.valueOf(new long[]{Long.valueOf(m)}))
                    .toArray(BitSet[]::new);
        }

        var result = new BitSet[n];
        for (int i = 0; i < n; i++) {
            result[i] = new BitSet();
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                var bitSet = matrix[i][j];
                for (int index = 0; index < bitSet.length(); index++) {
                    if (bitSet.get(index)) {
                        result[i].set(index);
                        result[j].set(index);
                    }
                }
            }
        }

        System.out.println(Arrays.stream(result).map(m -> {
                    var array = m.toLongArray();
                    if (array.length > 0) {
                        return Math.toIntExact(array[0]);
                    }
                    return 0;
                })
                .map(m -> Integer.toString(m))
                .collect(Collectors.joining(" ")));
    }
}
