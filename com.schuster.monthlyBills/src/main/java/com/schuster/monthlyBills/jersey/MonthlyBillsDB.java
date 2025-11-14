package com.schuster.monthlyBills.jersey;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class MonthlyBillsDB {
	private static 		Logger	log = LogManager.getLogger();
	
	//private static final ZoneId zone = ZoneId.of("America/Chicago");
	private static final ZoneId zone = ZoneId.of("UTC");
	
	private static	String		dbConnectionString;
	private static	String		dbUser;
	private static	String		dbPass;
	
	private static int defaultLimit = 1000;
	
	private static BasicDataSource ds = new BasicDataSource();
	

	public static String getDbConnectionString() {
		return dbConnectionString;
	}

	public static void setDbConnectionString(String dbConnectionString) {
		MonthlyBillsDB.dbConnectionString = dbConnectionString;
	}

	public static String getDbUser() {
		return dbUser;
	}

	public static void setDbUser(String dbUser) {
		MonthlyBillsDB.dbUser = dbUser;
	}

	public static String getDbPass() {
		return dbPass;
	}

	public static void setDbPass(String dbPass) {
		MonthlyBillsDB.dbPass = dbPass;
	}
	public static void initializeDB() {
		log.trace("Initializing pool with connection string = " + dbConnectionString);
		
		ds.setUrl(dbConnectionString);
		ds.setUsername(dbUser);
		ds.setPassword(dbPass);
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
		ds.setMaxOpenPreparedStatements(100);
		
		buildCategoriesFromDB();
		
	}
	
	public static void buildCategoriesFromDB() {
		log.trace("MBILLS Entering getRoomsFromDB()");
		String selectQuery = "select category_id, category, displayname, position, startdate, enddate, firsthalf, intotal from categories";
		PreparedStatement selectPreparedStatement = null;
		Connection dbConnection = null;
		try {
		 dbConnection = ds.getConnection();
		 //dbConnection = this.connectToDB();
		 selectPreparedStatement = dbConnection.prepareStatement(selectQuery);
		 ResultSet rs = selectPreparedStatement.executeQuery();
		 while (rs.next()) {
			 int categoryID = rs.getInt("category_id");
			 String category = rs.getString("category");
			 String displayName = rs.getString("displayname");
			 int position = rs.getInt("position");
			 
			 LocalDate startDateLD = LocalDate.now();
			 Date startDate = rs.getDate("startdate");
			 if (startDate != null) {
				startDateLD = startDate.toLocalDate(); 
			 }
			 
			 LocalDate endDateLD = LocalDate.now();
			 Date endDate = rs.getDate("enddate");
			 if (endDate != null) {
				 endDateLD = endDate.toLocalDate();
			 }
			 
			 boolean firsthalf = rs.getBoolean("firsthalf");
			 boolean inTotal = rs.getBoolean("intotal");
			 
			 MBillsCategory categoryObj = MonthlyBills.getOrCreateCategory(categoryID)
					 								.setCategory(category)
					 								.setDisplayName(displayName)
					 								.setPosition(position)
					 								.setStartDate(startDateLD)
					 								.setEndDate(endDateLD)
					 								.setFirstHalf(firsthalf)
					 								.setInTotal(inTotal);
			 log.trace("MBILLS created categoryObj " + categoryObj.getDisplayName());
					 					
			 
		 }
		} catch (SQLException e) {
			log.error("MBILLS SQL ERROR" + e.toString());
	   } finally {
			if (selectPreparedStatement != null) 
				try { selectPreparedStatement.close(); } 
				catch (SQLException logOrIgnore) {log.error("SQL ERROR selectPreparedStatement " + logOrIgnore.toString());}
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log.error("SQL closeConnection " + e.toString());
				}
			}
	   }
	}
	
	public static ArrayList<MBillsEntry> getEntriesForMonth(LocalDate yearMonth) {
		
		log.trace("MBILLS Entering getEntriesForMonth no paws");
		
		ConcurrentHashMap<Integer, MBillsEntry> entries = new ConcurrentHashMap<>();
		
		String selectQuery = "select c.category_id, c.displayname, c.category, e.paydate, e.amount, c.firsthalf, c.intotal from categories c, entries e where c.category_id = e.category_id " +
								"and e.yearMonth = ?";
		log.trace("MBILLS SQL = " + selectQuery);
		PreparedStatement selectPreparedStatement = null;
		Connection dbConnection = null;
		try {
		 dbConnection = ds.getConnection();
		 //dbConnection = this.connectToDB();
		 selectPreparedStatement = dbConnection.prepareStatement(selectQuery);
		 selectPreparedStatement.setDate(1, Date.valueOf(yearMonth));
		 ResultSet rs = selectPreparedStatement.executeQuery();
		 while (rs.next()) {
			 int categoryID = rs.getInt("c.category_id");
			 String category = rs.getString("c.category");
			 String displayName = rs.getString("c.displayname");
			 
			 LocalDate payDateLD = LocalDate.now();
			 Date payDate = rs.getDate("e.paydate");
			 if (payDate != null) {
				payDateLD = payDate.toLocalDate(); 
			 }
			 
			 BigDecimal amount = rs.getBigDecimal("e.amount");
			 
			 boolean firstHalf = rs.getBoolean("c.firsthalf");
			 boolean inTotal = rs.getBoolean("c.intotal");
			 
			 log.trace("MBILLS found entry for " + yearMonth.toString() + 
					 	" --- " + displayName + " --- " + payDateLD.toString() + " --- " + amount.toString() + " -- category = " + category);
			 MBillsEntry entry = new MBillsEntry()
					 				.setCategoryId(categoryID)
					 				.setItem(displayName)
					 				.setKey(categoryID)
					 				.setPayAmount(amount)
					 				.setPayDate(payDateLD)
					 				.setIntotal(inTotal)
					 				.setFirsthalf(firstHalf);
			 entries.put(categoryID, entry);		 					 					
			 
		 }
		 
		} catch (SQLException e) {
			log.error("MBILLS SQL ERROR" + e.toString());
	   } finally {
			if (selectPreparedStatement != null) 
				try { selectPreparedStatement.close(); } 
				catch (SQLException logOrIgnore) {log.error("SQL ERROR selectPreparedStatement " + logOrIgnore.toString());}
			//if (dbConnection != null) { connectionPool.closeConnection(dbConnection); };
	   }
		
		String sql2 = "select c.category_id, c.category, e.paydate, e.amount from categories c, entries e where " +
						"c.category_id = e.category_id and e.yearmonth = DATE_SUB(?, INTERVAL 1 MONTH)";
		log.trace("MBILLS sql2 = " + sql2);
		PreparedStatement stmnt2 = null;
		try {
			stmnt2 = dbConnection.prepareStatement(sql2);
			stmnt2.setDate(1, Date.valueOf(yearMonth));
			ResultSet rs2 = stmnt2.executeQuery();
			while (rs2.next()) {
				int categoryID = rs2.getInt("c.category_id");
				String category = rs2.getString("c.category");
				LocalDate payDateLD = LocalDate.now();
				Date payDate = rs2.getDate("e.paydate");
				if (payDate != null) {
					payDateLD = payDate.toLocalDate(); 
				}
				BigDecimal amount = rs2.getBigDecimal("e.amount");
				log.trace("MBILLS found LAST MONTHS entry for " + category + 
					 	" --- " + payDateLD.toString() + " --- " + amount.toString());
				
				MBillsEntry entry;
				if (entries.containsKey(categoryID)) {
					entry = entries.get(categoryID);
				} else {
					log.trace("MBILLS could not find category for this month " + category + " - creating new one");
					entry = new MBillsEntry()
							.setCategoryId(categoryID)
				 			.setKey(categoryID);
							
					if (MonthlyBills.categories.containsKey(categoryID)) {
						MBillsCategory cat = MonthlyBills.categories.get(categoryID);
						entry.setItem(cat.getCategory());
						entry.setFirsthalf(cat.isFirstHalf());
						entry.setIntotal(cat.isInTotal());
						entry.setPosition(cat.getPosition());
					}
					entries.put(categoryID, entry);
				}
				entry.setLastMonth(payDate.toString() + " - $" + amount.toString());
				// Find entry and add last months info to it here
			}
			
		} catch (SQLException e) {
			log.error("MBILLS SQL ERROR" + e.toString());
		} finally {
			if (stmnt2 != null) 
				try { stmnt2.close(); } 
				catch (SQLException logOrIgnore) {log.error("SQL ERROR stmnt2 " + logOrIgnore.toString());}
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log.error("SQL closeConnection " + e.toString());
				}
			}
		}
		
		// Now lets loop through all of the possible categories and build the arraylist
		// if the this is not before the start date and not after the end date
		
		ArrayList<MBillsEntry> entriesInOrder = new ArrayList<>();
		
		for (MBillsCategory cat : MonthlyBills.categories.values()) {
			log.trace(cat.getDisplayName() + ":" + cat.getStartDate().toString() + ":" + cat.getEndDate().toString());
			if ((cat.getStartDate().isBefore(yearMonth)) && (cat.getEndDate().isAfter(yearMonth)) ) {
				MBillsEntry newEntry;
				
				log.trace("here for " + cat.getDisplayName());
				
				if (entries.containsKey(cat.getCategoryID())) {
					log.trace("here 2 for " + cat.getDisplayName());
					newEntry = entries.get(cat.getCategoryID());
					newEntry.setPosition(cat.getPosition());
				} else {
					log.trace("here 3 for " + cat.getDisplayName());
					newEntry = new MBillsEntry()
							.setCategoryId(cat.getCategoryID())
				 			.setKey(cat.getCategoryID())
				 			.setItem(cat.getCategory())
				 			.setIntotal(cat.isInTotal())
				 			.setFirsthalf(cat.isFirstHalf())
				 			.setPosition(cat.getPosition());
				}
				entriesInOrder.add(newEntry);
			}
		}
		
		
		

		log.trace("here 99 " + entriesInOrder.size());
		Collections.sort(entriesInOrder, Comparator.comparingInt(MBillsEntry::getPosition));
		log.trace("100");
		return entriesInOrder;
	}
	
	public static void addEntry(MBillsEntryInput entryIn) {
		log.trace("Entering addEntry");
		
		String sql = "INSERT INTO entries (category_id, paydate, yearmonth, amount ) VALUES ( ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE paydate=?, amount=?";
		
		// Need to find category object for a couple of reasons
		// Parse out the yearmonth into year and month and then 
		// get the localdate of day 1 of that combo. 
		// Or just change the json in the calling script to pass year and month
		
		String[] fields = entryIn.getYearMonth().split("-");
		int year = Integer.parseInt(fields[0]);
		int month = Integer.parseInt(fields[1]);
		
		LocalDate yearMonth = LocalDate.of(year, month, 1);
		LocalDate payDate = LocalDate.parse(entryIn.getPaydate());
		BigDecimal amount = new BigDecimal(entryIn.getAmount());
		
		log.trace("MBILLS add entry yearMonth = " + yearMonth.toString() + 
				  " - payDate = " + payDate.toString()  +
				  " - db key = " + entryIn.getKey() + 
				  " - amount = " + amount
						  );
		Connection dbConnection = null;
    	PreparedStatement stmnt = null;
    	
    	try {
			dbConnection = ds.getConnection();
			stmnt = dbConnection.prepareStatement(sql);
			
			stmnt.setInt(1,entryIn.getKey());
			stmnt.setDate(2, Date.valueOf(payDate));
			stmnt.setDate(3, Date.valueOf(yearMonth));
			stmnt.setBigDecimal(4, amount);
			stmnt.setDate(5, Date.valueOf(payDate));
			stmnt.setBigDecimal(6, amount);
			
			stmnt.executeUpdate();
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e.toString());;
		} finally {
			if (stmnt != null) 
				try { stmnt.close(); } 
				catch (SQLException logOrIgnore) {log.error("SQL ERROR stmnt2 " + logOrIgnore.toString());}
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log.error("SQL closeConnection " + e.toString());
				}
			}
		}
		
}
	
	
	
}
