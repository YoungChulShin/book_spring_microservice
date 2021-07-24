package microservices.core.product.services;

import api.core.product.Product;
import javax.annotation.Generated;
import microservices.core.product.persistence.ProductEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-07-24T16:02:12+0900",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_265 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product entityToApi(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Product product = new Product();

        return product;
    }

    @Override
    public ProductEntity apiToEntit(Product api) {
        if ( api == null ) {
            return null;
        }

        ProductEntity productEntity = new ProductEntity();

        return productEntity;
    }
}
