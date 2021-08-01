package microservices.core.product;


import microservices.core.product.persistence.ProductRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.MediaType;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductServiceApplicationTests {

  @Autowired
  private WebTestClient client;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private Sink channels;

  private AbstractMessageChannel input = null;

  @Before
  public void setup() {
    input = (AbstractMessageChannel)channels.input();
    productRepository.deleteAll().block();
  }

  @Test
  public void getProductById() {
    int productId = 1;

    client.get()
        .uri("/product/" + productId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
          .jsonPath("$.productId").isEqualTo(productId);
  }

  @Test
  public void getProductInvalidParameterString() {
    client.get()
        .uri("/product/no-integer")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path").isEqualTo("/product/no-integer");
  }

  @Test
  public void getProductNotFound() {
    int productIdNotFound = 13;

    client.get()
        .uri("/product/" + productIdNotFound)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
        .jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);
  }

  @Test
  public void getProductInvalidParameterNegativeValue() {

  }
}
