package microservices.composite.product.services;

import api.composite.product.ProductAggregate;
import api.composite.product.ProductCompositeService;
import api.core.product.Product;
import api.core.recommendation.Recommendation;
import api.core.review.Review;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import util.http.ServiceUtil;

@RestController
@RequiredArgsConstructor
public class ProductCompositeServiceImpl implements ProductCompositeService {

  private final ServiceUtil serviceUtil;
  private final ProductCompositeIntegration integration;

  @Override
  public ProductAggregate getProduct(int productId) {
    Product product = integration.getProduct(productId);
    List<Recommendation> recommendations = integration.getRecommendations(productId);
    List<Review> reviews = integration.getReviews(productId);

    return createProductAggregate(
        product, recommendations, reviews, serviceUtil.getServiceAddress());
    )
  }
}
