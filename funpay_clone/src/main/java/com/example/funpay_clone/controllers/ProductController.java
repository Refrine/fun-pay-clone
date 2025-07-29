package com.example.funpay_clone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.funpay_clone.repository.ProductRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data   
@Controller
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductRepository productRepo;


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productRepo.findAll());
        

        return "index";
    }
    




}
