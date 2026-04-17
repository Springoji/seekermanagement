package com.management.usermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    private String id;
    
    private String fullName;
    private String zone;
    private String phoneNumber;
    private String whatsappNumber;
    private String emailAddress;
    private String fullAddress;
    private String status;
    private String tags;
    private String notes;
}
