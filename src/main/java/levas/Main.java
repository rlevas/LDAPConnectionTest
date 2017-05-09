package levas;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import java.io.Console;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import levas.socket.NonTrustingSSLSocketFactory12;
import levas.socket.TrustingSSLSocketFactory;
import levas.socket.TrustingSSLSocketFactory10;
import levas.socket.TrustingSSLSocketFactory11;
import levas.socket.TrustingSSLSocketFactory12;

public class Main {
  private static final String LDAP_CONTEXT_FACTORY_CLASS = "com.sun.jndi.ldap.LdapCtxFactory";
  private LdapContext ldapContext;
  private SearchControls searchControls;

  private String tlsVersion;
  private String adminPrincipal;
  private String adminPassword;
  private String ldapUrl;
  private String containerDN;


  public void open() throws IOException, NoSuchAlgorithmException {
    this.ldapContext = createLdapContext();
    this.searchControls = createSearchControls();
  }


  protected SearchControls createSearchControls() {
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
    searchControls.setReturningAttributes(new String[]{"cn"});
    return searchControls;
  }


  protected LdapContext createLdapContext() throws NoSuchAlgorithmException, IOException {
    Properties properties = new Properties();
    properties.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY_CLASS);
    properties.put(Context.PROVIDER_URL, ldapUrl);

    properties.put(Context.SECURITY_PRINCIPAL, adminPrincipal);
    properties.put(Context.SECURITY_CREDENTIALS, adminPassword);
    properties.put(Context.SECURITY_AUTHENTICATION, "simple");
    properties.put(Context.REFERRAL, "follow");

    if (ldapUrl.startsWith("ldaps")) {
      String tlsFactoryClassName;


      SocketFactory socketFactory;
      if ("1.2".equals(tlsVersion) || "TLSv1.2".equalsIgnoreCase(tlsVersion)) {
        socketFactory = TrustingSSLSocketFactory12.getDefault();
        tlsFactoryClassName = TrustingSSLSocketFactory12.class.getName();
      } else if ("1.1".equals(tlsVersion) || "TLSv1.1".equalsIgnoreCase(tlsVersion)) {
        socketFactory = TrustingSSLSocketFactory11.getDefault();
        tlsFactoryClassName = TrustingSSLSocketFactory11.class.getName();
      } else if ("1.0".equals(tlsVersion) || "TLSv1".equalsIgnoreCase(tlsVersion)) {
        socketFactory = TrustingSSLSocketFactory10.getDefault();
        tlsFactoryClassName = TrustingSSLSocketFactory10.class.getName();
      } else if ("TLS".equalsIgnoreCase(tlsVersion)) {
        socketFactory = TrustingSSLSocketFactory.getDefault();
        tlsFactoryClassName = TrustingSSLSocketFactory.class.getName();
      } else if ("TLSv1.2_NonTrusting".equalsIgnoreCase(tlsVersion)) {
        socketFactory = NonTrustingSSLSocketFactory12.getDefault();
        tlsFactoryClassName = NonTrustingSSLSocketFactory12.class.getName();
      } else {
        socketFactory = SSLContext.getDefault().getSocketFactory();
        tlsFactoryClassName = null;
      }

      System.out.println("*** Using " +
          ((tlsFactoryClassName == null) ? "_DEFAULT_" : tlsFactoryClassName));

      if (tlsFactoryClassName != null) {
        properties.put("java.naming.ldap.factory.socket", tlsFactoryClassName);
      }

      SSLSocket socket = (SSLSocket) socketFactory.createSocket();

      String[] protocols;

      protocols = socket.getSupportedProtocols();
      System.out.println("Supported Protocols: " + protocols.length);
      for (int i = 0; i < protocols.length; i++) {
        System.out.println(" " + protocols[i]);
      }

      protocols = socket.getEnabledProtocols();
      System.out.println("Enabled Protocols: " + protocols.length);
      for (int i = 0; i < protocols.length; i++) {
        System.out.println(" " + protocols[i]);
      }
    }

    try {
      return new InitialLdapContext(properties, null);
    } catch (CommunicationException e) {
      String message = String.format("Failed to communicate with the Active Directory at %s: %s", ldapUrl, e.getMessage());
      System.err.println(message);
      e.printStackTrace(System.err);
    } catch (AuthenticationException e) {
      String message = String.format("Failed to authenticate with the Active Directory at %s: %s", ldapUrl, e.getMessage());
      System.err.println(message);
      e.printStackTrace(System.err);
    } catch (NamingException e) {
      String error = e.getMessage();
      String message = String.format("Failed to communicate with the Active Directory at %s: %s", ldapUrl, e.getMessage());
      System.err.println(message);
      e.printStackTrace(System.err);
    }

    return null;
  }

  private String findPrincipalDN(String normalizedPrincipal) throws NamingException {
    String dn = null;

    if (normalizedPrincipal != null) {
      NamingEnumeration<SearchResult> results = null;

      try {
        results = ldapContext.search(
            containerDN,
            String.format("(userPrincipalName=%s)", normalizedPrincipal),
            searchControls
        );

        if ((results != null) && results.hasMore()) {
          SearchResult result = results.next();
          dn = result.getNameInNamespace();
        }
      } finally {
        try {
          if (results != null) {
            results.close();
          }
        } catch (NamingException ne) {
          // ignore, we can not do anything about it
        }
      }
    }

    return dn;
  }


  public static void main(String[] args) throws NamingException, IOException, NoSuchAlgorithmException {
    System.setProperty("javax.net.debug", "ssl");

    Main main = new Main();

    main.ldapUrl = args[0];
    main.containerDN = args[1];
    main.adminPrincipal = args[2];
    main.tlsVersion = args[3];
    main.adminPassword = (args.length > 5) ? args[5] : null;

    if (main.adminPassword == null) {
      Console cons;
      char[] passwd;
      if ((cons = System.console()) != null &&
          (passwd = cons.readPassword("%s", "Password:")) != null) {
        main.adminPassword = String.valueOf(passwd);
      }
    }

    main.open();

    System.out.println(main.findPrincipalDN(args[4]));
  }
}
