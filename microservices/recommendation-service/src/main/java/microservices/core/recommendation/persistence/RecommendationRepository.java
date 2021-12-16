package microservices.core.recommendation.persistence;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RecommendationRepository extends
    ReactiveCrudRepository<RecommendationEntity, String> {

  List<RecommendationEntity> findByProductId(int productId);
}
