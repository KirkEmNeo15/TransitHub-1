package com.transithub.service;

import com.transithub.entity.Route;

import java.util.List;

/**
 * Contract for finding routes between two places.
 *
 * An interface says WHAT a search service must do, not HOW. Today we plan one
 * implementation (direct routes only). Later we could add another one that
 * finds routes with transfers, without changing the code that uses this interface.
 */
public interface RouteSearchService {

    /**
     * Finds ACTIVE routes with a stop matching the origin that comes BEFORE
     * a stop matching the destination.
     *
     * @param origin      text typed by the user, for example "Lipa"
     * @param destination text typed by the user, for example "Batangas"
     * @return matching routes, or an empty list when none match
     */
    List<Route> findDirectRoutes(String origin, String destination);
}
