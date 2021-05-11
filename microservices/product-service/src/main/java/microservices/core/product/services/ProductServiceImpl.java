package microservices.core.product.services;

import api.core.product.Product;
import api.core.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.NotFoundException;
import util.http.ServiceUtil;

@RestController
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ServiceUtil serviceUtil;

  @Override
  public Product getProduct(int productId) {

    if (productId == 13) {
      throw new NotFoundException("No product found for productId: " + productId);
    }
    return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
  }
}
