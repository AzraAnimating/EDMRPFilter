/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.util;

import de.azraanimating.edmrpfilter.restapi.RestApiController;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheManager {

    private final HashMap<String, ArrayList<JSONObject>> cachedData = new HashMap<>();
    private ArrayList<String> systems;
    private HashMap<String, String> stationSystems = new HashMap<>();
    private HashMap<String, JSONObject> coordinates = new HashMap<>();
    private final HashMap<String, HashMap<String, HashMap<String, Integer>>> materialInfo = new HashMap<>();//System//Station//Material//Price
    //---------------------^System---------^Station--------^Material--^Price

    public void loadSystemsIntoCache() {
        this.systems = RestApiController.mySQLHandler.getAllIndexedSystems();
    }

    public void loadStationsWithSystemsIntoCache() {
        this.stationSystems = RestApiController.mySQLHandler.getAllStationsWithSystems();
        System.out.println(this.stationSystems.size());
    }

    public void loadRessourcesWithSystemsIntoCache(final List<String> ressources) {
        final AtomicInteger i = new AtomicInteger();
        final AtomicInteger j = new AtomicInteger();
        ressources.forEach(ressource -> {
            RestApiController.mySQLHandler.getStationsSellingRessource(ressource).forEach(station -> {
                this.updateRessourceCacheData(this.getSystemForStation(station), station, ressource, RestApiController.mySQLHandler.getStationRessourcePrice(station, ressource));
                i.getAndIncrement();
                j.getAndIncrement();
            });
            System.out.println(ressource + " Cached " + i + " Stations");
            i.set(0);
        });
        System.out.println("Cached " + j.get() + " total");
    }

    public boolean isSystemChached(final String system) {
        return this.systems.contains(system);
    }

    public void addSystemToCache(final String system) {
        this.systems.add(system);
    }

    public ArrayList<String> getSystems() {
        return this.systems;
    }

    public void addSystemWithStationToCache(final String stationname, final String systemname) {
        this.stationSystems.put(stationname, systemname);
    }

    public void addToCache(final String stationName, final ArrayList<JSONObject> data) {
        this.cachedData.put(stationName, data);
    }

    public ArrayList<JSONObject> getCachedData(final String stationName) {
        return this.cachedData.get(stationName);
    }

    public boolean contains(final String stationName) {
        return this.cachedData.containsKey(stationName);
    }

    public boolean isSameData(final String stationName, final ArrayList<JSONObject> data) {
        return this.cachedData.get(stationName).equals(data);
    }

    public String getSystemForStation(final String stationName) {
        return this.stationSystems.get(stationName.toLowerCase());
    }

    public void cacheCoordinates() {
        this.coordinates = RestApiController.mySQLHandler.getCoordinates();
    }

    public JSONObject getCoordinates(final String systemName) {
        return this.coordinates.get(systemName);
    }

    public void updateRessourceCacheData(final String systemName, final String stationName, final String ressource, final int price) {
        if (!this.materialInfo.containsKey(systemName)) {
            final HashMap<String, Integer> ressources = new HashMap<>();
            ressources.put(ressource, price);

            final HashMap<String, HashMap<String, Integer>> stationData = new HashMap<>();
            stationData.put(stationName, ressources);

            this.materialInfo.put(systemName, stationData);
        } else {
            if (!this.materialInfo.get(systemName).containsKey(stationName)) {
                final HashMap<String, Integer> ressources = new HashMap<>();
                ressources.put(ressource, price);

                this.materialInfo.get(systemName).put(stationName, ressources);
            } else {
                this.materialInfo.get(systemName).get(stationName).put(ressource, price);
            }
        }
    }

    public void cacheStationRessource(final String systemName, final String stationName, final HashMap<String, Integer> materials) {
        if (!this.materialInfo.containsKey(systemName)) {
            final HashMap<String, HashMap<String, Integer>> station = new HashMap<>();
            station.put(stationName, materials);
            this.materialInfo.put(systemName, station);
        } else {
            this.materialInfo.get(systemName).put(stationName, materials);
        }
    }

    public HashMap<String, String> getStationsSellingRessource(final String ressource) {
        final HashMap<String, String> stationsSellingRessource = new HashMap<>();
        this.materialInfo.forEach((systemName, stations) -> {
            stations.forEach((stationName, materials) -> {
                if (materials.containsKey(ressource)) {
                    stationsSellingRessource.put(stationName, systemName);
                }
            });
        });
        return stationsSellingRessource;
    }

    public HashMap<String, HashMap<String, HashMap<String, Integer>>> getMaterialInfo() {
        return this.materialInfo;
    }
}
