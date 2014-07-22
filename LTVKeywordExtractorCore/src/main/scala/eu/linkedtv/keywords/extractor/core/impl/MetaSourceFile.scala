package eu.linkedtv.keywords.extractor.core.impl

import eu.linkedtv.keywords.extractor.core.ResultFile
import eu.linkedtv.keywords.extractor.core.SourceFile
import eu.linkedtv.keywords.extractor.core.impl.SrtSourceFile.HasEndPunct
import eu.linkedtv.keywords.extractor.core.impl.SrtSourceFile.Word
import java.io.ByteArrayInputStream
import scala.xml.XML
import eu.linkedtv.keywords.extractor.core.&

object MetaSourceFile {

  def unapply(sf: SourceFile): Option[ResultFile] = {
    val xml = XML.load(new ByteArrayInputStream(sf.source))
    val Duration = """duration: .*?(\d+).*""".r
    val o = xml \\ "format" find (x => x.text.startsWith("duration:")) map (_.text) match {
      case Some(Duration(d)) => new Occurence(0, d.toLong)
      case _ => new Occurence(0, 0)
    }
    val rf = new ResultFile(
      new TokenizerImpl(
        (xml \\ "description" map (_.text) mkString ". ")
          .trim
          .replaceAll("""\s+""", " ")
          .replaceAll("""\.+""", ".")
          .split(' ')
          .toList collect {
            case HasEndPunct() & Word(w) => new TokenImpl(w, Set(o, SentenceEnd))
            case Word(w) => new TokenImpl(w, Set(o))
          }) filter TokenizerFilter.filterSuccessor)
    if (rf.tokenizer.tokens.isEmpty)
      None
    else
      Some(rf)
  }

}