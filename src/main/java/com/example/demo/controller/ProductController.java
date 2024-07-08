package com.example.demo.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.ProductDTO;
import com.example.demo.model.ProductModel;
import com.example.demo.repository.ProductRepository;

import jakarta.validation.Valid;

@RestController
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@PostMapping("/products")
	public ResponseEntity<ProductModel> post(@RequestBody @Valid ProductDTO productDTO) {
		var productModel = new ProductModel();
		//BeanUtils.copyProperties aqui está sendo convertido o DTO para model
		BeanUtils.copyProperties(productDTO, productModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}

}
