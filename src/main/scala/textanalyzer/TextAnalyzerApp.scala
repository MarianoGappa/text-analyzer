package textanalyzer

import scala.scalajs.js.JSApp
import org.scalajs.dom
import dom.document
import org.scalajs.jquery.jQuery
import scala.collection.JavaConversions._

object TextAnalyzerApp extends JSApp {

  def analyze(): Unit = {

    val text = jQuery("#text").value().toString

    jQuery("#results").fadeOut(100)

    val analyzer = new TextAnalyzer(text)

    jQuery("#results").empty

    analyzer.run foreach {
      case (name, value: Int) => append(s"""<p><div class="metricName">$name</div><div class="metricValue">$value</div></p>""")
      case (name, value: List[_]) => append(s"""<p><div class="metricName">$name</div><div class="metricValue">[${value mkString ", "}]</div></p>""")
      case el => append(el.toString)
    }

    jQuery("#results").fadeIn(500)
  }

  def append(s: String) = jQuery("#results").append(s)

  def setupUI: Unit = {
    jQuery("#run").click(analyze _)
  }

  def main(): Unit = {
    jQuery(setupUI _)
  }
}

class TextAnalyzer(rawText: String) {
  type WordFrequency = (String, Int)

  lazy val words = (rawText.split(" ").flatMap(_.split("\n")) map normalise).toList.filterNot(_ == "").filterNot(_.matches("""\d*"""))
  lazy val wordCount = words.length
  lazy val uniqueWords = words.toSet.filterNot(word => word.last == 's' && words.contains(word.init))
  lazy val uniqueWordCount = uniqueWords.size
  lazy val wordFrequency = (words groupBy identity mapValues (_.size)).toList.sortWith(sortByFrequencyDesc)
  lazy val uncommonWords = uniqueWords -- prepositions -- pronouns -- wordsByFrequency
  lazy val uncommonWordsWithFrequency = wordFrequency filter (uncommonWords contains _._1)
  lazy val mostUsedUncommonWords = uncommonWordsWithFrequency.take(100).map(_._1).sorted
  lazy val uncommonWordsUsedOnce = uncommonWordsWithFrequency.filter(_._2 == 1).toMap.keys.toList.sorted

  def run = {
    Map(
      "Word count" -> wordCount,
      "Unique word count" -> uniqueWordCount,
      "Uncommon words used once" -> uncommonWordsUsedOnce,
      "Most used uncommon words" -> mostUsedUncommonWords
    )
  }

  def normalise(s: String) = s.replaceAll("(?u)[^\\wáéíóúÁÉÍÓÚñÑ]", "").toLowerCase
  def sortByFrequencyDesc: (WordFrequency, WordFrequency) => Boolean = (f1, f2) => f1._2 > f2._2
}
