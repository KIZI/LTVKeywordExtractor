package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.AnyToDouble
import eu.linkedtv.keywords.extractor.core.AnyToInt
import eu.linkedtv.keywords.extractor.core.KEFile
import eu.linkedtv.keywords.extractor.core.KEKeyword
import eu.linkedtv.keywords.extractor.core.KELanguage
import eu.linkedtv.keywords.extractor.core.KeywordExtractor
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Form
import javax.ws.rs.core.MediaType
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONArray
import scala.util.parsing.json.JSONObject

class KeywordExtractorParseException(msg : String) extends Exception(msg)

class KeywordExtractorImpl(accessKey : String) extends KeywordExtractor {

//  private val userName = "test"
//  private val password = "heslo"
  private val endpointUrl = "http://localhost:8088/LTVKeywords/rest/v2"
//    private val endpointUrl = "http://localhost:8080/rest/v2"
  private val endpoint = ClientBuilder.newClient.target(endpointUrl)
//  private lazy val accessKey = endpoint
//  .path("user")
//  .request
//  .header("userName", userName)
//  .header("password", password)
//  .get(classOf[String])
  
  private def auth(eb : javax.ws.rs.client.Invocation.Builder) = eb.header("userKey", accessKey)
  
  def fetchFiles = JSON.parseRaw(auth(endpoint.request).get(classOf[String])) match {
    case Some(files : JSONArray) => files.list map {
        case file : JSONObject => (file.obj.get("fileId"), file.obj.get("name"), file.obj.get("languageId")) match {
            case (
                Some(AnyToInt(id)),
                Some(name : String),
                Some(AnyToInt(KELanguage(lang)))
              ) => new KEFile(id, name, lang)
            case _ => throw new KeywordExtractorParseException("Bad file data.")
          }
        case _ => throw new KeywordExtractorParseException("Bad file type (must be JSONObject).")
      }
    case _ => throw new KeywordExtractorParseException("Bad response type (must be JSONArray).")
  }
  
  def fetchKeywords(fileId : Int) = JSON.parseRaw(auth(endpoint.path(fileId.toString).request).get(classOf[String])) match {
    case Some(resp : JSONObject) => resp.obj.get("keywords") match {
        case Some(keywords : JSONArray) => keywords.list map {
            case keyword : JSONObject => (keyword.obj.get("word"), keyword.obj.get("confidence")) match {
                case (Some(word : String), Some(AnyToDouble(conf))) => new KEKeyword(word, conf)
                case _ => throw new KeywordExtractorParseException("Bad keyword data.")
              }
            case _ => throw new KeywordExtractorParseException("Bad keyword type (must be JSONObject).")
          }
        case _ => throw new KeywordExtractorParseException("Bad keywords type (must be JSONArray).")
      }
    case _ => throw new KeywordExtractorParseException("Bad response type (must be JSONObject).")
  }
  
  def putFile(fileName : String, lang : KELanguage, content : String) = {
    val form = new Form;
    form.param("fileName", fileName);
    form.param("fileText", content);
    JSON.parseRaw(
      auth(endpoint.path(lang.name).request).post(
        Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
        classOf[String]
      )
    ) match {
      case Some(resp : JSONObject) => resp.obj.get("fileId") match {
          case Some(AnyToInt(id)) => id
          case _ => throw new KeywordExtractorParseException("Bad id data.")
        }
      case _ => throw new KeywordExtractorParseException("Bad response type (must be JSONObject).")
    }
  }
    
  def deleteFile(fileId : Int, lang : KELanguage) = auth(endpoint.path(lang.name + "/" + fileId.toString).request).async.delete
  
}
