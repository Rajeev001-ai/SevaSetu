package com.project.localservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.localservice.helper.BookingStatus;
import com.project.localservice.model.Booking;
import com.project.localservice.model.User;
import com.project.localservice.repository.BookingRepository;
import com.project.localservice.repository.ServiceRepository;
import com.project.localservice.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/helper")
@PreAuthorize("hasRole('HELPER')")
public class HelperController {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {

        User helper = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("helper", helper);
        List<Booking> bookings = bookingRepo.findByHelper(helper);

        model.addAttribute("totalBookings", bookings.size());

        model.addAttribute("completedJobs", bookingRepo.findByHelperAndStatus(helper, BookingStatus.COMPLETED).size());
        model.addAttribute("pendingJobs", bookingRepo.findByHelperAndStatus(helper, BookingStatus.ASSIGNED).size());
        return "helper/dashboard";
    }

    @GetMapping("/profile")
    public String helperProfile(Authentication auth, Model model) {
        String email = auth.getName();
        User helper = userRepo.findByEmail(email).orElseThrow();
        model.addAttribute("helper", helper);
        model.addAttribute("services", serviceRepo.findAll());
        return "helper/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication auth,
            @RequestParam String name,
            @RequestParam String city,
            @RequestParam String service,
            @RequestParam String phone) {

        User helper = userRepo.findByEmail(auth.getName()).orElseThrow();

        helper.setCity(city);
        helper.setName(name);
        helper.setService(service);
        helper.setPhone(phone);

        userRepo.save(helper);

        return "redirect:/helper/profile";
    }

    @GetMapping("/bookings")
    public String getBookings(Authentication auth, Model model) {

        User helper = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("helper", helper);
        List<Booking> booking = bookingRepo.findByHelper(helper);
        model.addAttribute("bookings", booking);
        return "helper/bookings";
    }

    @PostMapping("/booking/accept/{id}")
    public String acceptBooking(@PathVariable Long id) {

        Booking booking = bookingRepo.findById(id).orElseThrow();
        booking.setStatus(BookingStatus.ACCEPTED);

        bookingRepo.save(booking);
        return "redirect:/helper/bookings";
    }

    @PostMapping("/booking/reject/{id}")
    public String rejectBooking(@PathVariable Long id) {

        Booking booking = bookingRepo.findById(id).orElseThrow();
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepo.save(booking);
        return "redirect:/helper/bookings";
    }

    @PostMapping("/booking/complete/{id}")
    public String completeBooking(@PathVariable Long id) {

        Booking booking = bookingRepo.findById(id).orElseThrow();
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepo.save(booking);
        return "redirect:/helper/bookings";
    }

    @GetMapping("/earnings")
    public String helperEarnings(Model model,Authentication auth) {

        User helper = userRepo.findByEmail(auth.getName()).orElseThrow();

        List<Booking> completedBookings = bookingRepo.findByHelperAndStatus(helper,BookingStatus.COMPLETED);

        double totalEarnings = helper.getPrice()*completedBookings.size();

        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("totalJobs", completedBookings.size());
        model.addAttribute("bookings", completedBookings);
        model.addAttribute("helper", helper);

        return "helper/earning";
    }

}
