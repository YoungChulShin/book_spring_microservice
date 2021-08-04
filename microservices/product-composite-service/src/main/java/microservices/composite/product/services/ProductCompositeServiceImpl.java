package microservices.composite.product.services;

import api.composite.product.ProductAggregate;
import api.composite.product.ProductCompositeService;
import api.composite.product.RecommendationSummary;
import api.composite.product.ReviewSummary;
import api.composite.product.ServiceAddresses;
import api.core.product.Product;
import api.core.recommendation.Recommendation;
import api.core.review.Review;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import util.http.ServiceUtil;

@RestController
@RequiredArgsConstructor
public class ProductCompositeServiceImpl implements ProductCompositeService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

  private final ServiceUtil serviceUtil;
  private final ProductCompositeIntegration integration;

  @Override
  public void createCompositeProduct(ProductAggregate body) {
    LOG.warn("Create composite start");
    try {

      LOG.warn("Create product start");
      Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
      integration.createProduct(product);

      LOG.warn("Create recommendations start");
      if (body.getRecommendations() != null) {
        body.getRecommendations().forEach(r -> {
          Recommendation recommendation = new Recommendation(
              body.getProductId(),
              r.getRecommendationId(),
              r.getAuthor(),
              r.getRate(),
              r.getContent(),
              null);
          integration.createRecommendation(recommendation);
        });
      }

      LOG.warn("review item size: {}", body.getReviews().size());
      if (body.getReviews() != null) {
        body.getReviews().forEach(r -> {
          LOG.warn("review item={}, {}, {}, {}",
              r.getReviewId(),
              r.getAuthor(),
              r.getContent(),
              r.getSubject());

          Review review = new Review(
              body.getProductId(),
              r.getReviewId(),
              r.getAuthor(),
              r.getSubject(),
              r.getContent(),
              null);
          integration.createReview(review);
        });
      }
    } catch (RuntimeException re) {
      LOG.warn("createCompositeProduct failed", re);
    }
  }

  @Override
  public Mono<ProductAggregate> getCompositeProduct(int productId) {
    return Mono.zip(
        values -> createProductAggregate(
            (Product)values[0],
            (List<Recommendation>)values[1],
            (List<Review>)values[2],
            serviceUtil.getServiceAddress()),
        integration.getProduct(productId),
        integration.getRecommendations(productId).collectList(),
        integration.getReviews(productId).collectList()
    );

//    Product product = integration.getProduct(productId);
//    List<Recommendation> recommendations = integration.getRecommendations(productId);
//    List<Review> reviews = integration.getReviews(productId);
//
//    return createProductAggregate(
//        product, recommendations, reviews, serviceUtil.getServiceAddress());
  }

  @Override
  public void deleteCompositeProduct(int productId) {
    integration.deleteProduct(productId);
    integration.deleteRecommendations(productId);
    integration.deleteReviews(productId);
  }

  private ProductAggregate createProductAggregate(
      Product product,
      List<Recommendation> recommendations,
      List<Review> reviews,
      String serviceAddress) {
    int productId = product.getProductId();
    String name = product.getName();
    int weight = product.getWeight();

    List<RecommendationSummary> recommendationSummaries = (recommendations == null)
        ? null
        : recommendations.stream()
          .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
          .collect(Collectors.toList());

    List<ReviewSummary> reviewSummaries = (reviews == null)
        ? null
        : reviews.stream()
          .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
          .collect(Collectors.toList());

    String productAddress = product.getServiceAddress();
    String reviewAddress = (reviews != null && reviews.size() > 0)
        ? reviews.get(0).getServiceAddress()
        : "";
    String recommendationAddress = (recommendations != null && recommendations.size() > 0)
        ? recommendations.get(0).getServiceAddress()
        : "";
    ServiceAddresses serviceAddresses = new ServiceAddresses(
        serviceAddress,
        productAddress,
        reviewAddress,
        recommendationAddress);

    return new ProductAggregate(
        productId,
        name,
        weight,
        recommendationSummaries,
        reviewSummaries,
        serviceAddresses);
  }
}
