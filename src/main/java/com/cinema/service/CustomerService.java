package com.cinema.service;

import com.cinema.exception.BookingException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Customer;
import com.cinema.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
    }

    public Customer registerCustomer(Customer customer) {
        customerRepository.findByEmail(customer.getEmail()).ifPresent(c -> {
            throw new BookingException("A customer with email '" + customer.getEmail() + "' already exists.");
        });
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        getCustomerById(id); // ensures exists
        // Check email uniqueness if changed
        customerRepository.findByEmail(customer.getEmail()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new BookingException("Email '" + customer.getEmail() + "' is already taken.");
            }
        });
        customer.setId(id);
        return customerRepository.update(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
