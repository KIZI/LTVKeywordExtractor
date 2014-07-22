package eu.linkedtv.keywords.extractor.core

object Test {

  def main(args:Array[String]) : Unit = {
//    val a = new StlSourceFile("neco", KELangGerman, new FileInputStream("testdata2/Lecorce_du_vent.srt"))
//  a.tokenizer.tokens foreach (x => println(x.word))
//    val teacher = SourceFile.learn(new KeywordExtractorImpl("4330f873ac422453698d888b15c4245c555d62fd"), TokenizerStringBuilderImpl) _
//    val resulter = SourceFile.result(new KeywordExtractorImpl("4330f873ac422453698d888b15c4245c555d62fd")) _
//    
//    val testdir = new File("testdata")
//    val ids = for (file <- testdir.listFiles if file.isFile) yield {
//      val srt = new AsrSourceFile(
//        file.getName,
//        KELangGerman,
//        new AsrTranscript(new FileInputStream(file))
//      )
//      (
//        teacher(srt),
//        file
//      )
//    }
//    for ((id, file) <- ids) {
//      FileUtils.writeStringToFile(
//        new File("result/" + file.getName),
//        resulter(id, new SrtSourceFile(file.getName, KELangEnglish, new FileInputStream(file))).toString,
//        "UTF-8"
//      )
//    }
  }
  
}