package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.KeywordOccurence
import eu.linkedtv.keywords.extractor.core.SourceFile
import eu.linkedtv.keywords.extractor.core.Token

trait KeywordOccurencesMapper extends SourceFile {

  lazy val wordMap = tokenizer.tokens groupBy (_.word.toLowerCase)
  
  def keywordOccurences(keyword : String) = { 
    val words = keyword.split(' ').toList
    wordMap.get(words.head.toLowerCase) match {
      case Some(tokens) => 
        (
          tokens map {
            case token @ Occurence(occurStart) => {
                def walkKeyword(currToken : Token, input : List[String]) : Option[KeywordOccurence] = (currToken, input) match {
                  case (Successor(succ), head :: (tail @ (second :: _))) if currToken.word.toLowerCase == head.toLowerCase => walkKeyword(succ.token, tail)
                  case (Occurence(occurEnd), head :: Nil) if currToken.word.toLowerCase == head.toLowerCase => Some(KeywordOccurence(occurStart.start, occurEnd.end))
                  case _ => None
                }
                walkKeyword(token, words)
              }
            case _ => None
          } collect { case Some(x) => x }
        ).toSet
      case _ => Set.empty
    }
  }
  
}