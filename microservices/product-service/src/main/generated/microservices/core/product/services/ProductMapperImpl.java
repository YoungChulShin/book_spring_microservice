package microservices.core.product.services;

import api.core.product.Product;
import api.core.product.Product.ProductBuilder;
import javax.annotation.Generated;
import microservices.core.product.persistence.ProductEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-20T23:59:24+0900",
    comments = "version: 1.4.1.Final, compiler: javac, environment: Java 1.8.0_265 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product entityToApi(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductBuilder product = Product.builder();

        product.productId( entity.getProductId() );
        product.name( entity.getName() );
        product.weight( entity.getWeight() );

        return product.build();
    }

    @Override
    public ProductEntity apiToEntity(Product api) {
        if ( api == null ) {
            return null;
        }

        ProductEntity productEntity = new ProductEntity();

        return productEntity;
    }
}
