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

  @Mappings({
      @Mapping(target = "serviceAddress", ignore = true)})
  Product entityToApi(ProductEntity entity);

  ProductEntity apiToEntity(Product api);
}
