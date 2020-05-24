package com.space.service;

import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class ShipService {

    private ShipRepo shipRepo;

    @Autowired
    public ShipService(ShipRepo shipRepo) {
        this.shipRepo = shipRepo;
    }

    public Ship findById(Long id){
        if(id <= 0) throw new BadRequestException();
        return shipRepo.findById(id).orElseThrow(NotFoundException::new);
    }

    public Ship createNewShip(String name, String planet, ShipType shipType, Date prodDate, Boolean isUsed, Double speed, Integer crewSize) {
        if(name.length() > 50 || planet.length() > 50 || name.isEmpty() || planet.isEmpty() ||
                 crewSize < 0 || crewSize > 9999) throw new BadRequestException();

        if((Math.round(speed * 100) / 100.0) < 0.1 || (Math.round(speed * 100) / 100.0) > 0.99)
            throw new BadRequestException();

        LocalDate localDate = prodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year  = localDate.getYear();
        if(year < 2800 || year > 3019)
            throw new BadRequestException();

        Double rating = (80 * (isUsed ? 0.5 : 1) * speed) / (3019 - year + 1);

        Ship ship = new Ship(name, planet, shipType, prodDate, isUsed, Math.round(speed * 100) / 100.0, crewSize, rating);
        return shipRepo.save(ship);
    }

    public Ship updateShip(Long id, String name, String planet, ShipType shipType, Date prodDate, Boolean isUsed, Double speed, Integer crewSize) {
        if(id <= 0) throw new BadRequestException();

        Ship shipFromDB = shipRepo.findById(id).orElseThrow(NotFoundException::new);

        if(name == null && planet == null && shipType == null && prodDate == null && speed == null && crewSize == null && isUsed == null)
            return shipFromDB;

        if(name != null) {
            if (name.length() <= 50 && !name.isEmpty())
                shipFromDB.setName(name);
            else throw new BadRequestException();
        }

        if(planet != null) {
            if (planet.length() <= 50 && !planet.isEmpty())
                shipFromDB.setPlanet(planet);
            else throw new BadRequestException();
        }

        double speedToUpdate;
        if (speed != null) {
            speedToUpdate = Math.round(speed * 100) / 100.0;
            if(speedToUpdate > 0.1 && speedToUpdate < 0.99)
                shipFromDB.setSpeed(speedToUpdate);
            else throw new BadRequestException();
        }

        if(crewSize != null) {
            if (crewSize <= 9999 && crewSize > 0)
                shipFromDB.setCrewSize(crewSize);
            else throw new BadRequestException();
        }

        if(shipType != null)
            shipFromDB.setShipType(shipType);

        Integer year;
        if(prodDate != null) {
            LocalDate localDate = prodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            year  = localDate.getYear();
            if(year >= 2800 && year <= 3019)
                shipFromDB.setProdDate(prodDate);
            else throw new BadRequestException();
        }
        year = shipFromDB.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();

        if(isUsed != null) shipFromDB.setUsed(isUsed);

        double rating = (80 * (shipFromDB.isUsed() ? 0.5 : 1) * shipFromDB.getSpeed()) / (3019 - year + 1);
        shipFromDB.setRating(Math.round(rating * 100) / 100.0);

        shipRepo.save(shipFromDB);

        return shipFromDB;
    }

    public void deleteShip(Long id) {
        if (id <= 0) throw new BadRequestException();
        shipRepo.findById(id).orElseThrow(NotFoundException::new);
        shipRepo.deleteById(id);
    }

    public int getCount(Map<String, Object> parametersToSearch) {
        Pageable pageable = PageRequest.of(0, 3);
        return (int) findByCriteria(parametersToSearch, pageable).getTotalElements();
    }

    public List<Ship> getShips(Map<String, Object> parametersToSearch, Pageable pageable) {
        return findByCriteria(parametersToSearch, pageable).getContent();
    }

    private Page<Ship> findByCriteria(Map<String, Object> parametersToSearch, Pageable pageable) {
        Double minSpeed = parametersToSearch.get("minSpeed") == null ? 0.00 : (Double) parametersToSearch.get("minSpeed");
        Double maxSpeed = parametersToSearch.get("maxSpeed") == null ? 1.0 : (Double) parametersToSearch.get("maxSpeed");
        Integer minCrewSize = parametersToSearch.get("minCrewSize") == null ? 0 : (Integer) parametersToSearch.get("minCrewSize");
        Integer maxCrewSize = parametersToSearch.get("maxCrewSize") == null ? 10000 : (Integer) parametersToSearch.get("maxCrewSize");
        Double minRating = parametersToSearch.get("minRating") == null ? 0.0 : (Double) parametersToSearch.get("minRating");
        Double maxRating = parametersToSearch.get("maxRating") == null ? Double.MAX_VALUE : (Double) parametersToSearch.get("maxRating");
        Date after = parametersToSearch.get("after") == null ? new Date(26204666806826l) : new Date((Long) parametersToSearch.get("after"));
        Date before = parametersToSearch.get("before") == null ? new Date(33115544942804l) : new Date((Long) parametersToSearch.get("before"));

        Page<Ship> result = null;

        if (parametersToSearch.containsKey("name") && parametersToSearch.containsKey("planet") && parametersToSearch.containsKey("shipType") && parametersToSearch.containsKey("isUsed")) {
            result = shipRepo.findByNameIsContainingAndPlanetContainingAndShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("name"), (String) parametersToSearch.get("planet"), (ShipType) parametersToSearch.get("shipType"), (Boolean) parametersToSearch.get("isUsed"),
                    minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("name") && parametersToSearch.containsKey("planet") && parametersToSearch.containsKey("shipType")) {
            result = shipRepo.findByNameIsContainingAndPlanetContainingAndShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("name"), (String) parametersToSearch.get("planet"), (ShipType) parametersToSearch.get("shipType"),
                    minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("name") && parametersToSearch.containsKey("shipType") && parametersToSearch.containsKey("isUsed")) {
            result = shipRepo.findByNameIsContainingAndShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("name"), (ShipType) parametersToSearch.get("shipType"), (Boolean) parametersToSearch.get("isUsed"),
                    minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("planet") && parametersToSearch.containsKey("shipType") && parametersToSearch.containsKey("isUsed")) {
            result = shipRepo.findByPlanetContainingAndShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("planet"), (ShipType) parametersToSearch.get("shipType"), (Boolean) parametersToSearch.get("isUsed"),
                    minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("name") && parametersToSearch.containsKey("planet")) {
            result = shipRepo.findByNameIsContainingAndPlanetContainingAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("name"), (String) parametersToSearch.get("planet"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("name") && parametersToSearch.containsKey("shipType")) {
            result = shipRepo.findByNameIsContainingAndShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("name"), (ShipType) parametersToSearch.get("shipType"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("name") && parametersToSearch.containsKey("isUsed")) {
            result = shipRepo.findByNameIsContainingAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("name"), (Boolean) parametersToSearch.get("isUsed"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("planet") && parametersToSearch.containsKey("shipType")) {
            result = shipRepo.findByPlanetContainingAndShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("planet"), (ShipType) parametersToSearch.get("shipType"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("planet") && parametersToSearch.containsKey("isUsed")) {
            result = shipRepo.findByPlanetContainingAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (String) parametersToSearch.get("planet"), (Boolean) parametersToSearch.get("isUsed"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("shipType") && parametersToSearch.containsKey("isUsed")) {
            result = shipRepo.findByShipTypeAndIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(
                    (ShipType) parametersToSearch.get("shipType"), (Boolean) parametersToSearch.get("isUsed"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("name")) {
            result = shipRepo.findByNameIsContainingAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween((String) parametersToSearch.get("name"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if (parametersToSearch.containsKey("planet")) {
            result = shipRepo.findByPlanetContainingAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween((String) parametersToSearch.get("planet"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else if(parametersToSearch.containsKey("shipType")) {
            result = shipRepo.findByShipTypeAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween((ShipType) parametersToSearch.get("shipType"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        }else if(parametersToSearch.containsKey("isUsed")) {
            result = shipRepo.findByIsUsedAndSpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween((Boolean) parametersToSearch.get("isUsed"), minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);
        } else result = shipRepo.findBySpeedBetweenAndCrewSizeBetweenAndRatingBetweenAndProdDateBetween(minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, after, before, pageable);

        return result;
    }
}
