package com.schuster.monthlyBills.jersey;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MBillsEntry {
	
	public int categoryId;
	public String item;
	public int key;
	public boolean firsthalf;
	public boolean intotal;
	public String lastMonth;
	public String payDate;
	public String payAmount;
	public int position = 0;
	
	
	
	public int getCategoryId() {
		return categoryId;
	}
	public MBillsEntry setCategoryId(int categoryId) {
		this.categoryId = categoryId;
		return this;
	}
	
	public String getItem() {
		return item;
	}
	public MBillsEntry setItem(String item) {
		this.item = item;
		return this;
	}
	public int getKey() {
		return key;
	}
	public MBillsEntry setKey(int key) {
		this.key = key;
		return this;
	}
	public boolean getFirsthalf() {
		return firsthalf;
	}
	public MBillsEntry setFirsthalf(boolean firsthalf) {
		this.firsthalf = firsthalf;
		return this;
	}
	public boolean getIntotal() {
		return intotal;
	}
	public MBillsEntry setIntotal(boolean intotal) {
		this.intotal = intotal;
		return this;
	}
	public String getLastMonth() {
		return lastMonth;
	}
	public MBillsEntry setLastMonth(String lastMonth) {
		this.lastMonth = lastMonth;
		return this;
	}
	public String getPayDate() {
		return payDate;
	}
	public MBillsEntry setPayDate(String payDate) {
		this.payDate = payDate;
		return this;
	}
	public MBillsEntry setPayDate(LocalDate payDate) {
		this.payDate = payDate.toString();
		return this;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public MBillsEntry setPayAmount(String payAmount) {
		this.payAmount = payAmount;
		return this;
	}
	public MBillsEntry setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount.toString();
		return this;
	}
	public int getPosition() {
		return position;
	}
	public MBillsEntry setPosition(int position) {
		this.position = position;
		return this;
	}
	
	public int compareTo(MBillsEntry mbe) {
		return (position - mbe.getPosition());
	}
	
	
	
}
