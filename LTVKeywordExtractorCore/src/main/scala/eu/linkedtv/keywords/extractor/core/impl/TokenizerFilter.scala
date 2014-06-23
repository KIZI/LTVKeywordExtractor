package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.KELangEnglish
import eu.linkedtv.keywords.extractor.core.KELanguage
import eu.linkedtv.keywords.extractor.core.Token


object TokenizerFilter {
  
  object Sil {
    def isSil(t : Token) = t.word == "sil"
    def unapply(token : Token) = isSil(token)
  }
  
  def filterSuccessor(tokens : List[Token]) = {
    def succf(input : List[Token], output : List[Token]) : List[Token] = input match {
      case token1 :: (token2 : TokenImpl) :: tail => succf((token2 + new Successor(token1)) :: tail, token1 :: output)
      case head :: tail => succf(tail, head :: output)
      case _ => output
    }
    succf(tokens.reverse, Nil)
  }
  
  def filterSentence(lang : KELanguage)(tokens : List[Token]) = {
    def senf(input : List[Token], output : List[Token]) : List[Token] = (lang, input) match {
      case (_, (tk1 @ Occurence(occ1)) :: (tk2 @ Occurence(occ2)) :: tail) if (occ2.start - occ1.end) > 2000 =>
        senf(tk2 + UCFirst :: tail, tk1 + SentenceEnd :: output)
      case (KELangEnglish, (tk1 @ SrtOccurenceEnd()) :: tk2 :: tail) if tk2.word.head.isUpper =>
        senf(tk2 + UCFirst :: tail, tk1 + SentenceEnd :: output)
      case (_, (tk1 @ SentenceEnd()) :: tk2 :: tail) =>
        senf(tk2 + UCFirst :: tail, tk1 :: output)
      case (_, head :: tail) => senf(tail, head :: output)
      case _ => output match {
          case head :: tail => (head + SentenceEnd) :: tail
          case _ => output
        }
    }
    senf(tokens, Nil).reverse match {
      case head :: tail => head + UCFirst :: tail
      case _ => Nil
    }
  }
  
  def filterSil(tokens : List[Token]) = {
    def silf(input : List[Token], output : List[Token]) : List[Token] = input match {
      case Sil() :: tk :: tail => silf(tk + UCFirst :: tail, output)
      case Sil() :: tail => silf(tail, output)
      case tk :: (tail @ (Sil() :: _)) => silf(tail, tk + SentenceEnd :: output)
      case head :: tail => silf(tail, head :: output)
      case _ => output match {
          case head :: tail => (head + SentenceEnd) :: tail
          case _ => output
        }
    }
    silf(tokens, Nil).reverse
  }
  
}