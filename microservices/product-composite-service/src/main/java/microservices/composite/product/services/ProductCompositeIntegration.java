package microservices.composite.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
import api.core.recommendation.Recommendation;
import api.core.recommendation.RecommendationService;
import api.core.review.Review;
import api.core.review.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;
  private final String productServiceUrl;
  private final String recommendationServiceUrl;
  private final String reviewServiceUrl;

  @Autowired
  public ProductCompositeIntegration(
      WebClient.Builder webClient,
      RestTemplate restTemplate,
      ObjectMapper mapper,
      @Value("${app.product-service.host}") String productServiceHost,
      @Value("${app.product-service.port}") int productServicePort,
      @Value("${app.recommendation-service.host}") String recommendationServiceHost,
      @Value("${app.recommendation-service.port}") int recommendationServicePort,
      @Value("${app.review-service.host}") String reviewServiceHost,
      @Value("${app.review-service.port}") int reviewServicePort) {
    this.webClient = webClient.build();
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
  public Mono<Product> getProduct(int productId) {
    String url = productServiceUrl + productId;
    LOG.debug("Will call getProductAPI on URL: {}", url);

    return webClient
        .get().uri(url).retrieve().bodyToMono(Product.class)
        .log()
        .onErrorMap(
            WebClientResponseException.class,
            ex -> handleException(ex));
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

    return webClient.get().uri(url).retrieve().bodyToFlux(Recommendation.class)
        .log()
        .onErrorResume(error -> Flux.empty());
  }

  @Override
  public void deleteRecommendations(int productId) {
    try {
      restTemplate.delete(recommendationServiceUrl + "?productId=" + productId);
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

    return webClient.get().uri(url).retrieve().bodyToFlux(Review.class)
        .log()
        .onErrorResume(error -> Flux.empty());
  }

  @Override
  public void deleteReviews(int productId) {
    try {
      restTemplate.delete(reviewServiceUrl + "?productId=" + productId);
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

  private Throwable handleException(Throwable ex) {
    if (!(ex instanceof WebClientResponseException)) {
      LOG.warn("Got an unexpected error: {}, will throw it", ex.toString());
    }

    WebClientResponseException wcre = (WebClientResponseException)ex;
    switch (wcre.getStatusCode()) {
      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(wcre));
      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(wcre));
      default:
        LOG.warn("Got a unexpected HTTP error: {}, will throw it", wcre.getStatusCode());
        LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
        return ex;
    }
  }

  private String getErrorMessage(WebClientResponseException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioEx) {
      return ex.getMessage();
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
