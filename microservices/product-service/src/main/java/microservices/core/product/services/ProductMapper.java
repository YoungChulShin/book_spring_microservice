package microservices.core.product.services;

import api.core.product.Product;
import microservices.core.product.persistence.ProductEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductMapper {

  Product entityToApi(ProductEntity entity);

  ProductEntity apiToEntity(Product api);
}
