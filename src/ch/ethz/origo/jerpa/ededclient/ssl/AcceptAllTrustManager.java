package ch.ethz.origo.jerpa.ededclient.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 *
 * @author jezek
 */
public class AcceptAllTrustManager implements javax.net.ssl.X509TrustManager {

    /**
     * Empty array of certificate authority certificates.
     */
    private static final X509Certificate[] _AcceptedIssuers =
        new X509Certificate[] {};



    /**
     * Return an empty array of certificate authority certificates which
     * are trusted for authenticating peers.
     *
     * @return                a empty array of issuer certificates.
     */
    public X509Certificate[] getAcceptedIssuers() {
        return(_AcceptedIssuers);
    } // getAcceptedIssuers

      public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {

      }

      public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
      }
}
