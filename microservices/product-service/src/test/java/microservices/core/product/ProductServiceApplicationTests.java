//package microservices.core.product;
//
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//public class ProductServiceApplicationTests {
//
//  @Autowired
//  private WebTestClient client;
//
//  @Test
//  public void getProductById() {
//    int productId = 1;
//
//    client.get()
//        .uri("/product/" + productId)
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectHeader().contentType(MediaType.APPLICATION_JSON)
//        .expectBody()
//          .jsonPath("$.productId").isEqualTo(productId);
//  }
//
//  @Test
//  public void getProductInvalidParameterString() {
//    client.get()
//        .uri("/product/no-integer")
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isBadRequest()
//        .expectHeader().contentType(MediaType.APPLICATION_JSON)
//        .expectBody()
//        .jsonPath("$.path").isEqualTo("/product/no-integer");
//  }
//
//  @Test
//  public void getProductNotFound() {
//    int productIdNotFound = 13;
//
//    client.get()
//        .uri("/product/" + productIdNotFound)
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isNotFound()
//        .expectHeader().contentType(MediaType.APPLICATION_JSON)
//        .expectBody()
//        .jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
//        .jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);
//  }
//
//  @Test
//  public void getProductInvalidParameterNegativeValue() {
//
//  }
//}
