package edu.san.prep.dockerk8s.products;

import edu.san.prep.dockerk8s.exceptions.NotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductsService {

  private final ProductsRepository repository;

  ProductsService(ProductsRepository repository) {
    this.repository = repository;
  }

  public List<Product> getProducts() {
    return repository.findAll();
    /*
    initial stub that evolved
    return List.of(
        new Product(1, "colgate-toothpaste", "brush", 4.02),
        new Product(2, "Kirkland coconut water", "hydrate", 30.15)
    );
    */
  }

  public Product getProduct(Integer id) {
    return this.repository.findById(id)
        .orElseThrow(() -> {
          log.error("Product of id {} not found", id);
          return new NotFoundException("Product of id [%d] not found".formatted(id));
        });
  }
}
