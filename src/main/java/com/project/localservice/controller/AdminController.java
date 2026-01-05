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
import org.springframework.web.bind.annotation.RequestParam;

import com.project.localservice.helper.BookingStatus;
import com.project.localservice.helper.Role;
import com.project.localservice.model.Booking;
import com.project.localservice.model.Service;
import com.project.localservice.model.User;
import com.project.localservice.repository.BookingRepository;
import com.project.localservice.repository.ServiceRepository;
import com.project.localservice.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("totalUsers", userRepo.findByRole(Role.USER).size());
        model.addAttribute("totalHelpers", userRepo.findByRole(Role.HELPER).size());
        model.addAttribute("totalBookings", bookingRepo.findAll().size());
        return "admin/dashboard";
    }

    // handle bookings
    @GetMapping("/bookings-status")
    public String getBookingStatus(Model model) {

        model.addAttribute("bookings", bookingRepo.findAll());
        return "admin/bookings-status";
    }

    @PostMapping("/bookings/reject/{id}")
    public String rejectBooking(@PathVariable Long id) {

        Booking booking = bookingRepo.findById(id).orElseThrow();

        booking.setStatus(BookingStatus.REJECTED);

        bookingRepo.save(booking);

        return "redirect:/admin/bookings-status";
    }

    @PostMapping("/bookings/delete/{id}")
    public String deleteBooking(@PathVariable Long id) {

        Booking booking = bookingRepo.findById(id).orElseThrow();

        bookingRepo.delete(booking);

        return "redirect:/admin/bookings-status";
    }

    // Assign bookings
    @GetMapping("/bookings-assign")
    public String bookings(Model model) {
        model.addAttribute("bookings", bookingRepo.findByStatus(BookingStatus.PENDING));
        model.addAttribute("helpers", userRepo.findByRole(Role.HELPER));
        return "admin/bookings-assign";
    }

    @PostMapping("/bookings-assign/{id}")
    public String assignBooking(@PathVariable Long id,
            @RequestParam Long helperId) {

        Booking booking = bookingRepo.findById(id).orElseThrow();
        User helper = userRepo.findById(helperId).orElseThrow();

        booking.setHelper(helper);
        booking.setStatus(BookingStatus.ASSIGNED);

        bookingRepo.save(booking);
        return "redirect:/admin/bookings-assign";
    }

    // helper handler
    @GetMapping("/helpers")
    public String helpers(Model model) {
        List<User> helpers = userRepo.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.HELPER)
                .toList();

        model.addAttribute("helpers", helpers);
        return "admin/helpers";
    }

    @PostMapping("/helper/approve/{id}")
    public String approve(@PathVariable Long id) {
        User helper = userRepo.findById(id).orElseThrow();
        helper.setEnabled(true);
        userRepo.save(helper);
        return "redirect:/admin/helpers";
    }

    @PostMapping("/helper/block/{id}")
    public String reject(@PathVariable Long id) {
        User helper = userRepo.findById(id).orElseThrow();
        helper.setEnabled(false);
        userRepo.save(helper);
        return "redirect:/admin/helpers";
    }

    // handler users
    @GetMapping("/users")
    public String getUsers(Model model) {

        List<User> users = userRepo.findByRole(Role.USER);
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/user/approve/{id}")
    public String getapprove(@PathVariable Long id) {
        User helper = userRepo.findById(id).orElseThrow();
        helper.setEnabled(true);
        userRepo.save(helper);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/block/{id}")
    public String getreject(@PathVariable Long id) {
        User helper = userRepo.findById(id).orElseThrow();
        helper.setEnabled(false);
        userRepo.save(helper);
        return "redirect:/admin/users";
    }

    @GetMapping("/earnings")
    public String getEarning(Model model) {

        List<Booking> completedBookings = bookingRepo.findByStatus(BookingStatus.COMPLETED);

        model.addAttribute("totalRevenue", completedBookings.size()*500);
        model.addAttribute("completedBookings", completedBookings);

        return "admin/earnings";
    }

    //handle services
    @GetMapping("/services")
    public String getServices(Model model) {
        
        List<Service>services=serviceRepo.findAll();
        
        model.addAttribute("services", services);

        return "admin/services";
    }

    @PostMapping("/services/add")
    public String addService( 
        @RequestParam String name,
        @RequestParam String description,
        @RequestParam double price) {
        
        Service service=new Service();

        service.setPrice(price);
        service.setDescription(description);
        service.setName(name);

        serviceRepo.save(service);
        
        return "redirect:/admin/services";
    }

    @PostMapping("/delete-service/{id}")
    public String postMethodName(@PathVariable Long id) {
        
        serviceRepo.deleteById(id);
        
        return "redirect:/admin/services";
    }
    
    
    

}
