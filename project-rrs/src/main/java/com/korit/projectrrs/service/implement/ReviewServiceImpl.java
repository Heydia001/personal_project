package com.korit.projectrrs.service.implement;

import com.korit.projectrrs.common.ResponseMessage;
import com.korit.projectrrs.dto.ResponseDto;
import com.korit.projectrrs.dto.review.request.ReviewPostRequestDto;
import com.korit.projectrrs.dto.review.request.ReviewPutRequestDto;
import com.korit.projectrrs.dto.review.response.ReviewAvgScoreResponseDto;
import com.korit.projectrrs.dto.review.response.ReviewGetResponseDto;
import com.korit.projectrrs.dto.review.response.ReviewPostResponseDto;
import com.korit.projectrrs.dto.review.response.ReviewPutResponseDto;
import com.korit.projectrrs.entity.Review;
import com.korit.projectrrs.entity.User;
import com.korit.projectrrs.repositoiry.ReviewRepository;
import com.korit.projectrrs.repositoiry.UserRepository;
import com.korit.projectrrs.security.PrincipalUser;
import com.korit.projectrrs.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseDto<ReviewPostResponseDto> createReview(Long userId, @Valid ReviewPostRequestDto dto)  {
        ReviewPostResponseDto data = null;
        Long providerId = dto.getProviderId();
        int score = dto.getReviewScore();
        String content = dto.getReviewContent();

        //유효성 검사
        if (content == null || content.isEmpty() || content.length() > 500) {
            return ResponseDto.setFailed(ResponseMessage.REVIEW_TOO_lONG);
        }
        if (score > 5 || score < 0) {
            return ResponseDto.setFailed(ResponseMessage.REVIEW_SCORE_NUMBER_VALIDATION);
        }
        try{
            // Provider 등록 여부 확인
            User provider = userRepository.findProviderById(providerId)
                    .orElseThrow(() -> new InternalException(ResponseMessage.NOT_EXIST_PROVIDER_ID));

            // User 조회
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseDto.setFailed(ResponseMessage.NOT_EXIST_USER_ID);
            }

            User user = optionalUser.get();

            Review review = Review.builder()
                    .user(user)
                    .provider(provider)
                    .reviewScore(score)
                    .reviewContent(content)
                    .reviewCreatedAt(LocalDateTime.now())
                    .build();

            reviewRepository.save(review);

            // 성공 응답 데이터 생성
            data = new ReviewPostResponseDto(review);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.setFailed(ResponseMessage.DATABASE_ERROR);
        }
        return ResponseDto.setSuccess(ResponseMessage.SUCCESS, data);
    }

    @Override
    public ResponseDto<List<ReviewGetResponseDto>> getReviewsByProvider(Long providerId) {
        List<ReviewGetResponseDto> data = null;
        try {
            Optional<List<Review>> reviews = reviewRepository.findReviewsByProvider(providerId);
            if (reviews.isPresent()) {
                data = reviews.get().stream()
                        .map(ReviewGetResponseDto::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.setFailed(ResponseMessage.DATABASE_ERROR);
        }
        return ResponseDto.setSuccess(ResponseMessage.SUCCESS, data);
    }

    @Override
    public ResponseDto<ReviewAvgScoreResponseDto> getAverageReviewScoreByProvider(Long providerId) {
        ReviewAvgScoreResponseDto data = null;
        try {
            Double avgScore = reviewRepository.findAverageReviewScoreByProvider(providerId);
            if (!userRepository.existsById(providerId)) {
                return ResponseDto.setFailed(ResponseMessage.NOT_EXIST_PROVIDER_ID);
            }
            data = new ReviewAvgScoreResponseDto(avgScore);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.setFailed(ResponseMessage.DATABASE_ERROR);
        }
        return ResponseDto.setSuccess(ResponseMessage.SUCCESS, data);
    }

    @Override
    public ResponseDto<ReviewGetResponseDto> getByReviewId(Long reviewId) {
        ReviewGetResponseDto data = null;
        try {
            Optional<Review> optionalReview = reviewRepository.findById(reviewId);
            if (optionalReview.isPresent()) {
                data = new ReviewGetResponseDto(optionalReview.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.setFailed(ResponseMessage.DATABASE_ERROR);
        }
        return ResponseDto.setSuccess(ResponseMessage.SUCCESS, data);
    }

    @Override
    public ResponseDto<ReviewPutResponseDto> updateReview(ReviewPutRequestDto dto) {
        ReviewPutResponseDto data = null;
        int score = dto.getReviewScore();
        String content = dto.getReviewContent();
        try {
            Optional<Review> optionalReview = reviewRepository.findById(dto.getReviewId());
            if (optionalReview.isPresent()) {
                Review respondedReview = optionalReview.get().toBuilder()
                        .reviewScore(score)
                        .reviewContent(content)
                        .build();
                reviewRepository.save(respondedReview);
                data = new ReviewPutResponseDto(respondedReview);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.setFailed(ResponseMessage.DATABASE_ERROR);
        }
        return ResponseDto.setSuccess(ResponseMessage.SUCCESS, data);
    }

    @Override
    public ResponseDto<Void> deleteReview(Long reviewId) {
        try {
            if(!reviewRepository.existsById(reviewId)) ResponseDto.setFailed(ResponseMessage.DATABASE_ERROR);
            reviewRepository.deleteById(reviewId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.setFailed(ResponseMessage.DATABASE_ERROR);
        }
        return ResponseDto.setSuccess(ResponseMessage.SUCCESS, null);
    }
}