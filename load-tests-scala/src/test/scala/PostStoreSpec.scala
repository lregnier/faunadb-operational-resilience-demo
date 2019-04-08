import faunadb.FaunaClient
import org.apache.jmeter.protocol.java.sampler.JUnitSampler
import org.junit.{After, Before, Test}
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitSuite

import scala.util.Random

object PostStoreSpec {
  private val FaunaDbEndpointKey = "faunadb_endpoint"
  private val FaunaDbSecretKey = "faunadb_secret"
  private val PostIdKey = "post_id"
  private val LastTxnTimeKey = "last_txn_time"
}

class PostStoreSpec extends JUnitSuite with Matchers with ScalaFutures {
  import PostStoreSpec._

  private var faunaClient: FaunaClient = _
  private var postStore: PostStore = _

  @Before
  def setUp() {
    val faunaEndpoint = getJMeterVariable(FaunaDbEndpointKey)
    val faunaSecret = getJMeterVariable(FaunaDbSecretKey)
    val lastTxnTime = getJMeterVariable(LastTxnTimeKey)

    faunaClient = FaunaClient(faunaSecret, faunaEndpoint)
    faunaClient.syncLastTxnTime(lastTxnTime.toLong)

    postStore = new PostStore(faunaClient)
  }

  @After
  def tearDown(): Unit = {
    val lastTxnTime = faunaClient.lastTxnTime
    setJMeterVariable(LastTxnTimeKey, lastTxnTime.toString)
    faunaClient.close()
  }

  @Test
  def create(): Unit = {
    // Fixtures
    val title = randomAlphanumeric(25)
    val tags = Seq(randomAlphanumeric(5), randomAlphanumeric(5))

    // Run
    val post = postStore.create(title, tags).futureValue

    // Assert
    post.title shouldBe title
    post.tags shouldBe tags

    // Save Id
    setJMeterVariable(PostIdKey, post.id)
  }

  @Test
  def read(): Unit = {
    // Fixtures
    val id = getJMeterVariable(PostIdKey)

    // Run
    val post = postStore.read(id).futureValue

    // Assert
    post.id shouldBe id
  }

  @Test
  def update(): Unit = {
    // Fixtures
    val id = getJMeterVariable(PostIdKey)
    val title = randomAlphanumeric(25)
    val tags = Seq(randomAlphanumeric(5), randomAlphanumeric(5))

    // Run
    val post = postStore.update(id, title, tags).futureValue

    // Assert
    post.id shouldBe id
    post.title shouldBe title
    post.tags shouldBe tags
  }

  @Test
  def delete(): Unit = {
    // Fixtures
    val id = getJMeterVariable(PostIdKey)

    // Run
    val post = postStore.delete(id).futureValue

    // Assert
    post.id shouldBe id
  }

  private def getJMeterVariable(key: String): String = {
    val sampler = new JUnitSampler
    sampler.getThreadContext.getVariables.get(key)
  }

  private def setJMeterVariable(key: String, value: String): Unit = {
    val sampler = new JUnitSampler
    val vars = sampler.getThreadContext.getVariables
    vars.put(key, value)
    sampler.getThreadContext.setVariables(vars)
  }

  private def randomAlphanumeric(count: Int): String =
    Random.alphanumeric.take(count).mkString

}
