package com.transithub.repository;

import com.transithub.entity.FavoriteRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {

    List<FavoriteRoute> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<FavoriteRoute> findByUserIdAndRouteId(Long userId, Long routeId);

    boolean existsByUserIdAndRouteId(Long userId, Long routeId);
}
