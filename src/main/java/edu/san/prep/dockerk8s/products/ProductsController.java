package edu.san.prep.dockerk8s.products;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

  private final ProductsService service;

  ProductsController(ProductsService service) {
    this.service = service;
  }

  @GetMapping
  public List<Product> getProducts() {
    return this.service.getProducts();
  }

  @GetMapping("/{id}")
  public Product getProduct(@PathVariable Integer id) {
    return this.service.getProduct(id);
  }
}
