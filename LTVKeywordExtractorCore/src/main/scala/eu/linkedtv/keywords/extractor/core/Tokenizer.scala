package eu.linkedtv.keywords.extractor.core

trait Tokenizer {
  
  val tokens : List[Token]
  
  def filter(f : List[Token] => List[Token]) : Tokenizer
  
}
