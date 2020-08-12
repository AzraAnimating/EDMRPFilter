/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.restapi;

import de.azraanimating.edmrpfilter.util.CacheManager;
import de.azraanimating.edmrpfilter.util.MySQLHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class RestApiController {

    public static MySQLHandler mySQLHandler;
    public static CacheManager cacheManager;

    @GetMapping("/edmrp/systemdata/")
    @ResponseBody
    public ResponseEntity<String> getStationsInSystem(@RequestParam final String system) {

        final ArrayList<String> stations = RestApiController.mySQLHandler.getAllStationsInSystem(system);

        final StringBuilder replyStringBuilder = new StringBuilder();

        replyStringBuilder.append("{\"stations\":").append(stations.toString()).append(",\"coordinates\":").append(RestApiController.mySQLHandler.getSystemCoordinates(system)).append("}");

        return ResponseEntity.status(HttpStatus.OK).body(replyStringBuilder.toString());

    }

    @GetMapping("/edmrp/ressources/")
    @ResponseBody
    public ResponseEntity<String> getAllStations(@RequestParam final String station) {

        return ResponseEntity.status(HttpStatus.OK).body("{\"ressources\":" + RestApiController.mySQLHandler.getStationData(station) + "}");

    }

    @GetMapping("/edmrp/allsystems/")
    @ResponseBody
    public ResponseEntity<String> getAllStations() {

        return ResponseEntity.status(HttpStatus.OK).body("{\"systems\":" + RestApiController.cacheManager.getSystems() + "}");

    }

    @GetMapping("/edmrp/coordinates/")
    @ResponseBody
    public ResponseEntity<String> getCoordinates(@RequestParam final String system) {

        return ResponseEntity.status(HttpStatus.OK).body("{\"coordinates\":" + RestApiController.mySQLHandler.getSystemCoordinates(system) + "}");

    }

    @GetMapping("/edmrp/ressourcestations/")
    @ResponseBody
    public ResponseEntity<String> getStationsSellingRessources(@RequestParam final String ressource) {

        final ArrayList<String> sellers = RestApiController.mySQLHandler.getStationsSellingRessource(ressource);
        final ArrayList<String> stationsWithSystems = new ArrayList<>();

        sellers.forEach(stationName -> {
            String systemName = RestApiController.cacheManager.getSystemForStation(stationName);
            if (systemName == null) {
                systemName = RestApiController.mySQLHandler.getSystemName(stationName);
                RestApiController.cacheManager.addSystemWithStationToCache(stationName, systemName);
            }
            stationsWithSystems.add(systemName + "#" + stationName);
        });


        return ResponseEntity.status(HttpStatus.OK).body("{\"stations\":" + stationsWithSystems.toString() + "}");

    }

    @GetMapping("/edmrp/system/")
    @ResponseBody
    public ResponseEntity<String> getSystemFromStation(@RequestParam final String station) {

        return ResponseEntity.status(HttpStatus.OK).body("{\"system\":" + RestApiController.cacheManager.getSystemForStation(station) + "}");

    }

}
