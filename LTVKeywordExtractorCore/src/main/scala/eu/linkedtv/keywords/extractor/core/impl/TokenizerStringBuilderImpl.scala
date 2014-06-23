package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.Tokenizer
import eu.linkedtv.keywords.extractor.core.TokenizerStringBuilder

object TokenizerStringBuilderImpl extends TokenizerStringBuilder {

  def toString(tokenizer : Tokenizer) = tokenizer.tokens.foldLeft("")((r, t) =>
    r + ' ' + t.appendix.foldLeft(t.word){
      case (r, SentenceEnd) => r + '.'
      case (r, UCFirst) => r.capitalize
      case (r, _) => r
    }
  ).drop(1)
  
}