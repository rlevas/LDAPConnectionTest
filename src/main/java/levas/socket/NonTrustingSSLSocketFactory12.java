package levas.socket;

import javax.net.SocketFactory;

public class NonTrustingSSLSocketFactory12 extends SSLSocketFactoryImpl {
  public static SocketFactory getDefault() {
    return new NonTrustingSSLSocketFactory12();
  }

  public NonTrustingSSLSocketFactory12() {
    super("TLSv1.2", false);
  }
}
