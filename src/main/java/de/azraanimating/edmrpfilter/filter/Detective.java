/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.filter;

import de.azraanimating.edmrpfilter.main.EliteDangerousMiningRoutePlanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Detective {

    private final EliteDangerousMiningRoutePlanner eliteDangerousMiningRoutePlanner;
    private final SaveManager saveManager;
    private List<String> searchedMaterials = new ArrayList<>();

    public Detective(final EliteDangerousMiningRoutePlanner eliteDangerousMiningRoutePlanner) {
        this.eliteDangerousMiningRoutePlanner = eliteDangerousMiningRoutePlanner;
        this.saveManager = new SaveManager(this.eliteDangerousMiningRoutePlanner.getCacheManager(), this.eliteDangerousMiningRoutePlanner.getMySQLHandler());
    }

    public void rawFilter(final String rawData) throws JSONException, IOException {

        final JSONObject fullObject = new JSONObject(rawData);

        if (fullObject.getString("$schemaRef").equals("https://eddn.edcd.io/schemas/commodity/3")) {
            final JSONObject message = fullObject.getJSONObject("message");

            final JSONArray commoditiesRaw = message.getJSONArray("commodities");

            final ArrayList<JSONObject> commodities = new ArrayList<>();

            for (int i = 0; i < commoditiesRaw.length(); i++) {
                commodities.add(commoditiesRaw.getJSONObject(i));
            }

            final ArrayList<JSONObject> valuables = new ArrayList<>();


            commodities.forEach(jsonObject -> {
                try {
                    if (this.searchedMaterials.contains(jsonObject.getString("name"))) {
                        valuables.add(jsonObject);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            });

            if (!valuables.isEmpty()) {
                final String stationName = message.getString("stationName");
                final String stationSystem = message.getString("systemName");

                this.saveManager.save(stationName, stationSystem, valuables, message.getString("timestamp"));
            }
        }
    }

    public void setSearchedMaterials(final String searchedMaterials) {
        this.searchedMaterials = Arrays.asList(searchedMaterials.split(";"));
    }

}
