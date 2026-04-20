package com.cinema.repository;

import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> Customer.builder()
            .id(rs.getLong("id"))
            .firstName(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .email(rs.getString("email"))
            .phone(rs.getString("phone"))
            .registeredAt(rs.getTimestamp("registered_at").toLocalDateTime())
            .build();

    public List<Customer> findAll() {
        return jdbcTemplate.query("SELECT * FROM customers ORDER BY last_name, first_name", customerRowMapper);
    }

    public Optional<Customer> findById(Long id) {
        List<Customer> result = jdbcTemplate.query("SELECT * FROM customers WHERE id = ?", customerRowMapper, id);
        return result.stream().findFirst();
    }

    public Optional<Customer> findByEmail(String email) {
        List<Customer> result = jdbcTemplate.query(
                "SELECT * FROM customers WHERE LOWER(email) = LOWER(?)", customerRowMapper, email);
        return result.stream().findFirst();
    }

    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, registered_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        customer.setId(keyHolder.getKey().longValue());
        return customer;
    }

    public Customer update(Customer customer) {
        String sql = "UPDATE customers SET first_name=?, last_name=?, email=?, phone=? WHERE id=?";
        int rows = jdbcTemplate.update(sql,
                customer.getFirstName(), customer.getLastName(),
                customer.getEmail(), customer.getPhone(), customer.getId());
        if (rows == 0) throw new ResourceNotFoundException("Customer not found with id: " + customer.getId());
        return customer;
    }

    public void deleteById(Long id) {
        int rows = jdbcTemplate.update("DELETE FROM customers WHERE id = ?", id);
        if (rows == 0) throw new ResourceNotFoundException("Customer not found with id: " + id);
    }
}
