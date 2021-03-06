/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.main;

import de.azraanimating.edmrpfilter.filter.Detective;
import de.azraanimating.edmrpfilter.reader.EddnReader;
import de.azraanimating.edmrpfilter.restapi.Application;
import de.azraanimating.edmrpfilter.restapi.RestApiController;
import de.azraanimating.edmrpfilter.startup.ConfigReader;
import de.azraanimating.edmrpfilter.util.CacheManager;
import de.azraanimating.edmrpfilter.util.MySQLHandler;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EliteDangerousMiningRoutePlanner {

    private final Detective detective;
    private final MySQLHandler mySQLHandler;
    private final CacheManager cacheManager;
    public static List<String> searchedMaterials;

    public EliteDangerousMiningRoutePlanner() throws InvalidConfigurationException, IOException, SQLException {
        this.mySQLHandler = new MySQLHandler();

        final ConfigReader configReader = new ConfigReader();
        configReader.readConfig();

        this.mySQLHandler.connectToMysql(configReader.getHostname(), configReader.getPort(), configReader.getDatabase(), configReader.getUser(), configReader.getPassword());

        this.cacheManager = new CacheManager();

        this.detective = new Detective(this);

        configReader.readRessources(this);

        this.mySQLHandler.setSearchedRessources(this.detective.getSearchedMaterials());

        final Application application = new Application();
        application.startSpring(configReader.getApiport());

        RestApiController.mySQLHandler = this.mySQLHandler;
        RestApiController.cacheManager = this.cacheManager;

        this.cacheManager.loadSystemsIntoCache();
        this.cacheManager.loadStationsWithSystemsIntoCache();
        this.cacheManager.loadRessourcesWithSystemsIntoCache(this.detective.getSearchedMaterials());
        this.cacheManager.cacheCoordinates();

        EliteDangerousMiningRoutePlanner.searchedMaterials = this.detective.getSearchedMaterials();

        final EddnReader eddnReader = new EddnReader(this.detective);
        eddnReader.readStream();
    }

    public Detective getDetective() {
        return this.detective;
    }

    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    public MySQLHandler getMySQLHandler() {
        return this.mySQLHandler;
    }


}
