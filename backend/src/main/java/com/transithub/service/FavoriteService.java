package com.transithub.service;

import com.transithub.dto.response.RouteResponse;
import com.transithub.entity.FavoriteRoute;
import com.transithub.entity.Route;
import com.transithub.entity.User;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.mapper.RouteMapper;
import com.transithub.repository.FavoriteRouteRepository;
import com.transithub.repository.RouteRepository;
import com.transithub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A user's saved routes. The user id is passed in by the controller
 * (taken from the logged-in user in Phase 10).
 */
@Service
public class FavoriteService {

    private final FavoriteRouteRepository favoriteRouteRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    public FavoriteService(FavoriteRouteRepository favoriteRouteRepository,
                           UserRepository userRepository,
                           RouteRepository routeRepository,
                           RouteMapper routeMapper) {
        this.favoriteRouteRepository = favoriteRouteRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> getFavorites(Long userId) {
        return favoriteRouteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(favorite -> routeMapper.toResponse(favorite.getRoute()))
                .toList();
    }

    @Transactional
    public RouteResponse addFavorite(Long userId, Long routeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", routeId));

        if (favoriteRouteRepository.existsByUserIdAndRouteId(userId, routeId)) {
            throw new DuplicateResourceException("This route is already in your favorites");
        }
        favoriteRouteRepository.save(new FavoriteRoute(user, route));
        return routeMapper.toResponse(route);
    }

    @Transactional
    public void removeFavorite(Long userId, Long routeId) {
        FavoriteRoute favorite = favoriteRouteRepository.findByUserIdAndRouteId(userId, routeId)
                .orElseThrow(() -> new ResourceNotFoundException("This route is not in your favorites"));
        favoriteRouteRepository.delete(favorite);
    }
}
