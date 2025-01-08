package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    static class ThreadSafeQueue<T> {
        private ReentrantLock lock;
        private Condition condition;
        private ArrayDeque<T> queue;

        ThreadSafeQueue() {
            lock = new ReentrantLock();
            condition = lock.newCondition();
            queue = new ArrayDeque<T>();
        }

        boolean empty() {
            lock.lock();

            boolean res = queue.isEmpty();

            lock.unlock();

            return res;
        }

        void push(T value) {
            lock.lock();

            queue.add(value);
            condition.signal();

            lock.unlock();
        }

        T try_pop() {
            lock.lock();

            var res = queue.poll();

            lock.unlock();

            return res;
        }

        T try_top() {
            lock.lock();

            var res = queue.peek();

            lock.unlock();

            return res;
        }

        T wait_and_pop() {
            lock.lock();

            try {
                while (queue.isEmpty()) {
                    condition.await();
                }
                var res = queue.poll();

                lock.unlock();

                return res;
            } catch (InterruptedException e) {
                lock.unlock();
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        // basic queue operations
        /*ThreadSafeQueue<String> queue = new ThreadSafeQueue<String>();

        var th = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                var cur = queue.wait_and_pop();
                System.out.println("Waited and popped: " + cur);
            }
        });
        th.start();

        queue.push("gjnivjgf");

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        queue.push("njtrbvinjklv");


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        queue.push("kmonjihbunjk");


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        try {
            th.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/


        // linear impact of increase number of threads for generation of integers
        /*
        long sum1 = 0;

        Random random = new Random();

        long NUMBER_OF_VALUES = 2_00_000_000L;
        long NUMBER_OF_THREADS = 3;

        var start = System.currentTimeMillis();
        var end = System.currentTimeMillis();
        var timeDelta = (end - start);


        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                long sum = 0;

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    sum += random_l.nextInt(1000);
                }

                System.out.println(sum);
            }));
        }

        start = System.currentTimeMillis();
        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }




        long sum = 0;

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            sum += random.nextInt(1000);
            //sum += j;
        }

        System.out.println(sum);





        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        end = System.currentTimeMillis();
        timeDelta = (end - start);

        System.out.println("multiple threads: " + timeDelta + " ms");*/


        // impact of increase number of threads for filling thread-local queues
        /*long sum1 = 0;

        Random random = new Random();

        long NUMBER_OF_VALUES = 20_000_000L;
        long NUMBER_OF_THREADS = 2;

        var start = System.currentTimeMillis();
        var end = System.currentTimeMillis();
        var timeDelta = (end - start);


        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();
                ArrayDeque<Integer> queue = new ArrayDeque<>();

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    queue.add(random_l.nextInt(1000));
                }

                System.out.println(queue.getLast());
            }));
        }

        start = System.currentTimeMillis();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }




        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.add(random.nextInt(1000));
        }

        System.out.println(queue.getLast());





        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        end = System.currentTimeMillis();
        timeDelta = (end - start);

        System.out.println("multiple threads: " + timeDelta + " ms");


        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        System.out.println(heapSize + " " + heapMaxSize + " " + heapFreeSize);*/


        // impact of increase number of threads for filling thread-local queues
        /*long sum1 = 0;

        Random random = new Random();

        long NUMBER_OF_VALUES = 20_000_000L;
        long NUMBER_OF_THREADS = 2;

        var start = System.currentTimeMillis();
        var end = System.currentTimeMillis();
        var timeDelta = (end - start);

        ThreadLocal<ArrayDeque<Integer>> queue = new ThreadLocal<>();


        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                queue.set(new ArrayDeque<>());

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    queue.get().add(random_l.nextInt(1000));
                }

                System.out.println(queue.get().getLast());
            }));
        }

        start = System.currentTimeMillis();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }



        queue.set(new ArrayDeque<>());

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.get().add(random.nextInt(1000));
        }

        System.out.println(queue.get().getLast());





        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        end = System.currentTimeMillis();
        timeDelta = (end - start);

        System.out.println("multiple threads: " + timeDelta + " ms");


        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        System.out.println(heapSize + " " + heapMaxSize + " " + heapFreeSize);*/


        // using of builtin ConcurrentQueue
        /*long sum1 = 0;

        Random random = new Random();

        long NUMBER_OF_VALUES = 20_000_000L;
        long NUMBER_OF_THREADS = 4;

        var start = System.currentTimeMillis();
        var end = System.currentTimeMillis();
        var timeDelta = (end - start);

        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();


        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    queue.add(random_l.nextInt(1000));
                }

                System.out.println(queue.peek());
            }));
        }

        start = System.currentTimeMillis();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }



        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.add(random.nextInt(1000));
        }

        System.out.println(queue.peek());





        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        end = System.currentTimeMillis();
        timeDelta = (end - start);

        System.out.println("multiple threads: " + timeDelta + " ms");


        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        System.out.println(heapSize + " " + heapMaxSize + " " + heapFreeSize);*/


        // using of implemented ThreadSafeQueue
        /*long sum1 = 0;

        Random random = new Random();

        long NUMBER_OF_VALUES = 100_000_000L;
        long NUMBER_OF_THREADS = 1;

        var start = System.currentTimeMillis();
        var end = System.currentTimeMillis();
        var timeDelta = (end - start);

        ThreadSafeQueue<Integer> queue = new ThreadSafeQueue<>();


        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    queue.push(random_l.nextInt(1000));
                }

                System.out.println(queue.wait_and_pop());
            }));
        }

        start = System.currentTimeMillis();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }



        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.push(random.nextInt(1000));
        }

        System.out.println(queue.wait_and_pop());





        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        end = System.currentTimeMillis();
        timeDelta = (end - start);

        System.out.println("multiple threads: " + timeDelta + " ms");


        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        System.out.println(heapSize + " " + heapMaxSize + " " + heapFreeSize);*/
    }
}