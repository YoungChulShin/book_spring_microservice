package api.core.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface ProductService {

  @GetMapping("/product/{productId}")
  Mono<Product> getProduct(@PathVariable int productId);

  Product createProduct(@RequestBody Product body);

  void deleteProduct(@PathVariable int productId);
}
