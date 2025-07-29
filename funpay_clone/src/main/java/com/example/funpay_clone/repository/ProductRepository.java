package com.example.funpay_clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.funpay_clone.models.Product;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

        List<Product> findByName(String name);


    
}
