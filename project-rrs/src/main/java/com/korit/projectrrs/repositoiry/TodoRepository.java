package com.korit.projectrrs.repositoiry;

import com.korit.projectrrs.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository {
    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId " +
            "AND FUNCTION('DATE', t.todoCreateAt) = :day " +
            "ORDER BY t.todoCreateAt ASC")
    Optional<List<Todo>> findTodosByUserIdAndDay(@Param("userId") String userId, @Param("day") LocalDate day);

    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId ")
    Optional<Todo> findTodoByUserId(@Param("userId") String userId);
}
