package microservices.core.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
import com.mongodb.DuplicateKeyException;
import com.oracle.tools.packager.Log;
import lombok.RequiredArgsConstructor;
import microservices.core.product.persistence.ProductEntity;
import microservices.core.product.persistence.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.http.ServiceUtil;

@RestController
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final ServiceUtil serviceUtil;

  @Override
  public Product createProduct(Product body) {
    if (body.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + body.getProductId());
    }

    ProductEntity entity = productMapper.apiToEntit(body);
    Mono<Product> newEntity = productRepository.save(entity)
        .log()
        .onErrorMap(
            DuplicateKeyException.class,
            ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
        .map(e -> productMapper.entityToApi(e));

    return newEntity.block();
  }

  @Override
  public Mono<Product> getProduct(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    return productRepository.findByProductId(productId)
        .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId : " + productId)))
        .log()
        .map(e -> productMapper.entityToApi(e))
        .map(e -> {
          e.updateServiceAddress(serviceUtil.getServiceAddress());
          return e;
        });
  }

  @Override
  public void deleteProduct(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.debug("deleteProduct : tries to delete an entity with productId: {}", productId);
    productRepository
        .findByProductId(productId)
        .log()
        .map(e -> productRepository.delete(e))
        .flatMap(e -> e)
        .block();
  }
}
