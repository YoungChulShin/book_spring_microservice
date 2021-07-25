package microservices.core.review.services;

import api.core.review.Review;
import api.core.review.ReviewService;
import com.oracle.tools.packager.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import microservices.core.review.persistence.ReviewEntity;
import microservices.core.review.persistence.ReviewRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import util.exceptions.InvalidInputException;
import util.http.ServiceUtil;

@RestController
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

  private final Scheduler scheduler;
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final ServiceUtil serviceUtil;

  @Override
  public Review createReview(Review body) {
    if (body.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + body.getProductId());
    }

    try {
      ReviewEntity entity = reviewMapper.apiToEntity(body);
      ReviewEntity newEntity = reviewRepository.save(entity);

      return reviewMapper.entityToApi(newEntity);
    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id: " + body.getReviewId());
    }
  }

  @Override
  public Flux<Review> getReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.info("Will get reviews for product with id={}", productId);

    return asyncFlux(() -> Flux.fromIterable(getByProductId(productId)))
        .log(null, Level.FINE);
  }

  protected List<Review> getByProductId(int productId) {
    List<ReviewEntity> reviewEntityList = reviewRepository.findByProductId(productId);
    List<Review> reviewList = reviewMapper.entityListToApiList(reviewEntityList);
    reviewList.forEach(r -> r.updateServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("/reviews response size: {}", reviewList.size());

    return reviewList;
  }

  @Override
  public void deleteReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
  }

  private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
    return Flux.defer(publisherSupplier).subscribeOn(scheduler);
  }
}
