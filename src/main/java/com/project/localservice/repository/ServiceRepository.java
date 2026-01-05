package com.project.localservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.localservice.model.Service;

public interface ServiceRepository extends JpaRepository<Service,Long> {

}
