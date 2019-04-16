import faunadb.values.Codec

case class Post(id: String, title: String, tags: Seq[String])

object Post {
  implicit val codec: Codec[Post] = Codec.Record[Post]
}