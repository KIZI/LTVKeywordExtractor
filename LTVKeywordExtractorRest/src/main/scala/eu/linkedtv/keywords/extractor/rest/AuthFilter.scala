package eu.linkedtv.keywords.extractor.rest

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@Provider
class AuthFilter extends ContainerRequestFilter {

  def filter(requestContext: ContainerRequestContext) = {
    if (!requestContext.getUriInfo.getPath.startsWith("doc")) {
      val apiKey = requestContext.getHeaderString("userKey")
      if (apiKey == null || apiKey.isEmpty)
        requestContext.abortWith(Response.status(401).build)
    }
  }

}