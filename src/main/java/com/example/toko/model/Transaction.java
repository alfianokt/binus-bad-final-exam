package com.example.toko.model;

import java.sql.Date;

public class Transaction {
  private int id;
  private int amount;
  private int totalItem;
  private int productId;
  private Date date;

  public Transaction(int id, int amount, int totalItem, int productId, Date date) {
    this.id = id;
    this.amount = amount;
    this.totalItem = totalItem;
    this.productId = productId;
    this.date = date;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public int getTotalItem() {
    return totalItem;
  }

  public void setTotalItem(int totalItem) {
    this.totalItem = totalItem;
  }

  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}