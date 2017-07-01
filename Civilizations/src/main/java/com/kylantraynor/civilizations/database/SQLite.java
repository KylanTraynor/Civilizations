package com.kylantraynor.civilizations.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.kylantraynor.civilizations.Civilizations;

public class SQLite extends Database{
	String dbName;
	public SQLite(Civilizations instance){
		super(instance);
		dbName = plugin.getSettings().getSQLiteFilename();
	}
	
	public String SQLiteCreateBudgetEntriesTable = "CREATE TABLE IF NOT EXISTS " + Databases.BUDGET_ENTRIES + " (" +
			"`emiter_id` binary(16) NOT NULL," +
			"`reciever_id` binary(16) NOT NULL," +
			"`timestamp` timestamp NOT NULL," +
			"`amount` float NOT NULL," +
			"`label` varchar(250) NOT NULL," +
			"PRIMARY KEY (`emiter_id`)" +
			");";
	
	public String SQLiteCreateEconomicEntitiesTable = "CREATE TABLE IF NOT EXISTS " + Databases.ECONOMY + " (" +
			"`eco_id` binary(16) NOT NULL," +
			"`balance` float NOT NULL," +
			"PRIMARY KEY (`id`)" +
			");";
	
	public String SQLiteCreateGroupsTable = "CREATE TABLE IF NOT EXISTS " + Databases.GROUPS + " (" +
			"`group_id` binary(16) NOT NULL," +
			"`name` varchar(250) NOT NULL," + 
			"PRIMARY KEY (`group_id`)" +
			");";

	public String SQLiteCreatePlotTable = "CREATE TABLE IF NOT EXISTS " + Databases.PLOTS + " (" +
			"`plot_id` binary(16) NOT NULL," +
			"`group_id` binary(16) NOT NULL," +
			"`plot_type` varchar(150) NOT NULL," +
			"`purchase_price` float NOT NULL," +
			"`purchasable` boolean NOT NULL," +
			"`rent_price` float NOT NULL," +
			"`rentable` boolean NOT NULL," +
			"`owner` binary(16) NOT NULL," +
			"PRIMARY KEY (`plot_id`)" +
			");";
	
	public String SQLiteCreateMembersTable = "CREATE TABLE IF NOT EXISTS " + Databases.MEMBERS + " (" +
			"`player_id` bynary(16) NOT NULL," + 
			"`group_id` binary(16) NOT NULL," +
			"PRIMARY KEY (`player_id`)" +
			");";
	
	@Override
	public Connection getSQLConnection() {
		File dataFolder = new File(plugin.getDataFolder(), dbName + ".db");
		if(!dataFolder.exists()){
			try{
				dataFolder.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "File write error: " + dbName + ".db");
			}
		}
		try{
			if(connection != null && !connection.isClosed()){
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
			return connection;
		} catch (SQLException ex){
			plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
		} catch (ClassNotFoundException ex){
			plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it, Put it in /lib folder.");
		}
		
		return null;
	}

	@Override
	public void load() {
		connection = getSQLConnection();
		try{
			Statement s = connection.createStatement();
			s.executeUpdate(SQLiteCreateGroupsTable);
			s.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		initialize();
	}
}
