package com.spring.cognito.springcognitodemoapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Index Page");
        return "index";
    }

    @GetMapping("/admin/greetMe")
    public String adminGreet(Model model){
        String response = "Welcome Admin! You developed an amazing Website! :";
        model.addAttribute("response", response);
        return "greeting";
    }

    @GetMapping("/user/greetMe")
    public String userGreet(Model model){
        String response = "Welcome User! Nice to Meet You! :";
        model.addAttribute("response", response);
        return "greeting";
    }
}
