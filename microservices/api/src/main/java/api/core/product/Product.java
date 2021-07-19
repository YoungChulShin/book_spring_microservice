package api.core.product;

public class Product {

  private int productId;

  private String name;

  private int weight;

  private String serviceAddress;

  public Product() {
    this.productId = 0;
    this.name =  null;
    this.weight = 0;
    this.serviceAddress = null;
  }

  public Product(int productId, String name, int weight, String serviceAddress) {
    this.productId = productId;
    this.name = name;
    this.weight = weight;
    this.serviceAddress = serviceAddress;
  }

  public int getProductId() {
    return productId;
  }

  public String getName() {
    return name;
  }

  public int getWeight() {
    return weight;
  }

  public String getServiceAddress() {
    return serviceAddress;
  }

  public void updateServiceAddress(String serviceAddress) {
    this.serviceAddress = serviceAddress;
  }
}
