/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.util;

import de.daschi.core.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLHandler {

    private MySQL mySQL;

    public MySQL connectToMysql(final String hostname, final int port, final String database, final String user, final String password) {
        final MySQL mySQL = new MySQL(hostname, port, user, password, database);
        MySQL.using(mySQL);
        this.mySQL = mySQL;

        System.out.println("MySQL Connected");
        return mySQL;
    }

    public void addStation(String stationname, String systemname, final String coordinates, final String data, final String timestamp) {
        if (stationname.contains("'")) {
            stationname = stationname.replace("'", "");
        }
        if (systemname.contains("'")) {
            systemname = systemname.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("INSERT INTO Ressources(stationname,systemname,coordinates,ressourcedata,timestamp) VALUES ('" + stationname + "','" + systemname + "','" + coordinates + "','" + data + "','" + timestamp + "');");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStation(String stationname, String systemname, final String data, final String timestamp) {
        if (stationname.contains("'")) {
            stationname = stationname.replace("'", "");
        }
        if (systemname.contains("'")) {
            systemname = systemname.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("INSERT INTO Ressources(stationname,systemname,coordinates,ressourcedata,timestamp) VALUES ('" + stationname + "','" + systemname + "','" + "NODATA" + "','" + data + "','" + timestamp + "');");
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

    public void setSystemName(final String data, String stationName) {
        if (stationName.contains("'")) {
            stationName = stationName.replace("'", "");
        }
        try {
            this.mySQL.executeUpdate("UPDATE Ressources SET systemname = '" + data + "' WHERE stationname = '" + stationName + "';");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
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


}
