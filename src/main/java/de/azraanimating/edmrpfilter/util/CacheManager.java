/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheManager {

    private final HashMap<String, ArrayList<JSONObject>> cachedData = new HashMap<>();

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
}
