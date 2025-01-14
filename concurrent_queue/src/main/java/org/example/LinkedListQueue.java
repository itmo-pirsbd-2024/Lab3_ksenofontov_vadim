package org.example;
class ListNode<T> {
    public T value;
    public ListNode<T> next;
    public ListNode<T> prev;

    ListNode(T value) {
        this.value = value;
        next = null;
        prev = null;
    }
}

public class LinkedListQueue<T> {
    private ListNode<T> head;
    private ListNode<T> tail;

    public LinkedListQueue() {
        head = null;
        tail = null;
    }

    public boolean isEmpty() {
        return (head == null);
    }

    public void add(T value) {
        ListNode<T> newNode = new ListNode<T>(value);

        if (isEmpty()) {
            head = newNode;
            tail = head;
        } else {
            var previousTail = tail;
            tail = newNode;
            newNode.next = previousTail;
            previousTail.prev = newNode;
        }
    }

    public T poll() {
        if (isEmpty()) {
            return null;
        }

        T res = head.value;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.prev;
            head.next = null;
        }

        return res;
    }

    public T peek() {
        if (isEmpty()) {
            return null;
        }

        return head.value;
    }
}
