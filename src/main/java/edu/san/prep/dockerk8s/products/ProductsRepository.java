package edu.san.prep.dockerk8s.products;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
//the default CrudRepository returns Iterable for findAll()
public interface ProductsRepository extends ListCrudRepository<Product, Integer> {

}
