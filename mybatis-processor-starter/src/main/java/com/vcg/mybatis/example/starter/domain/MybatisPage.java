package com.vcg.mybatis.example.starter.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MybatisPage<T> implements Page<T> {

    private List<T> contents;

    private long total;

    private Pageable pageable;


    @Override
    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) this.total / (double) getSize());
    }

    @Override
    public long getTotalElements() {
        return this.total;
    }

    @Override
    public int getNumber() {
        return this.pageable.getPageNumber();
    }

    @Override
    public int getSize() {
        return this.contents.size();
    }

    @Override
    public int getNumberOfElements() {
        return this.contents.size();
    }

    @Override
    public List<T> getContent() {
        return this.contents;
    }

    @Override
    public boolean hasContent() {
        return this.contents != null && !this.contents.isEmpty();
    }

    @Override
    public Sort getSort() {
        return this.pageable.getSort();
    }

    @Override
    public boolean isFirst() {
        return !hasPrevious();
    }

    @Override
    public boolean isLast() {
        return !hasNext();
    }

    @Override
    public boolean hasNext() {
        return getNumber() + 1 < getTotalPages();
    }

    @Override
    public boolean hasPrevious() {
        return getNumber() > 0;
    }

    @Override
    public Pageable nextPageable() {
        return hasNext() ? this.pageable.next() : Pageable.unpaged();
    }

    @Override
    public Pageable previousPageable() {
        return hasPrevious() ? this.pageable.previousOrFirst() : Pageable.unpaged();
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return new MybatisPage<U>()
                .setContents(getConvertedContent(converter))
                .setTotal(this.total)
                .setPageable(this.pageable);

    }

    @Override
    public Iterator<T> iterator() {
        return this.contents.iterator();
    }

    private <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
        return this.stream()
                .map(converter)
                .collect(Collectors.toList());
    }


    public MybatisPage<T> setContents(List<T> contents) {
        this.contents = contents;
        return this;
    }

    public MybatisPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public MybatisPage<T> setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

}
