package com.project.localservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.localservice.helper.BookingStatus;
import com.project.localservice.model.Booking;
import com.project.localservice.model.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByHelper(User helper);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking>findByHelperAndStatus(User helper,BookingStatus status);

}

