package com.example.toko.model;

import java.sql.Date;

public class TransactionReport {
  private String productName;
  private String productStock;
  private int amount;
  private int productId;
  private Date date;

  public TransactionReport(String productName, String productStock, int amount, int productId, Date date) {
    this.productName = productName;
    this.productStock = productStock;
    this.amount = amount;
    this.productId = productId;
    this.date = date;
  }

  public String getProductName() {
    return productName;
  }

  public String getProductStock() {
    return productStock;
  }

  public int getAmount() {
    return amount;
  }

  public int getProductId() {
    return productId;
  }

  public Date getDate() {
    return date;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public void setProductStock(String productStock) {
    this.productStock = productStock;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}