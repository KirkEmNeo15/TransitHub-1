package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/** A route saved by a user. Users and routes are many-to-many, so this link has its own class. */
@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "route_id"}))
public class FavoriteRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected FavoriteRoute() {
    }

    public FavoriteRoute(User user, Route route) {
        this.user = ValidationUtils.requireNonNull(user, "User");
        this.route = ValidationUtils.requireNonNull(route, "Route");
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
