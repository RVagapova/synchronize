import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        final int PATHS = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(PATHS);

        List<Future<String>> futuresRoads = new ArrayList<>();
        for (int i = 0; i < PATHS; i++) {
            futuresRoads.add(executor.submit(new RoadGeneratorCallable("RLRFR", 100)));
        }

        futuresRoads.forEach(futureRoad -> {
            try {
                String road = futureRoad.get();
                int countOfR = 0;
                char[] roadInChar;
                roadInChar = road.toCharArray();
                for (int i = 0; i < 100; i++) {
                    if (roadInChar[i] == 'R') {
                        countOfR++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countOfR)) {
                        sizeToFreq.put(countOfR, sizeToFreq.get(countOfR) + 1);
                    } else {
                        sizeToFreq.put(countOfR, 1);
                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        executor.shutdown();

        System.out.println(sizeToFreq);
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    static class RoadGeneratorCallable implements Callable<String> {
        private final String letters;

        private final int length;

        public RoadGeneratorCallable(String letters, int length) {
            this.letters = letters;
            this.length = length;
        }

        @Override
        public String call() throws Exception {
            return generateRoute(letters, length);
        }
    }

}