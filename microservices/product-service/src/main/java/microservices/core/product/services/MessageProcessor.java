package microservices.core.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
import api.event.Event;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
@RequiredArgsConstructor
public class MessageProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

  private final ProductService productService;

  public void process(Event<Integer, Product> event) {
    LOG.info("Process message created at {}...", event.getEventCreatedAt());

    switch (event.getEventType()) {
      case CREATE:
        Product product = event.getData();
        productService.createProduct(product);
        break;
      case DELETE:
        productService.deleteProduct(event.getKey());
        break;
      default:
        String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
        LOG.warn(errorMessage);
        throw new EventProcessingException(errorMessage);
    }

    LOG.info("Message processing done");
  }
}
