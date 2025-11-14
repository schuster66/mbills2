package com.schuster.monthlyBills.jersey;

import java.time.LocalDate;



public class MBillsCategory {
	private final int categoryID;
	private String category;
	private String displayName;
	private int position;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean firstHalf;
	private boolean inTotal;
	
	public MBillsCategory (int catID) {
		this.categoryID = catID;
	}

	public String getCategory() {
		return category;
	}

	public MBillsCategory setCategory(String category) {
		this.category = category;
		return this;
	}

	public String getDisplayName() {
		return displayName;
	}

	public MBillsCategory setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public int getPosition() {
		return position;
	}

	public MBillsCategory setPosition(int position) {
		this.position = position;
		return this;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public MBillsCategory setStartDate(LocalDate startDate) {
		this.startDate = startDate;
		return this;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public MBillsCategory setEndDate(LocalDate endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean isFirstHalf() {
		return firstHalf;
	}

	public MBillsCategory setFirstHalf(boolean firstHalf) {
		this.firstHalf = firstHalf;
		return this;
	}

	public boolean isInTotal() {
		return inTotal;
	}

	public MBillsCategory setInTotal(boolean inTotal) {
		this.inTotal = inTotal;
		return this;
	}

	public int getCategoryID() {
		return categoryID;
	}
	
	
	

}
