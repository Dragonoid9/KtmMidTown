package com.rac.ktm.controller;


import com.rac.ktm.entity.Podcast;
import com.rac.ktm.service.JwtService;
import com.rac.ktm.service.PodcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/rac/podcast")
public class PodcastController {

    @Autowired
    private PodcastService podcastService;
    private final String UPLOAD_DIR = "src/main/resources/static/images/podcast/";

    @Autowired
    private JwtService jwtService;

    private boolean handleToken(Model model, String token) {
        boolean isLoggedIn = false;
        String username = null;
        if (token != null) {
            try {
                username = jwtService.extractUsername(token);
                isLoggedIn = true;
            } catch (Exception e) {
                model.addAttribute("loginError", "Invalid token. Please log in again.");
                return false;
            }
        }
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("username", username);
        model.addAttribute("loginError", null);
        return isLoggedIn;
    }

    @GetMapping("/listpodcast")
    public String adminPage(Model model, @CookieValue(value = "jwt", required = false) String token) {
        List<Podcast> podcast = podcastService.findAll();
        handleToken(model, token);
        model.addAttribute("podcast", podcast);
        return "managePodcast/podcastadmin";
    }

    @GetMapping("/create")
    public String createPodcastForm(Model model, @CookieValue(value = "jwt", required = false) String token) {
        model.addAttribute("podcast", new Podcast());
        handleToken(model, token);
        return "managePodcast/podcastForm";
    }

    @PostMapping("/create")
    public String createPodcast(@ModelAttribute Podcast podcast, @RequestParam("imageFile") MultipartFile file, @CookieValue(value = "jwt", required = false) String token) throws IOException {
        String username = jwtService.extractUsername(token);
        if (!file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR, fileName);
                Files.createDirectories(path.getParent()); // Ensure the directory exists
                Files.write(path, file.getBytes()); // Write the file to the specified path
                podcast.setImageUrl("/images/podcast/" + fileName); // Set the URL for the image
            } catch (IOException e) {
                e.printStackTrace();
                // Handle file upload error
            }
        }

        podcastService.save(podcast, username);
        return "redirect:/rac/podcast/listpodcast";
    }

    @GetMapping("/edit/{id}")
    public String editPodcastForm(@PathVariable Long id, Model model, @CookieValue(value = "jwt", required = false) String token) {
        Podcast podcast = podcastService.findById(id);
        handleToken(model, token);
        model.addAttribute("podcast", podcast);
        return "managePodcast/podcastForm";
    }

    @PostMapping("/edit")
    public String editPodcast(@ModelAttribute Podcast podcast, @RequestParam("imageFile") MultipartFile file, @CookieValue(value = "jwt", required = false) String token) {
        String username = jwtService.extractUsername(token);

        if (!file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR, fileName);
                Files.createDirectories(path.getParent()); // Ensure the directory exists
                Files.write(path, file.getBytes()); // Write the file to the specified path
                podcast.setImageUrl("/images/podcast/" + fileName); // Set the URL for the image
            } catch (IOException e) {
                e.printStackTrace();
                // Handle file upload error
            }
        }

        podcastService.save(podcast, username);
        return "redirect:/rac/podcast/listpodcast";
    }

    @GetMapping("/delete/{id}")
    public String deletePodcast(@PathVariable Long id) {
        podcastService.deleteById(id);
        return "redirect:/rac/podcast/listpodcast";
    }
}
