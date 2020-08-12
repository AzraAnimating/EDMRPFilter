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

public class CacheManager {

    private final HashMap<String, ArrayList<JSONObject>> cachedData = new HashMap<>();
    private ArrayList<String> systems;
    private HashMap<String, String> stationSystems = new HashMap<>();

    public void loadSystemsIntoCache() {
        this.systems = RestApiController.mySQLHandler.getAllIndexedSystems();
    }

    public void loadStationsWithSystemsIntoCache() {
        this.stationSystems = RestApiController.mySQLHandler.getAllStationsWithSystems();
        System.out.println(this.stationSystems.size());
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
}
