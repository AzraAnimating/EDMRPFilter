/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.restapi;

import de.azraanimating.edmrpfilter.main.EliteDangerousMiningRoutePlanner;
import de.azraanimating.edmrpfilter.util.CacheManager;
import de.azraanimating.edmrpfilter.util.MySQLHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

        final HashMap<String, String> seller = RestApiController.cacheManager.getStationsSellingRessource(ressource);

        final ArrayList<String> stationsWithSystems = new ArrayList<>();

        seller.forEach((stationName, systemName) -> {
            stationsWithSystems.add(systemName + "#" + stationName);
        });

        return ResponseEntity.status(HttpStatus.OK).body("{\"stations\":" + stationsWithSystems.toString() + "}");

    }

    @GetMapping("/edmrp/ressourcesystem/")
    @ResponseBody
    public ResponseEntity<String> getSystemSellingRessource(@RequestParam final String ressource) {

        final Collection<String> seller = RestApiController.cacheManager.getStationsSellingRessource(ressource).values();

        return ResponseEntity.status(HttpStatus.OK).body("{\"stations\":" + seller.toString() + "}");

    }

    @GetMapping("/edmrp/system/")
    @ResponseBody
    public ResponseEntity<String> getSystemFromStation(@RequestParam final String station) {

        return ResponseEntity.status(HttpStatus.OK).body("{\"system\":" + RestApiController.cacheManager.getSystemForStation(station) + "}");

    }

    @GetMapping("/edmrp/materials/")
    @ResponseBody
    public ResponseEntity<String> getMaterials() {

        return ResponseEntity.status(HttpStatus.OK).body("{\"materials\":" + EliteDangerousMiningRoutePlanner.searchedMaterials + "}");

    }

    @GetMapping("/edmrp/pricing/")
    @ResponseBody
    public ResponseEntity<String> getPricingForMaterialFromHighestToLowest(@RequestParam final String ressource) {

        final HashMap<String, HashMap<String, HashMap<String, Integer>>> materialInfo = RestApiController.cacheManager.getMaterialInfo();
        final HashMap<String, Integer> pricingWithSystems = new HashMap<>();
        final ArrayList<String> assembledReturnData = new ArrayList<>();

        materialInfo.forEach((systemname, stations) -> {
            final AtomicInteger highestPrice = new AtomicInteger();
            final AtomicReference<String> namesWithHighestPrice = new AtomicReference<>("");
            stations.forEach((stationName, ressources) -> {
                namesWithHighestPrice.set("");
                if (ressources.get(ressource) != null) {
                    if (ressources.get(ressource) > highestPrice.get()) {
                        highestPrice.set(ressources.get(ressource));
                        namesWithHighestPrice.set(systemname + "#" + stationName);
                    }
                }
            });
            if (highestPrice.get() > 0) {
                pricingWithSystems.put(namesWithHighestPrice.get(), highestPrice.get());
            }
        });

        final ArrayList<String> sortedSystems = this.flip(this.sortIntegers(pricingWithSystems));

        for (int i = 0; i < Math.round(sortedSystems.size() * 0.1); i++) {
            try {
                final JSONObject coordinates = RestApiController.cacheManager.getCoordinates(sortedSystems.get(i).split("#")[0]);
                if (coordinates != null) {
                    assembledReturnData.add(sortedSystems.get(i) + "#" + coordinates.getDouble("x") + "#" + coordinates.getDouble("y") + "#" + coordinates.getDouble("z"));
                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("{\"prices\":" + assembledReturnData.toString() + "}");
    }


    @GetMapping("/edmrp/v1/bestprice/")
    @ResponseBody
    public ResponseEntity<String> getBestPrice(@RequestParam final String ressource) {

        final HashMap<String, HashMap<String, HashMap<String, Integer>>> materialInfo = RestApiController.cacheManager.getMaterialInfo();
        final HashMap<String, Integer> pricingWithSystems = new HashMap<>();
        final ArrayList<String> assembledReturnData = new ArrayList<>();

        materialInfo.forEach((systemname, stations) -> {
            final AtomicInteger highestPrice = new AtomicInteger();
            final AtomicReference<String> namesWithHighestPrice = new AtomicReference<>("");
            stations.forEach((stationName, ressources) -> {
                namesWithHighestPrice.set("");
                if (ressources.get(ressource) != null) {
                    if (ressources.get(ressource) > highestPrice.get()) {
                        highestPrice.set(ressources.get(ressource));
                        namesWithHighestPrice.set("\"systemname\" : \"" + systemname + "\" , \"stationname\" : \"" + stationName + "\" , ");
                    }
                }
            });
            if (highestPrice.get() > 0) {
                pricingWithSystems.put(namesWithHighestPrice.get(), highestPrice.get());
            }
        });

        final ArrayList<String> sortedSystems = this.flip(this.sortIntegers(pricingWithSystems));

        for (int i = 0; i < Math.round(sortedSystems.size() * 0.1); i++) {
            assembledReturnData.add("{ " + sortedSystems.get(i) + " }");
        }

        return ResponseEntity.status(HttpStatus.OK).body("{ \"prices\" : " + assembledReturnData.toString() + " }");
    }


    private HashMap<String, Integer> sort(final HashMap<String, Integer> listToSort) {

        return listToSort.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    }

    private ArrayList<String> sortIntegers(final HashMap<String, Integer> listToSort) {
        final Set set = listToSort.entrySet();
        final Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            final Map.Entry mapEntry = (Map.Entry) iterator.next();
        }

        final Map<Integer, String> map = RestApiController.sortByValues(listToSort);
        final Set set2 = map.entrySet();
        final Iterator iterator2 = set2.iterator();
        final ArrayList<String> finalList = new ArrayList<>();
        while (iterator2.hasNext()) {
            final Map.Entry me2 = (Map.Entry) iterator2.next();
            if (!me2.getValue().equals("0")) {
                finalList.add(me2.getKey() + "\"price\" : " + me2.getValue());
            }
        }
        return finalList;
    }

    private ArrayList<String> sortIntegersForJsonReturn(final HashMap<String, Integer> listToSort) {
        final Set set = listToSort.entrySet();
        final Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            final Map.Entry mapEntry = (Map.Entry) iterator.next();
        }

        final Map<Integer, String> map = RestApiController.sortByValues(listToSort);
        final Set set2 = map.entrySet();
        final Iterator iterator2 = set2.iterator();
        final ArrayList<String> finalList = new ArrayList<>();
        while (iterator2.hasNext()) {
            final Map.Entry me2 = (Map.Entry) iterator2.next();
            if (!me2.getValue().equals("0")) {
                finalList.add(me2.getKey() + "#" + me2.getValue());
            }
        }
        return finalList;
    }


    private ArrayList<String> flip(final ArrayList<String> mapToFlip) {
        final ArrayList<String> finalMap = new ArrayList<>();

        final int currentPos = mapToFlip.size() - 1;

        for (int i = currentPos; i > 0; i--) {
            finalMap.add(mapToFlip.get(i));
        }

        return finalMap;
    }

    private static HashMap sortByValues(final HashMap map) {
        final List list = new LinkedList(map.entrySet());

        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));

        final HashMap sortedHashMap = new LinkedHashMap();
        for (final Iterator it = list.iterator(); it.hasNext(); ) {
            final Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}
