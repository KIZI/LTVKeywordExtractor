package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.ResultFile
import eu.linkedtv.keywords.extractor.core.SourceFile
import eu.linkedtv.utils.asr.AsrException
import eu.linkedtv.utils.asr.AsrTranscript
import java.io.ByteArrayInputStream
import scala.collection.JavaConversions._

object AsrSourceFile {

  def unapply(sf: SourceFile): Option[ResultFile] = try {
    val rf = new ResultFile(
      new TokenizerImpl(
        new AsrTranscript(new ByteArrayInputStream(sf.source)).getTexts.toList map (x =>
          new TokenImpl(x.getWord, Set(new Occurence(x.getTimepoint.toLong, x.getTimepoint + x.getDuration))))) filter TokenizerFilter.filterSil filter TokenizerFilter.filterSuccessor)
    if (rf.tokenizer.tokens.isEmpty)
      None
    else
      Some(rf)
  } catch {
    case _: AsrException => None
  }

}