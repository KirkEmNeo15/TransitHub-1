package com.transithub.entity;

import com.transithub.entity.enums.AlertSeverity;
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

/** An announcement or service alert. It may be general or about one specific route. */
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AlertSeverity severity = AlertSeverity.INFO;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Optional: no route means a general announcement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    // Optional: the admin who published it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    protected Alert() {
    }

    public Alert(String title, String message, AlertSeverity severity) {
        setTitle(title);
        setMessage(message);
        setSeverity(severity);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = ValidationUtils.requireNotBlank(title, "Alert title");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = ValidationUtils.requireNotBlank(message, "Alert message");
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = ValidationUtils.requireNonNull(severity, "Alert severity");
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
