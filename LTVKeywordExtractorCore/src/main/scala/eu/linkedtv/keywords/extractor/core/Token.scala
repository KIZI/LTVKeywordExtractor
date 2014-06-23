package eu.linkedtv.keywords.extractor.core

trait Token {

  val word : String
  val appendix : Set[TokenAppendix]
  
  def +(ap : TokenAppendix) : Token
  
}

trait TokenAppendix