# LDAPConnectionTest
Tests connecting to an LDAP server using different SSLSocketFactory implementations.

The test connects to the specified LDAP URL, binds as some user (principal), and then 
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
   * TLS
   * TLSv1
   * TLSv1.1
   * TLSv1.2
   * TLSv1.2_NonTrusting
  
 * `Search User Principal` - The principal name of the user object to search for - 
 the attribute to match on is `userPrincipalName`
 
 * `Password` - Optional. The password for the user to connect to the LDAP server as.  
 If not set, a prompt for the password will be displayed. 
 
 