package com.rac.ktm.controller;

import com.rac.ktm.dto.requestDto.AuthRequest;
import com.rac.ktm.dto.requestDto.LoginRequestDto;
import com.rac.ktm.dto.requestDto.ProfileRequestDto;
import com.rac.ktm.dto.requestDto.ProfileResponseDto;
import com.rac.ktm.dto.requestDto.UserDto;
import com.rac.ktm.entity.Post;
import com.rac.ktm.service.JwtService;
import com.rac.ktm.service.PostService;
import com.rac.ktm.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rac")
public class UserController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PostService postService;

    private boolean handleToken(Model model, String token) {
        boolean isLoggedIn = false;
        String username = null;
        String role = null;
        if (token != null) {
            try {
                username = jwtService.extractUsername(token);
                role = userService.extractRole(username);
                isLoggedIn = true;
            } catch (Exception e) {
                model.addAttribute("loginError", "Invalid token. Please log in again.");
                return false;
            }
        }
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("loginError", null);
        return isLoggedIn;
    }

    @GetMapping("/homePage")
    public String showHomePage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        handleToken(model, token);
        return "homePage";
    }

    @GetMapping("/events")
    public String showEvents(Model model, @CookieValue(value = "jwt", required = false) String token) {
        handleToken(model, token);
        return "events";
    }

    @GetMapping("/news")
    public String showNewsPage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        handleToken(model, token);
        return "news";
    }

    @GetMapping("/podcast")
    public String showPodcastPage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        handleToken(model, token);
        return "podcast";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        handleToken(model, token);
        return "about";
    }

    @GetMapping("/admin")
    public String showAdminPage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        handleToken(model, token);
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "admin";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "registrationPage";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") @Valid UserDto userDto, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "registrationPage";
            }
            userService.registerUser(userDto);
            boolean isLoggedIn = false;
            model.addAttribute("isLoggedIn", isLoggedIn);
            model.addAttribute("loginError", null);
            return "homePage";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "errorPage";
        }
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("loginRequestDto") @Valid LoginRequestDto loginRequestDto, BindingResult bindingResult, Model model, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginError", "⚠ Error in form submission ⚠");
            return "homePage";
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("loginError", "⚠ Invalid Username or Password ⚠");
            return "homePage";
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails.getUsername());

        Cookie cookie = new Cookie("jwt", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Use true if HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // Set cookie expiration time

        response.addCookie(cookie);

        return "redirect:/rac/homePage";
    }

    @PostMapping("/logout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Use true if HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Invalidate the cookie

        response.addCookie(cookie);

        return "redirect:/rac/homePage";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        String username = jwtService.extractUsername(token);
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setUserName(username);
        ProfileResponseDto profileResponseDto = userService.profileRequest(profileRequestDto);
        model.addAttribute("profileResponseDto", profileResponseDto);
        return "profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute("profileResponseDto") ProfileResponseDto profileResponseDto,
                                @RequestParam("currentPassword") String currentPassword,
                                @CookieValue(value = "jwt", required = false) String token, Model model) {
        String username = jwtService.extractUsername(token);
        try {
            if (!userService.verifyPassword(username, currentPassword)) {
                model.addAttribute("passwordError", "⚠ Incorrect current password ⚠");
                return "profile";
            }
            userService.updateProfile(profileResponseDto);
            return "redirect:/rac/homePage";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "⚠ An unexpected error occurred. Please try again. ⚠");
            return "profile";
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.getUsername());
                return ResponseEntity.ok(token); // Return the token with a 200 OK status
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/secureEndpoint")
    public ResponseEntity<String> getSecureEndpoint(@CookieValue(value = "jwt", required = false) String token) {
        if (token != null && jwtService.validateToken(token)) {
            return ResponseEntity.ok("You have accessed a secure endpoint");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
