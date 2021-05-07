package api.composite.product;

public class ServiceAddresses {

  private final String cmp;

  private final String pro;

  private final String rev;

  private final String rec;

  public ServiceAddresses() {
    cmp = null;
    pro = null;
    rev = null;
    rec = null;
  }

  public ServiceAddresses(String cmp, String pro, String rev, String rec) {
    this.cmp = cmp;
    this.pro = pro;
    this.rev = rev;
    this.rec = rec;
  }
}
