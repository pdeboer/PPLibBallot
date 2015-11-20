import helper.QuestionHTMLFormatter
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {
	  "format questions properly" in new WithApplication {
		  val f = new QuestionHTMLFormatter("test asset://2/3  dddd", "myprefix").format
		  f mustEqual "test myprefix/assetsBallot/2/3  dddd"
	  }

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }
  }
}
