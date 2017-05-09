package levas.socket;

import javax.net.SocketFactory;

public class TrustingSSLSocketFactory extends SSLSocketFactoryImpl {
  public static SocketFactory getDefault() {
    return new TrustingSSLSocketFactory();
  }

  public TrustingSSLSocketFactory() {
    super("TLS");
  }
}
