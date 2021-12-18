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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.http.HttpErrorInfo;

@Component
public class ProductCompositeIntegration implements
    ProductService,
    RecommendationService,
    ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

  private final WebClient webClient;
  private final ObjectMapper mapper;

  private final String productServiceUrl;
  private final String recommendationServiceUrl;
  private final String reviewServiceUrl;

  @Autowired
  public ProductCompositeIntegration(
      WebClient.Builder webClientBuilder,
      ObjectMapper mapper,
      @Value("${app.product-service.host}") String productServiceHost,
      @Value("${app.product-service.port}") int productServicePort,
      @Value("${app.recommendation-service.host}") String recommendationServiceHost,
      @Value("${app.recommendation-service.port}") int recommendationServicePort,
      @Value("${app.review-service.host}") String reviewServiceHost,
      @Value("${app.review-service.port}") int reviewServicePort) {
    this.webClient = webClientBuilder.build();
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
  public Mono<Product> getProduct(int productId) {
  public Mono<Product> getProduct(int productId) {
    String url = productServiceUrl + productId;
    LOG.debug("Will call getProductAPI on URL: {}", url);

    return webClient.get()
        .uri(url).retrieve().bodyToMono(Product.class)
        .onErrorMap(HttpClientErrorException.class, this::handleException);
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
  public Flux<Recommendation> getRecommendations(int productId) {
      String url = recommendationServiceUrl + productId;

      LOG.debug("Will call recommendationAPI on URL: {}", url);

      return webClient.get()
          .uri(url).retrieve().bodyToFlux(Recommendation.class)
          .log()
          .onErrorResume(error -> Flux.empty());
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
  public Flux<Review> getReviews(int productId) {
      String url = reviewServiceUrl + productId;
      LOG.debug("Will call getRewvies API on URL: {}, url");

      return webClient.get()
          .uri(url).retrieve().bodyToFlux(Review.class)
          .log()
          .onErrorResume(error -> Flux.empty());
  }

  @Override
  public void deleteReviews(int productId) {
    try {
      restTemplate.delete(reviewServiceUrl + productId);
    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  private Throwable handleException(Throwable ex) {
    if (!(ex instanceof WebClientResponseException)) {
      LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
      return ex;
    }

    WebClientResponseException wcex = (WebClientResponseException)ex;

    switch (wcex.getStatusCode()) {
      case NOT_FOUND:
        throw new NotFoundException(getErrorMessage(wcex));
      case UNPROCESSABLE_ENTITY:
        throw new InvalidInputException(getErrorMessage(wcex));
      default:
        LOG.warn("Got a unexpected HTTP error: {}, will throw it", wcex.getStatusCode());
        LOG.warn("Error body: {}", wcex.getResponseBodyAsString());
        return wcex;
    }
  }

  private String getErrorMessage(WebClientResponseException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioEx) {
      return ex.getMessage();
    }
  }
}
