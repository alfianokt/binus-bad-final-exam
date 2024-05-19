package com.example.toko.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.toko.model.Product;
import com.example.toko.model.Transaction;
import com.example.toko.model.TransactionReport;

public class Database {
  private Connection connection;

  public Database(Connection _connection) {
    connection = _connection;
  }

  public List<Product> getAllProducts() {
    List<Product> dataList = new ArrayList<>();
    String sql = "SELECT * from products";

    try (Statement statement = connection.createStatement()) {
      ResultSet resultSet = statement.executeQuery(sql);

      while (resultSet.next()) {
        Product data = new Product(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("category"),
            resultSet.getString("description"),
            resultSet.getInt("stock"),
            resultSet.getInt("price"));

        dataList.add(data);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return dataList;
  }

  public void createProduct(Product data) {
    String sql = "INSERT INTO products (name, category, description, stock, price) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, data.getName());
      statement.setString(2, data.getName());
      statement.setString(3, data.getName());
      statement.setInt(4, data.getStock());
      statement.setInt(5, data.getPrice());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateProduct(Product data) {
    String sql = "UPDATE products SET name = ?, category = ?, description = ?, stock = ?, price = ? WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, data.getName());
      statement.setString(2, data.getName());
      statement.setString(3, data.getName());
      statement.setInt(4, data.getStock());
      statement.setInt(5, data.getPrice());
      statement.setInt(6, data.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Product findProductByNameOrCategory(String name, String category) {
    Product product = null;
    String sql = "SELECT * from products where name = ? OR category = ? LIMIT 1";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, name);
      statement.setString(2, category);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          product = new Product(
              resultSet.getInt("id"),
              resultSet.getString("name"),
              resultSet.getString("category"),
              resultSet.getString("description"),
              resultSet.getInt("stock"),
              resultSet.getInt("price"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return product;
  }

  public void createTransaction(Transaction data) {
    String sql = "INSERT INTO transactions (amount, total_item, product_id, date) VALUES (?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      Date now = Date.valueOf(LocalDate.now());

      statement.setInt(1, data.getAmount());
      statement.setInt(2, data.getTotalItem());
      statement.setInt(3, data.getProductId());
      statement.setDate(4, now);

      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<TransactionReport> getTransactionReports(LocalDate startDate, LocalDate endDate) {
    List<TransactionReport> reports = new ArrayList<>();

    String sql = "SELECT " +
        "    p.name as product_name, " +
        "    p.stock as product_stock, " +
        "    t.product_id, " +
        "    SUM(t.total_item * t.amount) as amount, " +
        "    t.date " +
        "FROM transactions t " +
        "JOIN products p ON t.product_id = p.id " +
        "WHERE t.date BETWEEN ? AND ? " +
        "GROUP BY " +
        "    p.name, " +
        "    t.product_id, " +
        "    t.date " +
        "ORDER BY " +
        "    t.date DESC, product_name;";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setDate(1, java.sql.Date.valueOf(startDate));
      statement.setDate(2, java.sql.Date.valueOf(endDate));

      try (ResultSet rs = statement.executeQuery()) {
        while (rs.next()) {
          TransactionReport report = new TransactionReport(
              rs.getString("product_name"),
              rs.getString("product_stock"),
              rs.getInt("amount"),
              rs.getInt("product_id"),
              rs.getDate("date"));
          reports.add(report);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return reports;
  }
}
