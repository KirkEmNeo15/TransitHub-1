package com.transithub.repository;

import com.transithub.entity.Route;
import com.transithub.entity.enums.RouteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteCode(String routeCode);

    boolean existsByRouteCode(String routeCode);

    long countByStatus(RouteStatus status);

    // Is any route still operated by this transportation service?
    boolean existsByTransportationId(Long transportationId);

    // @EntityGraph loads the transportation and fare together with the routes (fewer queries)
    @EntityGraph(attributePaths = {"transportation", "fare"})
    List<Route> findAllByOrderByRouteNameAsc();

    @EntityGraph(attributePaths = {"transportation", "fare"})
    List<Route> findByStatusOrderByRouteNameAsc(RouteStatus status);

    // For admin tables (search by name or code, with pagination)
    Page<Route> findByRouteNameContainingIgnoreCaseOrRouteCodeContainingIgnoreCase(
            String name, String code, Pageable pageable);

    /** All routes that pass through a stop ("Routes passing through this stop"). */
    @Query("""
            SELECT DISTINCT r FROM Route r
            JOIN r.routeStops rs
            WHERE rs.stop.id = :stopId
            ORDER BY r.routeName
            """)
    List<Route> findRoutesThroughStop(@Param("stopId") Long stopId);

    /**
     * ROUTE SEARCH. Finds routes that have a stop matching the origin text that comes
     * BEFORE a stop matching the destination text (stopOrder decides the direction).
     * Example: "Lipa" -> "Batangas" finds Lipa-to-Batangas routes but not Batangas-to-Lipa ones.
     * LOCATE(...) > 0 means "contains", and LOWER makes it case-insensitive.
     * Origin and destination must not be blank (the service checks that).
     */
    @EntityGraph(attributePaths = {"transportation", "fare"})
    @Query("""
            SELECT DISTINCT r FROM Route r
            JOIN r.routeStops fromStop
            JOIN r.routeStops toStop
            WHERE r.status = :status
              AND LOCATE(LOWER(:origin), LOWER(fromStop.stop.name)) > 0
              AND LOCATE(LOWER(:destination), LOWER(toStop.stop.name)) > 0
              AND fromStop.stopOrder < toStop.stopOrder
            ORDER BY r.estimatedMinutes ASC
            """)
    List<Route> findDirectRoutes(@Param("origin") String origin,
                                 @Param("destination") String destination,
                                 @Param("status") RouteStatus status);
}
