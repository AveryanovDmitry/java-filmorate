package ru.yandex.practicum.filmorate.exception;

import java.util.Arrays;

public class SearchBadParametrsException extends RuntimeException {
    public SearchBadParametrsException() {
        super("Указаны не все параметры");
    }

    public SearchBadParametrsException(String... search) {

        super(Arrays.stream(search)
                .reduce("По данным сво-вам не возможно начать поиск:",
                        (partialString, element) -> partialString + " " + element));
    }
}
