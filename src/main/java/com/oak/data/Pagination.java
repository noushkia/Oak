package com.oak.data;

import java.util.List;

import static java.lang.Math.max;

public class Pagination<T> {
    private Integer limit = 0;

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<T> getPage(List<T> items, Integer pageNumber) {
        int startIndex = (pageNumber - 1) * limit;
        int endIndex = Math.min(startIndex + limit, items.size());
        return items.subList(startIndex, endIndex);
    }

    public int getNumberOfPages(List<T> items) {
        int numItems = items.size();
        int numPages = numItems / limit;
        if (numItems % limit != 0) {
            numPages++;
        }
        return max(numPages, 1);
    }
}
