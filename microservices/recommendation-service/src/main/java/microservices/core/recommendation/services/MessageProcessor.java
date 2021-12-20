package microservices.core.recommendation.services;

import api.core.recommendation.Recommendation;
import api.core.recommendation.RecommendationService;
import api.event.Event;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
@RequiredArgsConstructor
public class MessageProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

  private final RecommendationService recommendationService;

  @StreamListener(target = Sink.INPUT)
  public void process(Event<Integer, Recommendation> event) {
    LOG.info("Process message created at {}...", event.getEventCreatedAt());

    switch (event.getEventType()) {
      case CREATE:
        recommendationService.createRecommendation(event.getData());
        break;
      case DELETE:
        recommendationService.deleteRecommendations(event.getKey());
        break;
      default:
        String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
        LOG.warn(errorMessage);
        throw new EventProcessingException(errorMessage);
    }

    LOG.info("Message processing done");
  }
}
