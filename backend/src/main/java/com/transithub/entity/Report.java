package com.transithub.entity;

import com.transithub.entity.enums.ReportStatus;
import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/** A user's report that some route information is incorrect. */
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReportStatus status = ReportStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Report() {
    }

    public Report(User user, Route route, String description) {
        this.user = ValidationUtils.requireNonNull(user, "User");
        this.route = ValidationUtils.requireNonNull(route, "Route");
        setDescription(description);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Route getRoute() {
        return route;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = ValidationUtils.requireNotBlank(description, "Report description");
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = ValidationUtils.requireNonNull(status, "Report status");
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
