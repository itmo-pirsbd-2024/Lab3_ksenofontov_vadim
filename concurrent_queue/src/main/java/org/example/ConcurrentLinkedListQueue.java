package org.example;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLinkedListQueue<T> {

    private ReentrantLock lock;
    private Condition condition;
    private LinkedListQueue<T> queue;

    public ConcurrentLinkedListQueue() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
        queue = new LinkedListQueue<T>();
    }

    boolean empty() {
        boolean res = true;

        lock.lock();

        try {
            res = queue.isEmpty();
        } finally {
            lock.unlock();
        }

        return res;
    }

    public void push(T value) {
        lock.lock();

        try {
            queue.add(value);
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    T try_pop() {
        T res;

        lock.lock();

        try {
            res = queue.poll();
        } finally {
            lock.unlock();
        }

        return res;
    }

    T try_top() {
        T res;

        lock.lock();

        try {
            res = queue.peek();
        } finally {
            lock.unlock();
        }

        return res;
    }

    public T wait_and_pop() {
        T res;

        lock.lock();

        try {
            while (queue.isEmpty()) {
                condition.await();
            }
            res = queue.poll();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return res;
    }
}

