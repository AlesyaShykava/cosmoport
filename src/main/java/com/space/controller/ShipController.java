package com.space.controller;

import com.space.exception.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("rest/ships")
public class ShipController {
    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public List<Ship> getShips(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String planet,
                               @RequestParam(required = false) ShipType shipType,
                               @RequestParam(required = false) Long after,
                               @RequestParam(required = false) Long before,
                               @RequestParam(required = false) Boolean isUsed,
                               @RequestParam(required = false) Double minSpeed,
                               @RequestParam(required = false) Double maxSpeed,
                               @RequestParam(required = false) Integer minCrewSize,
                               @RequestParam(required = false) Integer maxCrewSize,
                               @RequestParam(required = false) Double minRating,
                               @RequestParam(required = false) Double maxRating,
                               @RequestParam(required = false) ShipOrder order,
                               @RequestParam(required = false) Integer pageNumber,
                               @RequestParam(required = false) Integer pageSize
                                   ) {
        Map<String, Object> filterCriteria = fillFilterMap(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        Pageable pageable = PageRequest.of(pageNumber == null ? 0 : pageNumber, pageSize == null ? 3 : pageSize, Sort.by(order == null ? ShipOrder.ID.getFieldName() : order.getFieldName()));
        return shipService.getShips(filterCriteria, pageable);
    }

    @GetMapping("/count")
    public int getShipCount (@RequestParam(required = false) String name,
                              @RequestParam(required = false) String planet,
                              @RequestParam(required = false) ShipType shipType,
                              @RequestParam(required = false) Long after,
                              @RequestParam(required = false) Long before,
                              @RequestParam(required = false) Boolean isUsed,
                              @RequestParam(required = false) Double minSpeed,
                              @RequestParam(required = false) Double maxSpeed,
                              @RequestParam(required = false) Integer minCrewSize,
                              @RequestParam(required = false) Integer maxCrewSize,
                              @RequestParam(required = false) Double minRating,
                              @RequestParam(required = false) Double maxRating) {
        Map<String, Object> filterCriteria = fillFilterMap(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.getCount(filterCriteria);
    }

    @PostMapping()
    public Ship addNewShip(@RequestBody Ship ship)
    {
        if(ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null) throw new BadRequestException();
        return shipService.createNewShip(ship.getName(), ship.getPlanet(), ship.getShipType(), ship.getProdDate(), ship.isUsed() == null ? false : ship.isUsed(), ship.getSpeed(), ship.getCrewSize());
    }


    @PostMapping("/{id}")
    public Ship updateShip(@PathVariable Long id, @RequestBody Ship ship){
        return shipService.updateShip(id, ship.getName(), ship.getPlanet(), ship.getShipType(), ship.getProdDate(), ship.isUsed(), ship.getSpeed(), ship.getCrewSize());
    }

    @GetMapping("/{id}")
    public Ship getShip(@PathVariable Long id){
        return shipService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteShip (@PathVariable Long id) {
        shipService.deleteShip(id);
    }

    private Map<String, Object> fillFilterMap(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {
        Map<String, Object> filterCriteria = new HashMap<>();
        if(name != null) filterCriteria.put("name", name);
        if(planet != null) filterCriteria.put("planet", planet);
        if(shipType != null) filterCriteria.put("shipType", shipType);
        if(after != null) filterCriteria.put("after", after);
        if(before != null) filterCriteria.put("before", before);
        if(isUsed != null) filterCriteria.put("isUsed", isUsed);
        if(minSpeed != null) filterCriteria.put("minSpeed", minSpeed);
        if(maxSpeed != null) filterCriteria.put("maxSpeed", maxSpeed);
        if(minCrewSize != null) filterCriteria.put("minCrewSize", minCrewSize);
        if(maxCrewSize != null) filterCriteria.put("maxCrewSize", maxCrewSize);
        if(minRating != null) filterCriteria.put("minRating", minRating);
        if(maxRating != null) filterCriteria.put("maxRating", maxRating);
        return filterCriteria;
    }

}
