package com.management.usermanagement.controller;

import com.management.usermanagement.model.Customer;
import com.management.usermanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * POST - Add a new customer
     */
    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerRepository.save(customer);
            return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving customer: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET - Retrieve a customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * GET - Retrieve all customers
     */
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
    
    /**
     * PUT - Update an existing customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @RequestBody Customer customerDetails) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            Customer existingCustomer = customer.get();
            existingCustomer.setFullName(customerDetails.getFullName());
            existingCustomer.setZone(customerDetails.getZone());
            existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
            existingCustomer.setWhatsappNumber(customerDetails.getWhatsappNumber());
            existingCustomer.setEmailAddress(customerDetails.getEmailAddress());
            existingCustomer.setFullAddress(customerDetails.getFullAddress());
            existingCustomer.setStatus(customerDetails.getStatus());
            existingCustomer.setTags(customerDetails.getTags());
            existingCustomer.setNotes(customerDetails.getNotes());
            
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * DELETE - Delete a customer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            customerRepository.deleteById(id);
            return new ResponseEntity<>("Customer deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
    }
}
