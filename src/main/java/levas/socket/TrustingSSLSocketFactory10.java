package levas.socket;

import javax.net.SocketFactory;

public class TrustingSSLSocketFactory10 extends SSLSocketFactoryImpl {
  public static SocketFactory getDefault() {
    return new TrustingSSLSocketFactory10();
  }

  public TrustingSSLSocketFactory10() {
    super("TLSv1");
  }
}
