package com.unibooker.resource.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 서비스 카테고리
 */
@Getter
@RequiredArgsConstructor
public enum ServiceCategory {
    /** 예약 */
    RESERVATION("예약"),

    /** 좌석 예매 */
    SEAT("좌석 예매"),

    /** 이벤트 신청 */
    EVENT("이벤트 신청"),

    /** 전체 */
    ALL("전체");

    private final String label;
}