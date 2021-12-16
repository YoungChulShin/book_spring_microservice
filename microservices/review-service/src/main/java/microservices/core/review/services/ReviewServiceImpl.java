package microservices.core.review.services;

import api.core.review.Review;
import api.core.review.ReviewService;
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

  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final ServiceUtil serviceUtil;
  private final Scheduler scheduler;

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

//  @Override
//  public List<Review> getReviews(int productId) {
//    if (productId < 1) {
//      throw new InvalidInputException("Invalid productId: " + productId);
//    }
//
//    List<ReviewEntity> reviewEntityList = reviewRepository.findByProductId(productId);
//    List<Review> reviewList = reviewMapper.entityListToApiList(reviewEntityList);
//    reviewList.forEach(r -> r.updateServiceAddress(serviceUtil.getServiceAddress()));
//
//    LOG.debug("/reviews response size: {}", reviewList.size());
//
//    return reviewList;
//  }

  // 동기 메서드를 Scheduler를 이용해서 논블럭킹으로 동작하도록 수정
  // Scheduler는 ThreadPoolExecutor를 사용한다
  @Override
  public Flux<Review> getReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    return asyncFlux(() -> Flux.fromIterable(getByProductId(productId)))
        .log(null, Level.FINE);
  }

  private List<Review> getByProductId(int productId) {
    List<ReviewEntity> reviewEntityList = reviewRepository.findByProductId(productId);
    List<Review> reviewList = reviewMapper.entityListToApiList(reviewEntityList);
    reviewList.forEach(r -> r.updateServiceAddress(serviceUtil.getServiceAddress()));

    return reviewList;
  }

  private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
    return Flux.defer(publisherSupplier).subscribeOn(scheduler);
  }

  @Override
  public void deleteReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
  }
}
