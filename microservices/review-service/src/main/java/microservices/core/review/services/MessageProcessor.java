package microservices.core.review.services;

import api.core.review.Review;
import api.core.review.ReviewService;
import api.event.Event;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import util.exceptions.EventProcessingException;

@EnableBinding(value = Sink.class)
@RequiredArgsConstructor
public class MessageProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

  private final ReviewService reviewService;

  @StreamListener(target = Sink.INPUT)
  public void process(Event<Integer, Review> event) {
    LOG.info("Process message created at {}...", event.getEventCreatedAt());

    switch (event.getEventType()) {
      case CREATE:
        reviewService.createReview(event.getData());
        break;
      case DELETE:
        reviewService.deleteReviews(event.getKey());
        break;
      default:
        String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
        LOG.warn(errorMessage);
        throw new EventProcessingException(errorMessage);
    }

    LOG.info("Message processing done");
  }
}
