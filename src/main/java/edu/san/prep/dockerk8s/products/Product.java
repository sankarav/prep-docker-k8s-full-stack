package edu.san.prep.dockerk8s.products;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

//@Table(name = "product")
public record Product(@Id Integer id, String name, String description, Double price)

{

}
