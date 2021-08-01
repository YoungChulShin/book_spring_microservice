package microservices.composite.product;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

import java.util.Collections;
import java.util.LinkedHashMap;
import microservices.composite.product.services.ProductCompositeIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.DefaultReactiveHealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributorRegistry;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(value = {"microservices.composite.product", "util"})
public class ProductCompositeServiceApplication {

  @Value("${api.common.version}") String apiVersion;
  @Value("${api.common.title}") String apiTitle;
  @Value("${api.common.description}") String apiDescription;
  @Value("${api.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
  @Value("${api.common.license}") String apiLicense;
  @Value("${api.common.licenseUrl}") String apiLicenseUrl;
  @Value("${api.common.contact.name}") String apiContactName;
  @Value("${api.common.contact.url}") String apiContactUrl;
  @Value("${api.common.contact.email}") String apiContactEmail;

  public static void main(String[] args) {
    SpringApplication.run(ProductCompositeServiceApplication.class, args);
  }

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public Docket apiDocumentation() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(basePackage("microservices.composite.product"))
        .paths(PathSelectors.any())
        .build()
          .globalResponses(HttpMethod.GET, Collections.emptyList())
          .apiInfo(new ApiInfo(
              apiTitle,
              apiDescription,
              apiVersion,
              apiTermsOfServiceUrl,
              new Contact(apiContactName, apiContactUrl, apiContactEmail),
              apiLicense,
              apiLicenseUrl,
              Collections.emptyList()));
  }

//  @Autowired
//  StatusAggregator statusAggregator;
//
//  @Autowired
//  ProductCompositeIntegration integration;
//
//  @Bean
//  ReactiveHealthIndicator coreServices() {
//    ReactiveHealthContributorRegistry registry = new DefaultReactiveHealthContributorRegistry(new LinkedHashMap<>());
//
//
//    registry.registerContributor("product", () -> integration.getProductHealth());
//  }


}
