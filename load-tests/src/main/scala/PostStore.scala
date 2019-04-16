import faunadb.FaunaClient
import faunadb.query._
import faunadb.values.Value

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

object PostStore {
  val ClassName = "posts"
}

class PostStore(client: FaunaClient) {
  import PostStore._

  def create(title: String, tags: Seq[String]): Future[Post] = {
    val result: Future[Value] =
      client.query(
        Select(
          "data",
          Let(
            Seq("id" -> NewId()),
            Create(
              Ref(Class(ClassName), Var("id")),
              Obj("data" -> Obj("id" -> Var("id"), "title" -> title, "tags" -> tags))
            )
          )
        )
      )

    result.flatMap(toPost)

  }

  def read(id: String): Future[Post] = {
    val result: Future[Value] =
      client.query(
        Select(
          "data",
          Get(Ref(Class(ClassName), id))
        )
      )

    result.flatMap(toPost)
  }

  def update(id: String, title: String, tags: Seq[String]): Future[Post] = {
    val result: Future[Value] =
      client.query(
        Select(
          "data",
          Update(
            Ref(Class(ClassName), id),
            Obj("data" -> Obj("title" -> title, "tags" -> tags))
          )
        )
      )

    result.flatMap(toPost)
  }

  def delete(id: String): Future[Post] = {
    val result: Future[Value] =
      client.query(
        Select(
          "data",
          Delete(Ref(Class(ClassName), Value(id)))
        )
      )

    result.flatMap(toPost)
  }

  private def toPost(value: Value): Future[Post] = {
    Future(value.to[Post].get)
  }

}
