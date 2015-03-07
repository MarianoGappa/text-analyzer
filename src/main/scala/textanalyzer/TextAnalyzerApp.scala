package textanalyzer

import scala.scalajs.js.JSApp
import org.scalajs.dom
import dom.document
import org.scalajs.jquery.jQuery
import scala.collection.JavaConversions._

object TextAnalyzerApp extends JSApp {

  def addChangedMessage(): Unit = {

    val text = jQuery("#text").value().toString

    val analyzer = new TextAnalyzer(text)

    analyzer.run foreach {
      case (name, value: Int) => append(s"""<p><span class="metricName">$name</span><span class="metricValue">$value</span></p>""")
      case (name, value: List[_]) => append(s"""<p><span class="metricName">$name</span><span class="metricValue">${value mkString ", "}</span></p>""")
      case el => append(el.toString)
    }
  }

  def append(s: String) = jQuery("body").append(s)


  def setupUI: Unit = {
    jQuery("#text").keyup(addChangedMessage _)
  }

  def main(): Unit = {
    jQuery(setupUI _)
  }
}

class TextAnalyzer(rawText: String) {
  type WordFrequency = (String, Int)

  lazy val words = (rawText.split(" ").flatMap(_.split("\n")) map normalise).toList.filterNot(_ == "")
  lazy val wordCount = words.length
  lazy val uniqueWords = words.toSet
  lazy val uniqueWordCount = uniqueWords.size
  lazy val wordFrequency = (words groupBy identity mapValues (_.size)).toList.sortWith(sortByFrequencyDesc)
  lazy val uncommonWords = uniqueWords -- wordsByFrequency
  lazy val mostUsedUncommonWords = wordFrequency.take(100).map(_._1).sorted
  lazy val uncommonWordsUsedOnce = wordFrequency.filter(_._2 == 1).toMap.keys.toList.sorted

  def run = {
    Map(
      "Word count" -> wordCount,
      "Unique word count" -> uniqueWordCount,
      "Most used uncommon words" -> mostUsedUncommonWords,
      "Uncommon words used once" -> uncommonWordsUsedOnce
    )
  }

  def normalise(s: String) = s.replaceAll("(?u)[^\\wáéíóúÁÉÍÓÚñÑ]", "").toLowerCase
  def sortByFrequencyDesc: (WordFrequency, WordFrequency) => Boolean = (f1, f2) => f1._2 > f2._2
}
