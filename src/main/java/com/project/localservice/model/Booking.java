package com.project.localservice.model;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.localservice.helper.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String city;
    private String address;

    @ManyToOne
    private User user;

    @ManyToOne
    private User helper;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateTime;

    private double price=500;


    // getters setters
}
