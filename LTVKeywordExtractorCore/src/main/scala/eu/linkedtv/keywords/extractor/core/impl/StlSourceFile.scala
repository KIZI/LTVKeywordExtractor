package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.ResultFile
import eu.linkedtv.keywords.extractor.core.SourceFile
import java.io.ByteArrayInputStream
import subtitleFile.FatalParsingException
import subtitleFile.FormatSTL

object StlSourceFile {

  def unapply(sf: SourceFile): Option[ResultFile] = try {
    new SourceFile(sf.fileName, sf.lang, new FormatSTL().parseFile(sf.fileName, new ByteArrayInputStream(sf.source)).toSRT.mkString("\n").getBytes("UTF-8")) match {
      case SrtSourceFile(rf) =>
        if (rf.tokenizer.tokens.isEmpty)
          None
        else
          Some(rf)
      case _ => None
    }
  } catch {
    case _: FatalParsingException => None
  }

}