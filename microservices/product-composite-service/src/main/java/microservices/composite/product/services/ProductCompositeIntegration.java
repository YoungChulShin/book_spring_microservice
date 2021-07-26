package microservices.composite.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
import api.core.recommendation.Recommendation;
import api.core.recommendation.RecommendationService;
import api.core.review.Review;
import api.core.review.ReviewService;
import api.event.Event;
import api.event.Event.Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.http.HttpErrorInfo;


@EnableBinding(ProductCompositeIntegration.MessageSource.class)
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

  private MessageSource messageSources;

  public interface MessageSource {

    String OUTPUT_PRODUCTS = "output-products";
    String OUTPUT_RECOMMENDATIONS = "output-recommendations";
    String OUTPUT_REVIEWS = "output-review";

    @Output(OUTPUT_PRODUCTS)
    MessageChannel outputProducts();

    @Output(OUTPUT_RECOMMENDATIONS)
    MessageChannel outputRecommendations();

    @Output(OUTPUT_REVIEWS)
    MessageChannel outputReviews();
  }

  @Autowired
  public ProductCompositeIntegration(
      WebClient.Builder webClient,
      ObjectMapper mapper,
      MessageSource messageSources,
      @Value("${app.product-service.host}") String productServiceHost,
      @Value("${app.product-service.port}") int productServicePort,
      @Value("${app.recommendation-service.host}") String recommendationServiceHost,
      @Value("${app.recommendation-service.port}") int recommendationServicePort,
      @Value("${app.review-service.host}") String reviewServiceHost,
      @Value("${app.review-service.port}") int reviewServicePort) {
    this.webClient = webClient.build();
    this.mapper = mapper;
    this.messageSources = messageSources;

    this.productServiceUrl =
        "http://" + productServiceHost + ":" + productServicePort + "/product/";
    this.recommendationServiceUrl =
        "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
    this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
  }

  @Override
  public Product createProduct(Product body) {
    messageSources
        .outputProducts()
        .send(MessageBuilder.withPayload(
            new Event(Type.CREATE, body.getProductId(), body)).build());
    return body;
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
    messageSources
        .outputProducts()
        .send(MessageBuilder.withPayload(new Event(Type.DELETE, productId, null)).build());
//    try {
//      restTemplate.delete(productServiceUrl + "/" + productId);
//    } catch (HttpClientErrorException ex) {
//      throw handleHttpClientException(ex);
//    }
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {
    messageSources
        .outputRecommendations()
        .send(MessageBuilder.withPayload(
            new Event(Type.CREATE, body.getProductId(), body)).build());

    return body;
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
    messageSources
        .outputRecommendations()
        .send(MessageBuilder.withPayload(new Event(Type.DELETE, productId, null)).build());
  }

  @Override
  public Review createReview(Review body) {
    messageSources
        .outputReviews()
        .send(MessageBuilder.withPayload(
            new Event(Type.CREATE, body.getProductId(), body)).build());

    return body;
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
    messageSources
        .outputReviews()
        .send(MessageBuilder.withPayload(
            new Event(Type.DELETE, productId, null)).build());
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
}
