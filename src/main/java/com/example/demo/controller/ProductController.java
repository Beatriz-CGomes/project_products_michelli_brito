package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
		// BeanUtils.copyProperties aqui está sendo convertido o DTO para model
		BeanUtils.copyProperties(productDTO, productModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAll() {
		List<ProductModel> productList = productRepository.findAll();
		if (!productList.isEmpty()) {
			for (ProductModel product : productList) {
				UUID id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getById(id)).withRel("Products List"));
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(productList);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getById(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> product = productRepository.findById(id);
		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		} else {
			product.get().add(linkTo(methodOn(ProductController.class).getAll()).withRel("Products List"));
			return ResponseEntity.status(HttpStatus.OK).body(product.get());
		}
	}

	@PutMapping("/products/{id}")
	public ResponseEntity<Object> put(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductDTO dto) {
		Optional<ProductModel> product = productRepository.findById(id);
		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} else {
			var productModel = product.get();
			BeanUtils.copyProperties(dto, productModel);
			return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
		}
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> product = productRepository.findById(id);
		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		} else {
			productRepository.delete(product.get());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(product);
		}

	}
}
