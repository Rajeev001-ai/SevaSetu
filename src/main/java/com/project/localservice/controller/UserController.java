package com.project.localservice.controller;

import java.time.LocalDateTime;

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
import com.project.localservice.model.Booking;
import com.project.localservice.model.Service;
import com.project.localservice.model.User;
import com.project.localservice.repository.BookingRepository;
import com.project.localservice.repository.ServiceRepository;
import com.project.localservice.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {

        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("services", serviceRepo.findAll());
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String getMethodName(Model model, Authentication auth) {

        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("bookings", bookingRepo.findByUser(user));
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String postMethodName(Authentication auth, @RequestParam String name,
            @RequestParam String city,
        @RequestParam String phone) {

        User user = userRepo.findByEmail(auth.getName()).orElseThrow(null);

        if (user == null) {
            return "redirect:/login?error=user-not-found";
        }
        user.setCity(city);
        user.setName(name);
        user.setPhone(phone);
        userRepo.save(user);

        return "redirect:/user/profile";
    }

    // Booking form page
    @GetMapping("/book-service/{id}")
    public String showBookingForm(@PathVariable Long id, Model model) {

        Service service=serviceRepo.findById(id).orElseThrow();

        model.addAttribute("category", service.getName());
        return "user/booking-form";
    }

    @GetMapping("/book-service")
    public String bookService(Model model,@RequestParam String service){

        model.addAttribute("category", service);
        return "user/booking-form";
    }

    // Booking submission
    @PostMapping("/book-service/submit")
    public String submitBooking(
            @RequestParam String service,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam LocalDateTime dateTime,
            Authentication auth,
            Model model) {
        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        if (user == null)
            return "redirect:/login";

        Booking booking = new Booking();
        booking.setServiceName(service);
        booking.setAddress(address);
        booking.setCity(city);
        booking.setUser(user);
        booking.setDateTime(dateTime);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepo.save(booking);

        model.addAttribute("message", "Your booking is confirmed!");
        return "user/booking-confirmation";
    }

    // List user's bookings
    @GetMapping("/bookings")
    public String listBookings(Authentication auth, Model model) {
        User user = userRepo.findByEmail(auth.getName()).orElse(null);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("bookings", bookingRepo.findByUser(user));

        return "user/bookings";
    }

    @PostMapping("/delete-booking/{id}")
    public String deleteBooking(@PathVariable Long id) {
        
        bookingRepo.deleteById(id);
        return "redirect:/user/bookings";
    }
    

}
