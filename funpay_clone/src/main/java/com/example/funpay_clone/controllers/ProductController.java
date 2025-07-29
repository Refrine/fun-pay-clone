package com.example.funpay_clone.controllers;

import com.example.funpay_clone.models.Product;
import com.example.funpay_clone.models.User;
import com.example.funpay_clone.repository.ProductRepository;
import com.example.funpay_clone.services.ProductService;
import com.example.funpay_clone.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "create-product";
    }

    @PostMapping("/products")
    public String createProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Товар успешно создан!");
        return "redirect:/";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new IllegalArgumentException("Неверный ID товара: " + id));
        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product, 
                              RedirectAttributes redirectAttributes) {
        product.setId(id);
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Товар успешно обновлен!");
        return "redirect:/";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Товар успешно удален!");
        return "redirect:/";
    }

    @GetMapping("/products/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));
        model.addAttribute("product", product);
        return "product-details"; 
    }

    @GetMapping("/auth/login")
    public String showLoginForm(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "logout", required = false) String logout,
        Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Неверные логин или пароль");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Вы успешно вышли из системы");
        }
        
        model.addAttribute("user", new User());
        return "auth/login"; 
    }

    @GetMapping("/auth/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register"; 
    }

   
}