package com.transithub.controller;

import com.transithub.dto.response.RouteResponse;
import com.transithub.security.AuthenticatedUser;
import com.transithub.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** A logged-in user's saved routes. The user comes from the token, never from the URL. */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<RouteResponse> getFavorites(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        return favoriteService.getFavorites(currentUser.id());
    }

    @PostMapping("/{routeId}")
    public ResponseEntity<RouteResponse> addFavorite(@AuthenticationPrincipal AuthenticatedUser currentUser,
                                                     @PathVariable("routeId") Long routeId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.addFavorite(currentUser.id(), routeId));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> removeFavorite(@AuthenticationPrincipal AuthenticatedUser currentUser,
                                               @PathVariable("routeId") Long routeId) {
        favoriteService.removeFavorite(currentUser.id(), routeId);
        return ResponseEntity.noContent().build();
    }
}
