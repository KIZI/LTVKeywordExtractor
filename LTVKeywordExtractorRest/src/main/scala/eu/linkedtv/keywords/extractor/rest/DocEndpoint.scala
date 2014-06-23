package eu.linkedtv.keywords.extractor.rest

import javax.servlet.ServletContext
import javax.ws.rs._
import javax.ws.rs.core._
import org.glassfish.jersey.server.mvc.Viewable

@Path("/doc")
class DocEndpoint {

  @Context
  var sc: ServletContext = _

  @GET
  @Produces(Array("application/json"))
  def getHome = new Viewable("/apidoc/main.json");

  @GET
  @Path("/api/v1")
  @Produces(Array("application/json"))
  def getApiV1 = new Viewable("/apidoc/v1.json");

}