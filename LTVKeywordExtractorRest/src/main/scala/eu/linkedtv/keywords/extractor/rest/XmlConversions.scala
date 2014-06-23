package eu.linkedtv.keywords.extractor.rest

import scala.xml.PrettyPrinter
import scala.language.implicitConversions

object XmlConversions {

  implicit def XmlToXmlPrinter(elem: scala.xml.Elem) = new XmlPrinter(elem)

}

class XmlPrinter(elem: scala.xml.Elem) {

  def toXmlString = """<?xml version="1.0" encoding="UTF-8"?>""" + "\n" + new PrettyPrinter(80, 4).format(elem)

}
