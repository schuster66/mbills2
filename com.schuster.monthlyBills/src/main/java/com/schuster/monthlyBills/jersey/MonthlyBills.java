package com.schuster.monthlyBills.jersey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;







public class MonthlyBills {
	private static 		Logger	log = LogManager.getLogger();
	
	public static ConcurrentHashMap<Integer,MBillsCategory> categories = new ConcurrentHashMap<>();
	
	public static void initialize(HashMap<String,String> parameters) {
		
		log.info("MonthlyBills - in initialize(hash)---- Newwwwwww ");
		
		String dbName = parameters.get("mysql_database");
		String dbUser = parameters.get("mysql_user");
		String dbPass = parameters.get("mysql_pass");
		log.trace("MonthlyBills database = " + dbName);
		
		log.trace("MonthlyBills Initializing Database and categories");
		
		MonthlyBillsDB.setDbConnectionString(dbName);
		MonthlyBillsDB.setDbUser(dbUser);
		MonthlyBillsDB.setDbPass(dbPass);
		
		MonthlyBillsDB.initializeDB();
	}
	
	public static MBillsCategory getOrCreateCategory(int categoryID) {
		if (categories.containsKey(categoryID)) {
			return categories.get(categoryID);
		}
		
		MBillsCategory cat = new MBillsCategory(categoryID);
		categories.put(categoryID, cat);
		return cat;
	}
	
	public static ArrayList<MBillsCategory> getAllCategories() {
		return new ArrayList<MBillsCategory>(categories.values()); 
	}
}
