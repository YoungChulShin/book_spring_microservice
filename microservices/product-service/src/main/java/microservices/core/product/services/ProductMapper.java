package microservices.core.product.services;

import api.core.product.Product;
import microservices.core.product.persistence.ProductEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductMapper {

  Product entityToApi(ProductEntity entity);

  @Mappings({
      @Mapping(target = "productId", source = "api.productId")})
  ProductEntity apiToEntity(Product api);
}
