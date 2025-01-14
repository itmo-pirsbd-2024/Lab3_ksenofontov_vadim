package org.example;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        ConcurrentLinkedListQueue<String> queue = new ConcurrentLinkedListQueue<String>();

        var th = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                var cur = queue.wait_and_pop();
                System.out.println("Waited and popped: " + cur + " [" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime()) + "]");
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
        }
    }
}