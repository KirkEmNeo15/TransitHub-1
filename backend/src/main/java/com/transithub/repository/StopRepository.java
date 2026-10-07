package com.transithub.repository;

import com.transithub.entity.Stop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StopRepository extends JpaRepository<Stop, Long> {

    boolean existsByNameIgnoreCase(String name);

    // For the search box on the map
    List<Stop> findByNameContainingIgnoreCase(String text);

    // For admin tables (search + pagination)
    Page<Stop> findByNameContainingIgnoreCase(String text, Pageable pageable);

    /**
     * First step of "stops near me": the database returns only the stops inside a
     * rectangle (cheap). The service then measures the exact distance in Java and sorts.
     */
    @Query("""
            SELECT s FROM Stop s
            WHERE s.latitude  BETWEEN :minLatitude  AND :maxLatitude
              AND s.longitude BETWEEN :minLongitude AND :maxLongitude
            """)
    List<Stop> findInBoundingBox(@Param("minLatitude") double minLatitude,
                                 @Param("maxLatitude") double maxLatitude,
                                 @Param("minLongitude") double minLongitude,
                                 @Param("maxLongitude") double maxLongitude);
}
