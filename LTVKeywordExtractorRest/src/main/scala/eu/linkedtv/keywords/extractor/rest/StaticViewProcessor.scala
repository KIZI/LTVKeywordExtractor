package eu.linkedtv.keywords.extractor.rest

import java.io.OutputStream
import javax.ws.rs.core.MediaType
import org.glassfish.jersey.server.mvc.Viewable
import org.glassfish.jersey.server.mvc.spi.TemplateProcessor
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.Provider
import javax.servlet.ServletContext
import javax.ws.rs._
import javax.ws.rs.core._

@Provider
class StaticViewProcessor extends TemplateProcessor[String] {

  @Context
  var sc: ServletContext = _

  def resolve(path: String, mediaType: MediaType) = {
    path
  }

  def writeTo(templateReference: String,
    viewable: Viewable,
    mediaType: MediaType,
    mmap: MultivaluedMap[String, Object],
    out: OutputStream) = {
    val source = sc.getResourceAsStream(templateReference)
    try {
      Stream continually (source.read) takeWhile (_ != -1) foreach out.write
    } finally {
      source.close
      out.close
    }
  }

}