package com.space.repository;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ShipRepo extends JpaRepository<Ship, Long> {
    Page<Ship> findBySpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByNameIsContainingAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String name, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByPlanetContainingAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String planet, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(ShipType shipType, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByNameIsContainingAndPlanetContainingAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String name, String planet, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByNameIsContainingAndShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String name, ShipType shipType, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByNameIsContainingAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String name, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByPlanetContainingAndShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String planet, ShipType shipType, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByPlanetContainingAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String planet, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(ShipType shipType, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByNameIsContainingAndPlanetContainingAndShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String name, String planet, ShipType shipType, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByNameIsContainingAndShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String name, ShipType shipType, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByPlanetContainingAndShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String planet, ShipType shipType, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
    Page<Ship> findByNameIsContainingAndPlanetContainingAndShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(String name, String planet, ShipType shipType, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, Date after, Date before, Pageable pageable);
}
