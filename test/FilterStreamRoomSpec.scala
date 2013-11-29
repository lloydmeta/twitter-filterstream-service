import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import models.FilterStreamRoom
import play.api.test.WithApplication
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class FilterStreamRoomSpec

  extends Specification  {

  ".currentTerms" should {

    "should return an empty list in a future by default" in new WithApplication {
      FilterStreamRoom.currentTerms map (_ must be(Nil))
    }

    "should return a list that was sent previously via newTerms" in new WithApplication {
      val newTerms = List("app", "terms")
      FilterStreamRoom.newTerms(newTerms)
      FilterStreamRoom.currentTerms map (_ must be(newTerms))
    }

  }

}
