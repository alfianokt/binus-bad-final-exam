package com.example.toko.model;

public class Product {
  private int id;
  private String name;
  private String category;
  private String description;
  private int stock;
  private int price;

  public Product(int id, String name, String category, String description, int stock, int price) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.description = description;
    this.stock = stock;
    this.price = price;

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }
}