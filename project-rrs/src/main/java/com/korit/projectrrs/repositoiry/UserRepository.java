package com.korit.projectrrs.repositoiry;

import com.korit.projectrrs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    @Query(value = """
SELECT
    *
FROM
    USERS U
WHERE
    U.USER_ID = :userId
    AND U.ROLES LIKE '%, ROLE_PROVIDER%';
""",nativeQuery = true)
    Optional<User> findProviderById(@Param("userId") Long userId);

    @Query(value = """
SELECT
    U.NAME,
    U.NICKNAME,
    U.PHONE,
    U.PROFILE_IMAGE_URL,
    AVG(R.REVIEW_SCORE) AS avgReviewScore
FQROM
    USERS U
INNER JOIN
    REVIEWS R ON U.USER_ID = R.PROVIDER_ID
WHERE
    U.ROLES LIKE '%, ROLE_PROVIDER%'
GROUP BY
    U.NAME, U.NICKNAME, U.PHONE, U.PROFILE_IMAGE_URL
            """, nativeQuery = true)
Optional<User> findChosenProviderById(@Param("providerId") Long providerId);
}