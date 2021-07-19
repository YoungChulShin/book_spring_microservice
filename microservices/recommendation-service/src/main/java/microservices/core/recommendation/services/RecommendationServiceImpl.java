package microservices.core.recommendation.services;

import api.core.recommendation.Recommendation;
import api.core.recommendation.RecommendationService;
import com.mongodb.DuplicateKeyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import microservices.core.recommendation.persistence.RecommendationEntity;
import microservices.core.recommendation.persistence.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;
import util.http.ServiceUtil;

@RestController
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

  private final RecommendationRepository recommendationRepository;

  private final RecommendationMapper recommendationMapper;

  private final ServiceUtil serviceUtil;


  @Override
  public Recommendation createRecommendation(Recommendation body) {
    try {
      RecommendationEntity entity = recommendationMapper.apiToEntity(body);
      RecommendationEntity newEntity = recommendationRepository.save(entity);

      return recommendationMapper.entityToApi(newEntity);
    } catch(DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
    }
  }

  @Override
  public List<Recommendation> getRecommendations(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    List<RecommendationEntity> entityList = recommendationRepository.findByProductId(productId);
    List<Recommendation> recommendationList = recommendationMapper.entityListToApiList(entityList);
    recommendationList.forEach(r -> r.updateServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("/recommendation response size: {}", recommendationList.size());

    return recommendationList;
  }

  @Override
  public void deleteRecommendations(int productId) {
    recommendationRepository.deleteAll(recommendationRepository.findByProductId(productId));
  }
}
