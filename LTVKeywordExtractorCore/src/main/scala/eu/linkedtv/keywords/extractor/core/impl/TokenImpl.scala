package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.Token
import eu.linkedtv.keywords.extractor.core.TokenAppendix

class TokenImpl(val word : String, val appendix : Set[TokenAppendix]) extends Token {

  def +(ap : TokenAppendix) = new TokenImpl(word, appendix + ap)
  
}
