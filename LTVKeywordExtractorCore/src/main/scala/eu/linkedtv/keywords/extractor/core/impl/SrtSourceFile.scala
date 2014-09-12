package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.AnyToInt
import eu.linkedtv.keywords.extractor.core.ResultFile
import eu.linkedtv.keywords.extractor.core.SourceFile
import eu.linkedtv.keywords.extractor.core.Token
import scala.io.Source

object SrtSourceFile {

  def unapply(sf: SourceFile): Option[ResultFile] = {
    val rf = new ResultFile(
      {
        def findToken(input: Iterator[String], occurence: Option[Occurence], output: List[Token]): List[Token] = {
          if (input.hasNext)
            (input.next, occurence) match {
              case (Time(start, end), None) => findToken(input, Some(new Occurence(start, end)), output)
              case (End(), Some(_)) => output match {
                case head :: tail => findToken(input, None, head + SrtOccurenceEnd :: tail)
                case _ => findToken(input, None, output)
              }
              case (str, Some(occ)) => findToken(input, Some(occ), str.replaceAll("<[^>]+>", "").split(' ').foldLeft(output) {
                case (output, ostr @ Word(str)) => new TokenImpl(
                  str,
                  ostr match {
                    case HasEndPunct() => Set(occ, SentenceEnd)
                    case _ => Set(occ)
                  }) :: output
                case _ => output
              })
              case _ => findToken(input, None, output)
            }
          else
            output.reverse
        }
        new TokenizerImpl(findToken(Source.fromBytes(sf.source, "UTF-8").getLines, None, Nil)) filter TokenizerFilter.filterSentence(sf.lang) filter TokenizerFilter.filterSuccessor
      })
    if (rf.tokenizer.tokens.isEmpty)
      None
    else
      Some(rf)
  }

  object Word {
    def unapply(str: String) = {
      val r = str.replaceAll("""\p{Punct}""", "").trim
      if (!r.isEmpty)
        Some(r)
      else
        None
    }
  }

  object HasEndPunct {
    def unapply(str: String) = {
      if (str.isEmpty)
        false
      else {
        val end = str.last
        if (end == '.' || end == '!' || end == '?')
          true
        else
          false
      }
    }
  }

  object End {
    def unapply(str: String) = str.trim.isEmpty
  }

  object Time {
    def unapply(str: String) = {
      val Pattern = """(\d{2}):(\d{2}):(\d{2}),(\d+) --> (\d{2}):(\d{2}):(\d{2}),(\d+)""".r
      Pattern findFirstIn str match {
        case Some(
          Pattern(
            AnyToInt(sh),
            AnyToInt(sm),
            AnyToInt(ss),
            AnyToInt(sl),
            AnyToInt(eh),
            AnyToInt(em),
            AnyToInt(es),
            AnyToInt(el)
            )
          ) => Some(
          sl + ss * 1000L + sm * 60L * 1000L + sh * 60L * 60L * 1000L,
          el + es * 1000L + em * 60L * 1000L + eh * 60L * 60L * 1000L)
        case _ => None
      }
    }
  }

  object Start {
    def unapply(str: String) = {
      val Pattern = """\d+""".r
      str match {
        case Pattern(AnyToInt(i)) => Some(i)
        case _ => None
      }
    }
  }

}