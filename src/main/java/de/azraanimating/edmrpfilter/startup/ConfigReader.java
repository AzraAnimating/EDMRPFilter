/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.startup;

import de.azraanimating.edmrpfilter.main.EliteDangerousMiningRoutePlanner;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;

public class ConfigReader {

    private String hostname;
    private int port;
    private String database;
    private String user;
    private String password;
    private int apiport;

    public void readConfig() throws InvalidConfigurationException, IOException {
        final File configFile = new File("config.yml");

        final YamlFile yamlFile = new YamlFile(configFile);
        yamlFile.load();

        this.hostname = yamlFile.getString("hostname");
        this.port = yamlFile.getInt("port");
        this.database = yamlFile.getString("database");
        this.user = yamlFile.getString("username");
        this.password = yamlFile.getString("password");
        this.apiport = yamlFile.getInt("apiport");
    }

    public void readRessources(final EliteDangerousMiningRoutePlanner eliteDangerousMiningRoutePlanner) throws InvalidConfigurationException, IOException {
        final File configFile = new File("config.yml");
        final YamlFile yamlFile = new YamlFile(configFile);
        yamlFile.load();
        eliteDangerousMiningRoutePlanner.getDetective().setSearchedMaterials(yamlFile.getString("ressources"));
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public int getApiport() {
        return this.apiport;
    }
}
