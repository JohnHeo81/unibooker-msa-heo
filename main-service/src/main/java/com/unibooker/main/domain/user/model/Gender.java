package com.unibooker.main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 성별
 */
@Getter
@AllArgsConstructor
public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    OTHER("기타");

    private final String label;
}