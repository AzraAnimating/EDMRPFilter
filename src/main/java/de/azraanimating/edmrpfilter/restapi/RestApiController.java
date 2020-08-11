/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.restapi;

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

    @GetMapping("/edmrp/stations/")
    @ResponseBody
    public ResponseEntity<String> getStationsInSystem(@RequestParam final String system) {

        final ArrayList<String> stations = RestApiController.mySQLHandler.getAllStationsInSystem(system);

        final StringBuilder replyStringBuilder = new StringBuilder();

        replyStringBuilder.append("{\"stations\":").append(stations.toString()).append("}");

        return ResponseEntity.status(HttpStatus.OK).body(replyStringBuilder.toString());

    }

    @GetMapping("/edmrp/ressources/")
    @ResponseBody
    public ResponseEntity<String> getRessourcesAtStation(@RequestParam final String station) {

        return ResponseEntity.status(HttpStatus.OK).body("{\"ressources\":" + RestApiController.mySQLHandler.getStationData(station) + "}");

    }

    @GetMapping("/edmrp/allsystems/")
    @ResponseBody
    public ResponseEntity<String> getRessourcesAtStation() {

        return ResponseEntity.status(HttpStatus.OK).body("{\"systems\":" + RestApiController.mySQLHandler.getAllIndexedSystems() + "}");

    }

    @GetMapping("/edmrp/coordinates/")
    @ResponseBody
    public ResponseEntity<String> getCoordinates(final String system) {

        return ResponseEntity.status(HttpStatus.OK).body("{\"coordinates\":" + RestApiController.mySQLHandler.getSystemCoordinates(system) + "}");

    }

}
