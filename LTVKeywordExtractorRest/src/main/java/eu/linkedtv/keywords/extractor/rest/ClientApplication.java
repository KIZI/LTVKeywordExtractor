package eu.linkedtv.keywords.extractor.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;

/**
 *
 * @author venca
 */
public class ClientApplication extends ResourceConfig {

    public ClientApplication() {
        register(EndpointExceptionHandler.class);
        register(MvcFeature.class);
        register(StaticViewProcessor.class);
        register(AuthFilter.class);
        register(ClientEndpoint.class);
        register(DocEndpoint.class);
    }
}
