/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.ethz.origo.jerpa.ededclient.sources;

/**
 *
 * @author Petr Miko
 */

import ch.ethz.origo.jerpa.ededclient.generated.UserDataService;
import ch.ethz.origo.jerpa.ededclient.ssl.AcceptAllTrustManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import javax.net.ssl.TrustManager;
import javax.xml.ws.WebServiceException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Petr Miko
 */
public class EDEDClient {

    private String username, password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private boolean connected;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    private UserDataService service;

    public UserDataService getService() {
        return service;
    }

    private HTTPConduit conduit;
    private Client client;

    public static EDEDClient getInstance() {
        return instance;
    }

    private static EDEDClient instance = null;

    public EDEDClient() {
        connected = false;
        instance = this;
    }



    public void userLogIn(String username, String password, String endpoint) throws WebServiceException, ConnectException {

        //this should not be neccessary due to CXF runtime auto setup, but just to be safe
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mtom-enabled", Boolean.TRUE);

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        factory.setServiceClass(UserDataService.class);
        factory.setAddress(endpoint);
        factory.setProperties(properties);
        factory.setUsername(username);
        factory.setPassword(password);

        service = (UserDataService) factory.create();

        client = ClientProxy.getClient(service);

        conduit = (HTTPConduit) client.getConduit();

        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        params.setUseHttpsURLConnectionDefaultSslSocketFactory(false);
        params.setTrustManagers(new TrustManager[]{new AcceptAllTrustManager()});
        conduit.setTlsClientParameters(params);

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(1000 * 60 * 60);
        httpClientPolicy.setReceiveTimeout(1000 * 60 * 60 * 60);
        httpClientPolicy.setChunkingThreshold(512);

        conduit.setClient(httpClientPolicy);


        if (isServerAvailable()) {
            this.username = username;
            this.password = password;
        }
    }

    public void userLogout() {

        if (conduit != null)
            conduit.close();

        if (client != null)
            client.destroy();

        conduit = null;
        client = null;
        username = null;
        password = null;
        connected = false;

    }

    public boolean isServerAvailable() throws ConnectException, WebServiceException {

        connected = service.isServiceAvailable();

        return connected;

    }
}
