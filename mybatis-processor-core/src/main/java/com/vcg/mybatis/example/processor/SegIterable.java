package com.vcg.mybatis.example.processor;

import java.util.Iterator;

public class SegIterable<T> implements Iterable<T> {

    private long total = 0;

    private Iterator<T> iterator;

    private T last;

    @Override
    public Iterator<T> iterator() {
        return null;
    }


    public long getTotal() {
        return total;
    }

    public void increment() {
        total++;
    }

    public void setIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public Iterator<T> getIterator() {
        return iterator;
    }

    public void setLast(T last) {
        this.last = last;
    }

    public T getLast() {
        return last;
    }
}
