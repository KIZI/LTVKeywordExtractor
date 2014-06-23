package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.Token
import eu.linkedtv.keywords.extractor.core.TokenAppendix

object SrtOccurenceEnd extends TokenAppendix {
  def unapply(token : Token) = token.appendix(SrtOccurenceEnd)
}

object SentenceEnd extends TokenAppendix {
  def unapply(token : Token) = token.appendix(SentenceEnd)
}

object UCFirst extends TokenAppendix {
  def unapply(token : Token) = token.appendix(UCFirst)
}

class Successor(val token : Token) extends TokenAppendix

object Successor {
  def unapply(token : Token) = token.appendix find {
    case x : Successor => true
    case _ => false
  } map (_.asInstanceOf[Successor])
}

class Occurence(val start : Long, val end : Long) extends TokenAppendix

object Occurence {
  def unapply(token : Token) = token.appendix find {
    case x : Occurence => true
    case _ => false
  } map (_.asInstanceOf[Occurence])
}