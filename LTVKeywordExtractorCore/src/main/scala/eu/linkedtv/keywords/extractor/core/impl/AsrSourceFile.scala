package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.KELanguage
import eu.linkedtv.keywords.extractor.core.SourceFile
import eu.linkedtv.utils.asr.AsrTranscript
import scala.collection.JavaConversions._

class AsrSourceFile(
  val fileName : String,
  val lang : KELanguage,
  val asrt : AsrTranscript
) extends SourceFile with KeywordOccurencesMapper {
 
  lazy val tokenizer = new TokenizerImpl(
    asrt.getTexts.toList map (x =>
      new TokenImpl(x.getWord, Set(new Occurence(x.getTimepoint.toLong, x.getTimepoint + x.getDuration)))
    )
  ) filter TokenizerFilter.filterSil filter TokenizerFilter.filterSuccessor
  
}