# LDAPConnectionTest
Tests connecting to an LDAP server using different SSLSocketFactory implementations.

The test connections to the specified LDAP URL, binds as some user (principal), and then 
searches for a user object using a base DN and a principal name. The result is the `CN` of 
the found object. SSL debugging is turned on to show the relevant SSL connection messages. 
  
The current implementation assumes the LDAP server is an Active Directory and searches for 
a user object using the `userPrincipalName` field.  

Usage:
 `$JAVA_HOME/bin/java -jar LDAPConnectionTest-1.0.jar <LDAP URL> <Base Container> <User Principal> <TLS Version> <Search User Principal> [Password]`
 
 Where: 
 
 * `LDAP URL` - The LDAP or LDAPS URL for the relevant LDAP server, with or without the port.
   * ldaps://ad.host.com
   * ldaps://ad.host.com:636
   * ldap://ad.host.com
 
 * `Base Container` - The DN used to start the search operation
   * `CN=Users,DC=EXAMPLE,DC=COM`
 
 * `User Principal` - The principal name of the user to connect to the LDAP server as
 
 * `TLS Version` - The TLS protocol version to use when creating the `SSLSocketFactory` implementation
   * `TLS` - The JVM chooses the enabled TLS versions (for Java 1.7 TLS1 has been observed, for Java 1.8, TLS1.2 has been observed). 
   The SSL socket is trusting, so no certificate validation is performed. 
   * `TLSv1` - Use TLSv1. The SSL socket is trusting, so no certificate validation is performed.
   * `TLSv1.1` - Use TLSv1.1. The SSL socket is trusting, so no certificate validation is performed.
   * `TLSv1.2` - Use TLSv1.2. The SSL socket is trusting, so no certificate validation is performed
   * `TLSv1.2_NonTrusting` - Use TLSv1.2. The SSL socket is non-trusting, so certificate validation is performed
  
 * `Search User Principal` - The principal name of the user object to search for - 
 the attribute to match on is `userPrincipalName`
 
 * `Password` - Optional. The password for the user to connect to the LDAP server as.  
 If not set, a prompt for the password will be displayed. 
 
 