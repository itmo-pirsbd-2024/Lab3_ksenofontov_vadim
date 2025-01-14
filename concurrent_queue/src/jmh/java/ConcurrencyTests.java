package org.example.bench;

import org.example.ConcurrentLinkedListQueue;
import org.example.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.Random;

import static java.util.Arrays.sort;

@BenchmarkMode(Mode.AverageTime)

//@Fork(value = 1, jvmArgs = "-Xms6g -Xmx6g")
@Fork(value = 1)
@Warmup(iterations = 5, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
//@Timeout(time = 0)


@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ConcurrencyTests {

    @Param({"1", "2", "3", "4", "5", "6", "7", "8"})
    private long NUMBER_OF_THREADS;

    //---------------------------------------------------------------------------
    @Benchmark
    public void increaseThreadsForIntGenerationImpact(Blackhole blackhole) {
        Random random = new Random();

        long NUMBER_OF_VALUES = 1_000_000_000L;

        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                long sum = 0;

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    sum += random_l.nextInt(1000);
                }

                blackhole.consume(sum);
            }));
        }

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }


        long sum = 0;

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            sum += random.nextInt(1000);
        }

        blackhole.consume(sum);

        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //---------------------------------------------------------------------------
    @Benchmark
    public void increaseThreadsForFillingThreadlocalQueuesImpact(Blackhole blackhole) {
        Random random = new Random();

        long NUMBER_OF_VALUES = 20_000_000L;

        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();
                ArrayDeque<Integer> queue = new ArrayDeque<>();

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    queue.add(random_l.nextInt(1000));
                }

                blackhole.consume(queue.getLast());
            }));
        }

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.add(random.nextInt(1000));
        }

        blackhole.consume(queue.getLast());

        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //---------------------------------------------------------------------------
    @Benchmark
    public void increaseThreadsForFillingThreadlocalQueuesImpact2(Blackhole blackhole) {
        Random random = new Random();

        long NUMBER_OF_VALUES = 20_000_000L;

        ThreadLocal<ArrayDeque<Integer>> queue = new ThreadLocal<>();

        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                queue.set(new ArrayDeque<>());

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    queue.get().add(random_l.nextInt(1000));
                }

                blackhole.consume(queue.get().getLast());
            }));
        }

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }

        queue.set(new ArrayDeque<>());

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.get().add(random.nextInt(1000));
        }

        blackhole.consume(queue.get().getLast());

        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //---------------------------------------------------------------------------
    @Benchmark
    public void usingOfBuiltinConcurrentQueue(Blackhole blackhole) {
        Random random = new Random();

        long NUMBER_OF_VALUES = 20_000_000L;

        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    queue.add(random_l.nextInt(1000));
                }

                blackhole.consume(queue.peek());
            }));
        }

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.add(random.nextInt(1000));
        }

        blackhole.consume(queue.peek());

        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //---------------------------------------------------------------------------
    @Benchmark
    public void usingOfImplementedThreadSafeQueue(Blackhole blackhole) {

        Random random = new Random();

        long NUMBER_OF_VALUES = 20_000_000L;

        ConcurrentLinkedListQueue<Integer> queue = new ConcurrentLinkedListQueue<Integer>();

        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.add(new Thread(() -> {
                Random random_l = new Random();

                for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
                    ((ConcurrentLinkedListQueue<Integer>) queue).push(random_l.nextInt(1000));
                }

                blackhole.consume(((ConcurrentLinkedListQueue<Integer>) queue).wait_and_pop());
            }));
        }

        for (long i = 0; i < NUMBER_OF_THREADS - 1; i++) {
            threads.get((int) i).start();
        }

        for (long j = 0; j < NUMBER_OF_VALUES / NUMBER_OF_THREADS; j++) {
            queue.push(random.nextInt(1000));
        }

        blackhole.consume(((ConcurrentLinkedListQueue<Integer>) queue).wait_and_pop());

        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}