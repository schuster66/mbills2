package com.schuster.monthlyBills.jersey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MBillsEntryInput {
	@JsonProperty("yearmonth")
	public String yearMonth;
	public int key;
	public String paydate;
	public String amount;
	
	
	public String getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getPaydate() {
		return paydate;
	}
	public void setPaydate(String paydate) {
		this.paydate = paydate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
