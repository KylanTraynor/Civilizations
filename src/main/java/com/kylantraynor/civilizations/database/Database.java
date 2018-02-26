package com.kylantraynor.civilizations.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.BudgetEntry;
import com.kylantraynor.civilizations.protection.Lock;
import com.kylantraynor.civilizations.protection.LockKey;
import com.kylantraynor.civilizations.util.Util;

public abstract class Database {
	Civilizations plugin;
	Connection connection;
	
	public enum Databases{
		
		BUDGET_ENTRIES("civs_bgt_entries"),
		GROUPS("civs_groups"),
		PLOTS("civs_plots"),
		MEMBERS("civs_members"),
		ECONOMY("civs_eco"),
		LOCKS("civs_locks");
		
		private String name;
		
		Databases(String name){
			this.name = name;
		}
		public String toString(){
			return getName();
		}
		public String getName(){
			return name;
		}
	}
	
	public Database(Civilizations instance){
		plugin = instance;
	}
	
	public abstract Connection getSQLConnection();
	
	public abstract void load();
	
	public void initialize(){
		connection = getSQLConnection();
		try{
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + Databases.GROUPS + " WHERE group_id = ?");
			ResultSet rs = ps.executeQuery();
			close(ps,rs);
		} catch (SQLException ex){
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection to the database.", ex);
		}
	}
	
	public Integer getTokens(String string){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + Databases.GROUPS + " WHERE player = '" + string + "';");
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(rs.getString("player").equalsIgnoreCase(string.toLowerCase()))
					return rs.getInt("kills");
			}
		} catch (SQLException ex){
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try{
				if(ps != null)
					ps.close();
				if(conn != null)
					conn.close();
			} catch (SQLException ex){
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return 0;
	}
	
	public void setTokens(Player player, Integer tokens, Integer total){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = getSQLConnection();
			ps = conn.prepareStatement("REPLACE INTO " + Databases.GROUPS + " (player,kills,total) VALUES(?,?,?)");
			ps.setString(1,  player.getName().toLowerCase());
			ps.setInt(2, tokens);
			ps.setInt(3, total);
			ps.executeUpdate();
			return;
		} catch (SQLException ex){
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex){
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return;
	}
	
	public void setLock(Location location, Lock lock){
		//TODO adds the lock to the database.
	}
	
	public Lock getLock(Location location){
		//TODO gets the lock from the database.
		return null;
	}
	
	public void addLockKey(LockKey key){
		//TODO adds the key to the database;
	}
	
	public LockKey getLockKey(UUID id){
		//TODO gets the key from the databse.
		return null;
	}
	
	/**
	 * Inserts a Budget Entry into the DataBase.
	 * To avoid lag, use this method in an async task.
	 * @param entry
	 */
	public void insertBudgetEntry(BudgetEntry entry){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = getSQLConnection();
			ps = conn.prepareStatement("INSERT INTO " + Databases.BUDGET_ENTRIES + " (emiter_id, receiver_id, timestamp, amount, label) VALUES (?, ?, ?, ?, ?)" );
			if(entry.getEmiter() != null)
				ps.setBytes(1, Util.asBytes(entry.getEmiter()));
			if(entry.getReceiver() != null)
				ps.setBytes(2, Util.asBytes(entry.getReceiver()));
			ps.setTimestamp(3, Timestamp.from(entry.getInstant()));
			ps.setDouble(4, entry.getAmount());
			ps.setString(5, entry.getLabel());
			ps.executeUpdate();
		} catch (SQLException ex){
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try{
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex){
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
	}
	
	/**
	 * Gets a Budget Entry from the databse.
	 * To avoid lag, use it in an async task.
	 * @param involving
	 * @param from
	 * @param to
	 * @return
	 */
	public List<BudgetEntry> getBudgetEntries(UUID involving, Instant from, Instant to){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + Databases.BUDGET_ENTRIES + " WHERE (emiter_id = ? OR receiver_id = ?) AND (timestamp BETWEEN ? AND ?);");
			ps.setBytes(1, Util.asBytes(involving));
			ps.setBytes(2, Util.asBytes(involving));
			ps.setTimestamp(3, Timestamp.from(from));
			ps.setTimestamp(4, Timestamp.from(to));
			rs = ps.executeQuery();
			
			List<BudgetEntry> result = new ArrayList<BudgetEntry>();
			while(rs.next()){
				byte[] emiter = rs.getBytes("emiter_id");
				byte[] receiver = rs.getBytes("receiver_id");
				Timestamp ts = rs.getTimestamp("timestamp");
				double amount = rs.getDouble("amount");
				String label = rs.getString("label");
				try{
					result.add(new BudgetEntry(Util.asUuid(emiter), Util.asUuid(receiver), label, amount, ts.toInstant()));
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			return result;
		} catch (SQLException ex){
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try{
				if(ps != null)
					ps.close();
				if(conn != null)
					conn.close();
			} catch (SQLException ex){
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return new ArrayList<BudgetEntry>();
	}
	
	public void close(PreparedStatement ps, ResultSet rs){
		try{
			if(ps != null){
				ps.close();
			}
			if(rs != null)
				rs.close();
		}catch (SQLException ex){
			Error.close(plugin, ex);
		}
	}
}
