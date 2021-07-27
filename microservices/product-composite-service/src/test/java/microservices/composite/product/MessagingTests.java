package microservices.composite.product;

import static org.hamcrest.Matchers.is;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static reactor.core.publisher.Mono.just;

import api.composite.product.ProductAggregate;
import api.core.product.Product;
import api.event.Event;
import api.event.Event.Type;
import java.util.concurrent.BlockingQueue;
import microservices.composite.product.services.ProductCompositeIntegration;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.matcher.MessageQueueMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessagingTests {

  @Autowired
  private WebTestClient client;
  @Autowired
  private ProductCompositeIntegration.MessageSource channels;
  @Autowired
  private MessageCollector collector; // 테스트 중에 전송된 메시지를 확인할 수 있다

  BlockingQueue<Message<?>> queueProduct = null;
  BlockingQueue<Message<?>> queueRecommendations = null;
  BlockingQueue<Message<?>> queueReviews = null;

  @Before
  public void setup() {
    queueProduct = getQueue(channels.outputProducts());
    queueRecommendations = getQueue(channels.outputRecommendations());
    queueReviews = getQueue(channels.outputReviews());
  }

  @Test
  public void createCompositeProduct1() {
    ProductAggregate composite = new ProductAggregate(1, "name", 1, null, null, null);
    // 이벤트를 보내고 결과를 체크
    postAndVerifyProduct(composite, HttpStatus.OK);

    // Queue 사이즈를 체크
    Assertions.assertThat(queueProduct.size()).isEqualTo(1);

    // Queue에서 예상한 이벤트가 맞는지 체크
    Event<Integer, Product> expectedEvent = new Event(
        Type.CREATE,
        composite.getProductId(),
        new Product(composite.getProductId(), composite.getName(), composite.getWeight(), null));

    //Assertions.assertThat()
    Assert.assertThat(
        queueProduct,
        Matchers.is(
            MessageQueueMatcher.receivesPayloadThat(
                IsSameEvent.sameEventExceptCreatedAt(expectedEvent)
            )));

    Assertions.assertThat(queueRecommendations.size()).isEqualTo(0);
    Assertions.assertThat(queueReviews.size()).isEqualTo(0);
  }

  private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
    return collector.forChannel(messageChannel);
  }

  private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
    client.post()
        .uri("/product-composite")
        .body(just(compositeProduct), ProductAggregate.class)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus);
  }
}
