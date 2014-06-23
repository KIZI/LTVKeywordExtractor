package eu.linkedtv.keywords.extractor.core

sealed class KELanguage(val name : String, val id : Int)
object KELangEnglish extends KELanguage("english", 1)
object KELangGerman extends KELanguage("german", 2)
object KELangDutch extends KELanguage("dutch", 3)

object KELanguage {
  def unapply(id : Int) = id match {
    case 1 => Some(KELangEnglish)
    case 2 => Some(KELangGerman)
    case 3 => Some(KELangDutch)
    case _ => None
  }
  def unapply(id : String) = id match {
    case "en" => Some(KELangEnglish)
    case "de" => Some(KELangGerman)
    case "nl" => Some(KELangDutch)
    case _ => None
  }
}

class KEFile(val id : Int, val name : String, val lang : KELanguage)
class KEKeyword(val word : String, val conf : Double)

trait KeywordExtractor {

  def fetchFiles : List[KEFile]
  def fetchKeywords(fileId : Int) : List[KEKeyword] 
  def putFile(fileName : String, lang : KELanguage, content : String) : Int
  def deleteFile(fileId : Int, lang : KELanguage)
  
}