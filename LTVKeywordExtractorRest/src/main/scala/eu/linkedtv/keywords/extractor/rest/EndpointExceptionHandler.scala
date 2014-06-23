package eu.linkedtv.keywords.extractor.rest

import java.util.logging.Level
import java.util.logging.Logger
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class EndpointExceptionHandler extends ExceptionMapper[Throwable] {

  val logger = Logger.getLogger(classOf[EndpointExceptionHandler].getName)

  private def buildError(key: String, mess: String) = {
    import XmlConversions._
    (<error>{ s"$key: $mess" }</error>).toXmlString
  }

  def toResponse(e: Throwable) = {
    e match {
      case ResponseException(x: String, Some(y: String)) => Response.status(400).entity(buildError(x, y)).`type`(MediaType.APPLICATION_XML).build
      case ex: javax.ws.rs.NotFoundException => Response.status(404).build
      case ex: WebApplicationException => ex.getResponse
      case ex => ex.getCause match {
        case ResponseException(x: String, Some(y: String)) => Response.status(400).entity(buildError(x, y)).`type`(MediaType.APPLICATION_XML).build
        case _ => {
          logger.log(Level.WARNING, null, ex)
          Response.status(500).entity(buildError("class", ex.getClass.getSimpleName)).`type`(MediaType.APPLICATION_XML).build
        }
      }
    }
  }

}

class ResponseException(val field: String, message: String) extends Exception(message)

object ResponseException {

  def unapply(e: ResponseException) = Some(e.field, e.getMessage match {
    case null => None
    case "" => None
    case m => Some(m)
  })

}