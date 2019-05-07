package com.vcg.mybatis.example.starter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageWrapper<T> implements Page<T> {

    private long total;

    private int pages;

    private Pageable pageable;

    private List<T> content;

    public PageWrapper(List<T> content, Pageable pageable, long total) {
        this.content = content;
        this.pageable = pageable;
        this.total = total;
        if (total == 0) {
            this.pages = 0;
        } else {
            this.pages = (int) ((total / pageable.getPageSize()) + ((total % pageable.getPageSize()) > 0 ? 1 : 0));
        }
    }


    @Override
    public int getTotalPages() {
        return this.pages;
    }

    @Override
    public long getTotalElements() {
        return this.total;
    }

    @Override
    public int getNumber() {
        return this.pageable.isPaged() ? this.pageable.getPageNumber() : 0;
    }

    @Override
    public int getSize() {
        return this.pageable.isPaged() ? this.pageable.getPageSize() : 0;
    }

    @Override
    public int getNumberOfElements() {
        return content.size();
    }

    @Override
    public List<T> getContent() {
        return this.content;
    }

    @Override
    public boolean hasContent() {
        return this.content != null && this.content.size() > 0;
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
        return hasPrevious() ? pageable.previousOrFirst() : Pageable.unpaged();
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return new PageWrapper(this.content.stream()
                .map(converter)
                .collect(Collectors.toList()), this.pageable, this.total);
    }

    @Override
    public Iterator<T> iterator() {
        return this.content.iterator();
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", pages=" + pages +
                ", pageable=" + pageable +
                ", content=" + content +
                '}';
    }
}
