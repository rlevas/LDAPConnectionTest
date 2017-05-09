package levas.socket;

import javax.net.SocketFactory;

public class TrustingSSLSocketFactory11 extends SSLSocketFactoryImpl {
  public static SocketFactory getDefault() {
    return new TrustingSSLSocketFactory11();
  }

  public TrustingSSLSocketFactory11() {
    super("TLSv1.1");
  }
}
