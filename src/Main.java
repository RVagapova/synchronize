import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        final int PATHS = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(PATHS);

        //текущий лидер
        new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                        int max = 0;
                        for (int key : sizeToFreq.keySet()) {
                            if (sizeToFreq.get(max) == null || sizeToFreq.get(max) < sizeToFreq.get(key)) {
                                max = key;
                            }
                        }
                        System.out.println("текущий лидер - " + max);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        for (int i = 0; i < PATHS; i++) {
            executor.submit(() -> {
                String road = generateRoute("RLRFR", 100);
                int countOfR = 0;
                char[] roadInChar;
                roadInChar = road.toCharArray();
                for (int i1 = 0; i1 < 100; i1++) {
                    if (roadInChar[i1] == 'R') {
                        countOfR++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countOfR)) {
                        sizeToFreq.put(countOfR, sizeToFreq.get(countOfR) + 1);
                    } else {
                        sizeToFreq.put(countOfR, 1);
                    }
                    sizeToFreq.notify();
                }
            });
        }

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

}