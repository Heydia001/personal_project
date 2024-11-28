package com.korit.projectrrs.repositoiry;

import com.korit.projectrrs.entity.AvailableDateOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableDateOfWeekRepository extends JpaRepository<AvailableDateOfWeek, Long> {

}