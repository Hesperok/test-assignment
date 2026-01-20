package ua.edu.kpi.numberlist.impl;

import ua.edu.kpi.numberlist.NumberList;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Автор: Жалдак Сергій Денисович
 * Група: ІО-35
 * Залікова книжка: 3508
 */
public class NumberListImpl implements NumberList {

    private static class Node {
        byte value;
        Node next;

        Node(byte value) {
            this.value = value;
        }
    }

    private Node head;
    private int size;
    private final int base = 10;

    public NumberListImpl() {
        head = null;
        size = 0;
    }

    public NumberListImpl(String number) {
        this();
        for (char c : number.toCharArray()) {
            if (Character.isDigit(c)) {
                add((byte) (c - '0'));
            }
        }
    }

    public NumberListImpl(File file) throws IOException {
        this();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s = br.readLine();
            if (s != null) {
                for (char c : s.toCharArray()) {
                    if (Character.isDigit(c)) {
                        add((byte) (c - '0'));
                    }
                }
            }
        }
    }

    @Override
    public boolean add(Byte value) {
        Node n = new Node(value);
        if (head == null) {
            head = n;
            n.next = head;
        } else {
            Node cur = head;
            while (cur.next != head) {
                cur = cur.next;
            }
            cur.next = n;
            n.next = head;
        }
        size++;
        return true;
    }

    @Override
    public Byte get(int index) {
        checkIndex(index);
        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.value;
    }

    @Override
    public Byte remove(int index) {
        checkIndex(index);

        if (size == 1) {
            byte v = head.value;
            head = null;
            size = 0;
            return v;
        }

        if (index == 0) {
            Node last = head;
            while (last.next != head) {
                last = last.next;
            }
            byte v = head.value;
            head = head.next;
            last.next = head;
            size--;
            return v;
        }

        Node prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        byte v = prev.next.value;
        prev.next = prev.next.next;
        size--;
        return v;
    }

    @Override
    public int size() {
        return size;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<>() {
            private Node cur = head;
            private int passed = 0;

            @Override
            public boolean hasNext() {
                return passed < size;
            }

            @Override
            public Byte next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                byte v = cur.value;
                cur = cur.next;
                passed++;
                return v;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Byte b : this) {
            sb.append(b);
        }
        return sb.toString();
    }

    @Override
    public String toDecimalString() {
        long val = 0;
        for (Byte b : this) {
            val = val * base + b;
        }
        return Long.toString(val);
    }

    @Override
    public NumberList changeScale() {
        long value = 0;
        for (Byte b : this) {
            value = value * base + b;
        }

        NumberListImpl res = new NumberListImpl();
        if (value == 0) {
            res.add((byte) 0);
            return res;
        }

        while (value > 0) {
            res.add((byte) (value % 16));
            value /= 16;
        }
        return res;
    }

    @Override
    public NumberList additionalOperation(NumberList a, NumberList b) {
        long x = toLong(a);
        long y = toLong(b);

        long r = x - y;
        if (r < 0) r = 0;

        NumberListImpl res = new NumberListImpl();
        if (r == 0) {
            res.add((byte) 0);
            return res;
        }

        long tmp = r;
        byte[] digits = new byte[32];
        int i = 0;
        while (tmp > 0) {
            digits[i++] = (byte) (tmp % 10);
            tmp /= 10;
        }
        for (int j = i - 1; j >= 0; j--) {
            res.add(digits[j]);
        }
        return res;
    }

    private long toLong(NumberList n) {
        long v = 0;
        for (Byte b : n) {
            v = v * 10 + b;
        }
        return v;
    }

    @Override
    public void saveList(File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(toString());
        }
    }

    @Override
    public void sortAscending() {
        bubbleSort(true);
    }

    @Override
    public void sortDescending() {
        bubbleSort(false);
    }

    private void bubbleSort(boolean asc) {
        if (size < 2) return;

        for (int i = 0; i < size; i++) {
            Node cur = head;
            for (int j = 0; j < size - 1; j++) {
                Node next = cur.next;
                if ((asc && cur.value > next.value) ||
                    (!asc && cur.value < next.value)) {
                    byte t = cur.value;
                    cur.value = next.value;
                    next.value = t;
                }
                cur = cur.next;
            }
        }
    }

    @Override
    public void shiftLeft(int n) {
        if (size == 0) return;
        n %= size;
        for (int i = 0; i < n; i++) {
            head = head.next;
        }
    }

    @Override
    public void shiftRight(int n) {
        if (size == 0) return;
        n %= size;
        for (int i = 0; i < size - n; i++) {
            head = head.next;
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}
