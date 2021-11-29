package microservices.core.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import microservices.core.product.persistence.ProductEntity;
import microservices.core.product.persistence.ProductRepository;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.http.ServiceUtil;

@RestController
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  private final ProductMapper productMapper;
  private final ServiceUtil serviceUtil;

  @Override
  public Product createProduct(Product body) {
    try {
      ProductEntity entity = productMapper.apiToEntity(body);
      ProductEntity newEntity = productRepository.save(entity);
      return productMapper.entityToApi(newEntity);
    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
    }
  }

  @Override
  public Product getProduct(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    ProductEntity entity = productRepository.findByProductId(productId)
        .orElseThrow(() -> new NotFoundException("No product found for productId : " + productId));

    Product response = productMapper.entityToApi(entity);
    response.updateServiceAddress(serviceUtil.getServiceAddress());
    return response;
  }

  @Override
  public void deleteProduct(int productId) {
    productRepository.findByProductId(productId).ifPresent(e -> productRepository.delete(e));
  }
}
