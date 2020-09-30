/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.util;

import de.daschi.core.MySQL;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQLHandler {

    private MySQL mySQL;
    private List<String> searchedRessources;

    public void setSearchedRessources(final List<String> searchedRessources) {
        this.searchedRessources = searchedRessources;
    }

    public MySQL connectToMysql(final String hostname, final int port, final String database, final String user, final String password) throws SQLException {
        final MySQL mySQL = new MySQL(hostname, port, user, password, database);
        mySQL.openConnection();
        this.mySQL = mySQL;

        System.out.println("MySQL Connected");
        return mySQL;
    }

    public void addStation(String stationname, String systemname, final String coordinates, final String data, final String timestamp, final String[] ressources) {
        if (stationname.contains("'")) {
            stationname = stationname.replace("'", "");
        }
        if (systemname.contains("'")) {
            systemname = systemname.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("INSERT INTO Ressources(stationname,systemname,coordinates,ressourcedata,timestamp) VALUES ('" + stationname + "','" + systemname + "','" + coordinates + "','" + data + "','" + timestamp + "');");
            this.mySQL.executeUpdate("INSERT INTO Station_Ressources(stationname) VALUES ('" + stationname + "');");
            for (int i = 0; i < ressources.length; i++) {
                this.setStationRessourcePrice(ressources[i].split("-")[0], Integer.parseInt(ressources[i].split("-")[1]), stationname);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public String getStationData(String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT ressourcedata FROM Ressources WHERE stationname = '" + stationName + "';");
            resultSet.next();
            return resultSet.getString("ressourcedata");
        } catch (final Exception e) {
        }
        return null;
    }

    public void setStationData(final String data, String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("UPDATE Ressources SET ressourcedata = '" + data + "' WHERE stationname = '" + stationName + "';");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTimestamp(String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT timestamp FROM Ressources WHERE stationname = " + stationName + ";");
            resultSet.next();
            return resultSet.getString("ressourcedata");
        } catch (final Exception e) {
        }
        return null;
    }

    public void setTimestamp(final String data, String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("UPDATE Ressources SET timestamp = '" + data + "' WHERE stationname = '" + stationName + "';");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSystemCoordinates(String systemName) {
        if (systemName.contains("'")) {
            systemName = systemName.replace("'", "");
        }
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT coordinates FROM Ressources WHERE systemname = '" + systemName + "';");
            resultSet.next();
            return resultSet.getString("coordinates");
        } catch (final Exception e) {
        }
        return null;
    }

    public void setSystemCoordinates(final String data, String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("UPDATE Ressources SET coordinates = '" + data + "' WHERE stationname = '" + stationName + "';");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSystemName(String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT systemname FROM Ressources WHERE stationname = '" + stationName + "';");
            resultSet.next();
            return resultSet.getString("systemname");
        } catch (final Exception e) {
        }
        return null;
    }

    public void setSystemName(String data, String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        if (data.contains("'")) {
            data = data.replace("'", "--");
        }
        try {
            this.mySQL.executeUpdate("UPDATE Ressources SET systemname = '" + data + "' WHERE stationname = '" + stationName + "';");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void setStationRessourcePrice(final String ressource, final int sellPrice, String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("UPDATE Station_Ressources SET " + ressource + " = " + sellPrice + " WHERE stationname = '" + stationName + "';");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStationRessourcePrice(String stationName, final String ressource) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        stationName = this.mySQL.removeSQLInjectionPossibility(stationName);
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT " + ressource + " FROM Station_Ressources WHERE stationname = '" + stationName + "';");
            resultSet.next();
            return resultSet.getInt(ressource);
        } catch (final Exception e) {
        }
        return 0;
    }

    public ArrayList<String> getStationsSellingRessource(String ressource) {
        if (ressource.contains("'")) {
            ressource = ressource.replace("'", "");
        }
        if (this.searchedRessources.contains(ressource)) {
            ressource = this.mySQL.removeSQLInjectionPossibility(ressource);
            final ArrayList<String> stations = new ArrayList<>();
            final ArrayList<String> stationSystems = new ArrayList<>();
            try {
                final ResultSet resultSet = this.mySQL.executeQuery("SELECT * FROM Station_Ressources WHERE " + ressource + " IS NOT NULL;");
                while (resultSet.next()) {
                    stations.add(resultSet.getString("stationName").replace("--", "'"));
                }
                return stations;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<String> getAllStationsInSystem(String systemName) {
        if (systemName.contains("'")) {
            systemName = systemName.replace("'", "");
        }
        systemName = this.mySQL.removeSQLInjectionPossibility(systemName);
        final ArrayList<String> stations = new ArrayList<>();
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT * FROM Ressources WHERE systemname = '" + systemName + "';");
            while (resultSet.next()) {
                stations.add(resultSet.getString("stationName"));
            }
            return stations;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getAllIndexedSystems() {
        final ArrayList<String> systems = new ArrayList<>();
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT systemname FROM Ressources;");
            while (resultSet.next()) {
                if (!systems.contains(resultSet.getString("systemname"))) {
                    systems.add(resultSet.getString("systemname"));
                }
            }
            return systems;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> getAllStationsWithSystems() {
        final HashMap<String, String> systems = new HashMap<>();
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT systemname, stationname FROM Ressources;");
            while (resultSet.next()) {
                systems.put(resultSet.getString("stationname").toLowerCase(), resultSet.getString("systemname"));
            }
            return systems;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public HashMap<String, JSONObject> getCoordinates() {
        final HashMap<String, JSONObject> coords = new HashMap<>();
        try {
            final ResultSet resultSet = this.mySQL.executeQuery("SELECT systemname, coordinates FROM Ressources;");
            while (resultSet.next()) {
                final String coordis = resultSet.getString("coordinates");
                if (!coordis.equals("NODATA")) {
                    coords.put(resultSet.getString("systemname"), new JSONObject(coordis));
                }
            }
            return coords;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
