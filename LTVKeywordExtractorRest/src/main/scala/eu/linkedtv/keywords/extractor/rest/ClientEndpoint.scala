package eu.linkedtv.keywords.extractor.rest

import eu.linkedtv.keywords.extractor.core.KELangEnglish
import eu.linkedtv.keywords.extractor.core.KELanguage
import eu.linkedtv.keywords.extractor.core.SourceFile
import eu.linkedtv.keywords.extractor.core.impl.AsrSourceFile
import eu.linkedtv.keywords.extractor.core.impl.KeywordExtractorImpl
import eu.linkedtv.keywords.extractor.core.impl.SrtSourceFile
import eu.linkedtv.keywords.extractor.core.impl.TokenizerStringBuilderImpl
import eu.linkedtv.utils.asr.AsrTranscript
import java.security.MessageDigest
import javax.ws.rs._
import javax.ws.rs.core._
import org.apache.commons.io.IOUtils
import sun.misc.BASE64Encoder

@Path("/v1/{lang: de|nl|en}")
class ClientEndpoint {

  @HeaderParam("userKey")
  var apiKey: String = _

  @PathParam("lang")
  var _lang: String = _
  lazy val lang = _lang match {
    case KELanguage(x) => x
    case _ => KELangEnglish
  }

  def trainAndGetResult(sourceBuilder: String => SourceFile, source: String) = {
    try {
      import XmlConversions._
      val trainer = SourceFile.learn(new KeywordExtractorImpl(apiKey), TokenizerStringBuilderImpl) _
      val resulter = SourceFile.result(new KeywordExtractorImpl(apiKey)) _
      val sourceName = new BASE64Encoder().encode(MessageDigest.getInstance("SHA-256").digest(source.getBytes("UTF-8")))
      val sourceFile = sourceBuilder(sourceName)
      val sourceId = trainer(sourceFile)
      resulter(sourceId, sourceFile).toXmlString
    } catch {
      case e: Exception => throw new ResponseException(e.getClass.getName, e.getMessage)
    }
  }

  @POST
  @Path("/keywords")
  @Consumes(Array("text/plain"))
  @Produces(Array("application/xml"))
  def postGetKeywordsFromSrt(source: String) = {
    trainAndGetResult(
      x => new SrtSourceFile(
        x + ".srt",
        lang,
        IOUtils.toInputStream(source, "UTF-8")),
      source)
  }

  @POST
  @Path("/keywords")
  @Consumes(Array("application/xml"))
  @Produces(Array("application/xml"))
  def postGetKeywordsFromAsr(source: String) = {
    trainAndGetResult(
      x => new AsrSourceFile(
        x + ".xml",
        lang,
        new AsrTranscript(IOUtils.toInputStream(source, "UTF-8"))),
      source)
  }

}