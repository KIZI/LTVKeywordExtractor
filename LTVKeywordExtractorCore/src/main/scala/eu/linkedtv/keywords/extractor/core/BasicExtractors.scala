package eu.linkedtv.keywords.extractor.core

object AnyToInt {
  
  def unapply(s : Any) : Option[Int] = s match {
    case AnyToDouble(x) => Some(x.toInt)
    case _ => None
  }
 
}

object AnyToDouble {
  
  def unapply(s : Any) : Option[Double] = try {
    Some(s match {
        case x : Int => x.toDouble
        case x : Double => x
        case x : Float => x.toDouble
        case x : Long => x.toDouble
        case x : Short => x.toDouble
        case x : Byte => x.toDouble
        case x => x.toString.toDouble
      })
  } catch {
    case _ : java.lang.NumberFormatException => None
  }
 
}

object & {
  
  def unapply[A](a: A) = Some(a, a) 
  
}