package finder

import akka.actor.ActorRef
import collection.Iterator
import java.io.File

/**
 * Grep a text file by the @matcher, send the result to the @listener
 *
 * In order to avoid get all file data to memory for large files, can specify the @grepLines
 * By default, it will grep all lines in the file
 *
 * Created by :  Rockie Yang (eyouyan@gmail.com, snowriver.org)
 * Created at :  1/20/13
 */

class TextGreper(listener: ActorRef, matcher: Matcher, grepLines: Int = Int.MaxValue) extends Processor {


  def apply(file: File) {
    try {
      val lines = scala.io.Source.fromFile(file).getLines()

      val matches =
        if (grepLines == Int.MaxValue) lines.toList.filter(x => matcher(x))
        else filter(lines, (line: String) => matcher(line.trim()), grepLines)

      if (!matches.isEmpty)
        listener ! Result(file.getAbsolutePath, matches)
    }
    catch {
      case ex: Exception => println(ex.toString)
    }
  }

  private def filter[T](it: Iterator[T], predicate: T => Boolean, maxFilter: Int = Int.MaxValue): List[T] = {
    val result =
      for {i <- 0 until maxFilter
           if (it.hasNext)
           item = it.next()
           if predicate(item)
      } yield item
    result.toList
  }
}
