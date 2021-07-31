package microservices.core.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
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

  private final ProductService productService;

  @StreamListener(target = Sink.INPUT)
  public void process(Event<Integer, Product> event) {

    LOG.info("Process message created at {}", event.getEventCreatedAt());

    switch (event.getEventType()) {
      case CREATE:
        Product product = event.getData();
        LOG.info("Create product with ID: {}", product.getProductId());
        productService.createProduct(product);
        break;
      case DELETE:
        int productId = event.getKey();
        LOG.info("Delete product with ID: {}", productId);
        productService.deleteProduct(productId);
        break;
      default:
        String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATED or DELETE event";
        LOG.warn(errorMessage);
        throw new EventProcessingException(errorMessage);
    }
  }
}
