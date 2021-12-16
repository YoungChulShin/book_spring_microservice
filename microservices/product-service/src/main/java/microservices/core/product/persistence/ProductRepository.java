package microservices.core.product.persistence;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, String> {

  Optional<ProductEntity> findByProductId(int productId);
}
