/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.filter;

import de.azraanimating.edmrpfilter.util.CacheManager;
import de.azraanimating.edmrpfilter.util.MySQLHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveManager {

    private final CacheManager cacheManager;
    private final MySQLHandler mySQLHandler;

    public SaveManager(final CacheManager cacheManager, final MySQLHandler mySQLHandler) {
        this.cacheManager = cacheManager;
        this.mySQLHandler = mySQLHandler;
    }

    public void save(final String stationName, final String systemName, final ArrayList<JSONObject> data, final String timestamp) throws IOException, JSONException {
        if (this.cacheManager.contains(stationName)) {
            if (!this.cacheManager.isSameData(stationName, data)) {
                this.cacheManager.addToCache(stationName, data);
                this.mySQLHandler.setStationData(data.toString(), stationName);
                this.mySQLHandler.setTimestamp(timestamp, stationName);
                if (stationName.contains("-")) {
                    if (!this.mySQLHandler.getSystemName(stationName).equals(systemName)) {
                        this.mySQLHandler.setSystemName(systemName, stationName);
                        this.mySQLHandler.setSystemCoordinates(this.getSystemCoordinates(systemName), stationName);
                        System.out.println("Refreshed System Data for Fleetcarrier '" + stationName + "'");
                    }
                }
                if (!this.cacheManager.isSystemCached(systemName)) {
                    this.cacheManager.addSystemToCache(systemName);
                }

                final HashMap<String, Integer> materialMap = new HashMap<>();
                data.forEach(jsonObject -> {
                    try {
                        this.mySQLHandler.setStationRessourcePrice(jsonObject.getString("name"), jsonObject.getInt("sellPrice"), stationName);
                        materialMap.put(jsonObject.getString("name"), jsonObject.getInt("sellPrice"));
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                    this.cacheManager.cacheStationRessource(systemName, stationName, materialMap);
                });
                this.cacheManager.addSystemWithStationToCache(stationName, systemName);
                System.out.println("Updated Data for '" + stationName + "' in '" + systemName + "' in Cache & Database");
            }
            this.cacheManager.addSystemWithStationToCache(stationName, systemName);
        } else {
            final String stationData = this.mySQLHandler.getStationData(stationName);

            this.cacheManager.addSystemWithStationToCache(stationName, systemName);

            if (stationData == null) {

                final ArrayList<String> materialList = new ArrayList<>();
                final HashMap<String, Integer> materialMap = new HashMap<>();

                data.forEach(jsonObject -> {
                    try {
                        materialList.add(jsonObject.getString("name") + "-" + jsonObject.getInt("sellPrice"));
                        materialMap.put(jsonObject.getString("name"), jsonObject.getInt("sellPrice"));
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                });

                final String[] materials = materialList.toArray(new String[0]);

                this.mySQLHandler.addStation(stationName, systemName, this.getSystemCoordinates(systemName), data.toString(), timestamp, materials);
                this.cacheManager.addSystemWithStationToCache(stationName, systemName);
                this.cacheManager.cacheStationRessource(systemName, stationName, materialMap);
                System.out.println("Saved new Data for '" + stationName + "' in '" + systemName + "' to Database <- Indexed");
                if (!this.cacheManager.isSystemCached(systemName)) {
                    this.cacheManager.addSystemToCache(systemName);
                }
            } else {
                if (!this.mySQLHandler.getStationData(stationName).equals(data.toString())) {
                    this.mySQLHandler.setStationData(data.toString(), stationName);
                    this.mySQLHandler.setTimestamp(timestamp, stationName);
                    this.cacheManager.addToCache(stationName, data);
                    if (stationName.contains("-")) {
                        if (!this.mySQLHandler.getSystemName(stationName).equals(systemName)) {
                            this.mySQLHandler.setSystemName(systemName, stationName);

                            this.mySQLHandler.setSystemCoordinates(this.getSystemCoordinates(systemName), stationName);
                            System.out.println("Refreshed System Data for Fleetcarrier '" + stationName + "'");
                        }
                    }
                    if (!this.cacheManager.isSystemCached(systemName)) {
                        this.cacheManager.addSystemToCache(systemName);
                    }
                    final HashMap<String, Integer> materialMap = new HashMap<>();
                    data.forEach(jsonObject -> {
                        try {
                            this.mySQLHandler.setStationRessourcePrice(jsonObject.getString("name"), jsonObject.getInt("sellPrice"), stationName);
                            materialMap.put(jsonObject.getString("name"), jsonObject.getInt("sellPrice"));
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    this.cacheManager.cacheStationRessource(systemName, stationName, materialMap);
                    this.cacheManager.addSystemWithStationToCache(stationName, systemName);
                    System.out.println("Updated Data for '" + stationName + "' in '" + systemName + "' in Cache & Database [1. Entry to Cache]");
                }
            }
        }
    }


    private String getSystemCoordinates(final String systemName) throws JSONException, IOException {
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        final Request request = new Request.Builder()
                .url("https://www.edsm.net/api-v1/systems?systemName=" + systemName + "&showCoordinates=1")
                .method("GET", null)
                .addHeader("Cookie", "PHPSESSID=lp6gqp3cciu67a2jof5uggnd9m")
                .build();
        final Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            return "NODATA";
        }

        final JSONArray jsonArray = new JSONArray(response.body().string());

        if (jsonArray.length() > 0) {
            final JSONObject edsmData = jsonArray.getJSONObject(0);
            return edsmData.getJSONObject("coords").toString();
        } else {
            return "NODATA";
        }
    }
}
