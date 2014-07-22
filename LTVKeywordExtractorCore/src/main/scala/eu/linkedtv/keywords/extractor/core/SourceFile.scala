package eu.linkedtv.keywords.extractor.core

import eu.linkedtv.keywords.extractor.core.impl.Occurence
import eu.linkedtv.keywords.extractor.core.impl.Successor

case class KeywordOccurence(val start : Long, val end : Long)

class SourceFile(val fileName : String, val lang : KELanguage, val source : Array[Byte])

class ResultFile(val tokenizer : Tokenizer) {
  
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

object SourceFile {
  
  def learn(ke : KeywordExtractor, tsb : TokenizerStringBuilder)(src : SourceFile, rsf : ResultFile) = ke.putFile(src.fileName, src.lang, tsb.toString(rsf.tokenizer))
    
  def result(ke : KeywordExtractor)(id : Int, rsf : ResultFile) = 
    <keyword_extraction>
      <keywords>
        {
          ke.fetchKeywords(id) map (kw =>
            <keyword word={kw.word}>
              <occurrences>
                {
                  rsf.keywordOccurences(kw.word) map (oc =>
                    <occurrence start={oc.start.toString} end={oc.end.toString} />
                  )
                }
              </occurrences>
            </keyword>
          )
        }
      </keywords>
    </keyword_extraction>
  
}