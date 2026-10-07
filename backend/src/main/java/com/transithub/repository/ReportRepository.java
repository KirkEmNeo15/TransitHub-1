package com.transithub.repository;

import com.transithub.entity.Report;
import com.transithub.entity.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<Report> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(ReportStatus status);
}
