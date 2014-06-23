package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.Token
import eu.linkedtv.keywords.extractor.core.Tokenizer

class TokenizerImpl(val tokens : List[Token]) extends Tokenizer {
  
  def filter(f : List[Token] => List[Token]) = new TokenizerImpl(f(tokens))
  
}
