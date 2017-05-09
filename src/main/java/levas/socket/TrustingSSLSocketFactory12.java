package levas.socket;

import javax.net.SocketFactory;

public class TrustingSSLSocketFactory12 extends SSLSocketFactoryImpl {
  public static SocketFactory getDefault() {
    return new TrustingSSLSocketFactory12();
  }

  public TrustingSSLSocketFactory12() {
    super("TLSv1.2");
  }
}
