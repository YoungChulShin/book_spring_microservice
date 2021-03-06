package microservices.core.product.persistence;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Getter
public class ProductEntity {

  @Id
  private String id;

  @Version
  private Integer version;

  @Indexed(unique = true)
  private int productId;

  private String name;

  private int weight;

  protected ProductEntity() {
  }

  @Builder
  public ProductEntity(int productId, String name, int weight) {
    this.productId = productId;
    this.name = name;
    this.weight = weight;
  }

  public void updateName(String name) {
    this.name = name;
  }
}
