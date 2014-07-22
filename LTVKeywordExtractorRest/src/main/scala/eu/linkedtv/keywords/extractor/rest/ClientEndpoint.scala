package eu.linkedtv.keywords.extractor.rest

import eu.linkedtv.keywords.extractor.core.KELangEnglish
import eu.linkedtv.keywords.extractor.core.KELanguage
import eu.linkedtv.keywords.extractor.core.ResultFile
import eu.linkedtv.keywords.extractor.core.SourceFile
import eu.linkedtv.keywords.extractor.core.impl.AsrSourceFile
import eu.linkedtv.keywords.extractor.core.impl.KeywordExtractorImpl
import eu.linkedtv.keywords.extractor.core.impl.MetaSourceFile
import eu.linkedtv.keywords.extractor.core.impl.SrtSourceFile
import eu.linkedtv.keywords.extractor.core.impl.StlSourceFile
import eu.linkedtv.keywords.extractor.core.impl.TokenizerStringBuilderImpl
import java.security.MessageDigest
import javax.ws.rs._
import javax.ws.rs.core._
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

  def trainAndGetResult(sourceBuilder: SourceFile => (ResultFile, String), source: Array[Byte]) = {
    try {
      import XmlConversions._
      val trainer = SourceFile.learn(new KeywordExtractorImpl(apiKey), TokenizerStringBuilderImpl) _
      val resulter = SourceFile.result(new KeywordExtractorImpl(apiKey)) _
      val sourceName = new BASE64Encoder().encode(MessageDigest.getInstance("SHA-256").digest(source))
      val sourceFile = new SourceFile(sourceName, lang, source)
      val (resultFile, suffix) = sourceBuilder(sourceFile)
      val sourceId = trainer(new SourceFile(sourceName + suffix, lang, source), resultFile)
      resulter(sourceId, resultFile).toXmlString
    } catch {
      case e: Exception => throw new ResponseException(e.getClass.getName, e.getMessage)
    }
  }

  @POST
  @Path("/keywords")
  @Consumes(Array("text/plain"))
  @Produces(Array("application/xml"))
  def postGetKeywordsFromSrt(source: Array[Byte]) = {
    trainAndGetResult({
      case StlSourceFile(rs) => rs -> ".stl"
      case SrtSourceFile(rs) => rs -> ".srt"
      case _ => throw new ResponseException("Format", "Bad format. SRT or STL is required!")
    },
      source)
  }

  @POST
  @Path("/keywords")
  @Consumes(Array("application/xml"))
  @Produces(Array("application/xml"))
  def postGetKeywordsFromAsr(source: Array[Byte]) = {
    trainAndGetResult({
      case AsrSourceFile(rs) => rs -> ".asr.xml"
      case MetaSourceFile(rs) => rs -> ".meta.xml"
      case _ => throw new ResponseException("Format", "Bad format. ASR or META is required!")
    },
      source)
  }

}