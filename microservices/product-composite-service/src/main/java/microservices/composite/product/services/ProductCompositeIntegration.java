package microservices.composite.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
import api.core.recommendation.Recommendation;
import api.core.recommendation.RecommendationService;
import api.core.review.Review;
import api.core.review.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.http.HttpErrorInfo;

@Component
public class ProductCompositeIntegration implements
    ProductService,
    RecommendationService,
    ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;

  private final String productServiceUrl;
  private final String recommendationServiceUrl;
  private final String reviewServiceUrl;

  @Autowired
  public ProductCompositeIntegration(
      RestTemplate restTemplate,
      ObjectMapper mapper,
      @Value("${app.product-service.host}") String productServiceHost,
      @Value("${app.product-service.port}") int productServicePort,
      @Value("${app.recommendation-service.host}") String recommendationServiceHost,
      @Value("${app.recommendation-service.port}") int recommendationServicePort,
      @Value("${app.review-service.host}") String reviewServiceHost,
      @Value("${app.review-service.port}") int reviewServicePort) {
    this.restTemplate = restTemplate;
    this.mapper = mapper;
    this.productServiceUrl =
        "http://" + productServiceHost + ":" + productServicePort + "/product/";
    this.recommendationServiceUrl =
        "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
    this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
  }

  @Override
  public Product createProduct(Product body) {
    try {
      return restTemplate.postForObject(productServiceUrl, body, Product.class);
    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Product getProduct(int productId) {
    try {
      String url = productServiceUrl + productId;
      LOG.debug("Will call getProductAPI on URL: {}", url);

      Product product = restTemplate.getForObject(url, Product.class);
      LOG.debug("Found a product with id: {}", product.getProductId());

      return product;
    } catch (HttpClientErrorException ex) {
      switch (ex.getStatusCode()) {
        case NOT_FOUND:
          throw new NotFoundException(getErrorMessage(ex));
        case UNPROCESSABLE_ENTITY:
          throw new InvalidInputException(getErrorMessage(ex));
        default:
          LOG.warn("Got a unexpected HTTP error: {}, will throw it", ex.getStatusCode());
          LOG.warn("Error body: {}", ex.getResponseBodyAsString());
          throw ex;
      }
    }
  }

  @Override
  public void deleteProduct(int productId) {
    try {
      restTemplate.delete(productServiceUrl + "/" + productId);
    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {
    try {
      return restTemplate.postForObject(recommendationServiceUrl, body, Recommendation.class);
    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public List<Recommendation> getRecommendations(int productId) {
    try {
      String url = recommendationServiceUrl + productId;

      LOG.debug("Will call recommendationAPI on URL: {}", url);
      List<Recommendation> recommendations = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<List<Recommendation>>() {}).getBody();

      LOG.debug("Found {} recommendations for a product with id: {}",
          recommendations.size(), productId);
      return recommendations;
    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}",
          ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void deleteRecommendations(int productId) {
    try {
      restTemplate.delete(recommendationServiceUrl + productId);
    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Review createReview(Review body) {
    try {
      return restTemplate.postForObject(reviewServiceUrl, body, Review.class);
    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public List<Review> getReviews(int productId) {
    try {
      String url = reviewServiceUrl + productId;

      LOG.debug("Will call getRewvies API on URL: {}, url");
      List<Review> reviews = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<List<Review>>() {
          }).getBody();

      LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
      return reviews;
    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}",
          ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void deleteReviews(int productId) {
    try {
      restTemplate.delete(reviewServiceUrl + productId);
    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
    switch (ex.getStatusCode()) {
      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(ex));
      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(ex));
      default:
        return ex;
    }
  }

  private String getErrorMessage(HttpClientErrorException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioEx) {
      return ex.getMessage();
    }
  }
}
