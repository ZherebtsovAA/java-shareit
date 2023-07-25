package ru.practicum.shareit.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest {
    private final PageRequest pageRequest;

    public CustomPageRequest(int from, int size, Sort sort) {
        int page = getPageNumber(from, size);
        pageRequest = PageRequest.of(page, size, sort);
    }

    public CustomPageRequest(int from, int size) {
        int page = getPageNumber(from, size);
        pageRequest = PageRequest.of(page, size);
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}