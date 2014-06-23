package eu.linkedtv.keywords.extractor.core

case class KeywordOccurence(val start : Long, val end : Long)

trait SourceFile {
  
  val fileName : String
  val lang : KELanguage
  val tokenizer : Tokenizer
  
  def keywordOccurences(keyword : String) : Set[KeywordOccurence]
  
}

object SourceFile {
  
  def learn(ke : KeywordExtractor, tsb : TokenizerStringBuilder)(src : SourceFile) = ke.putFile(src.fileName, src.lang, tsb.toString(src.tokenizer))
    
  def result(ke : KeywordExtractor)(id : Int, src : SourceFile) = 
    <keyword_extraction>
      <keywords>
        {
          ke.fetchKeywords(id) map (kw =>
            <keyword word={kw.word}>
              <occurrences>
                {
                  src.keywordOccurences(kw.word) map (oc =>
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