import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        final int PATHS = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(PATHS);
        List<Future<Integer>> futures = new ArrayList<>();

        List<String> roads = new CopyOnWriteArrayList<>();


        for (int i = 0; i < PATHS; i++) {
            executor.submit(new RoadGenerator(roads,"RLRFR", 100));
        }
//        executor.shutdown();

        for (String road : roads) {
            Callable<Integer> logic = () -> {
                int countOfR = 0;
                char[] roadInChar;
                roadInChar = road.toCharArray();
                for (int i = 0; i < 100; i++) {
                    if (roadInChar[i] == 'R') {
                        countOfR++;
                    }
                }
                return countOfR;
            };
            futures.add(executor.submit(logic));
        }

        futures.forEach(future->{
            try {
                int count = future.get();
                synchronized (sizeToFreq) {
                    if(sizeToFreq.containsKey(count)) {
                        sizeToFreq.put(count, sizeToFreq.get(count) + 1);
                    } else {
                        sizeToFreq.put(count, 1);
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

    static class RoadGenerator implements Runnable {
        private final List<String> roads;
        private final String letters;
        private final int length;

        public RoadGenerator(List<String> roads, String letters, int length) {
            this.roads = roads;
            this.letters = letters;
            this.length = length;
        }

        @Override
        public void run() {
            roads.add(generateRoute(letters, length));
        }
    }
}