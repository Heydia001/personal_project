package com.korit.projectrrs.dto.review.request;

import lombok.Data;

@Data
public class ReviewPostRequestDto {
    private int reviewScore;
    private String reviewContent;
}