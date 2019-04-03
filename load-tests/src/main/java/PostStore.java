import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.faunadb.client.query.Language.Class;
import static com.faunadb.client.query.Language.*;

public class PostStore {
    private static final String CLASS_NAME = "posts";

    private FaunaClient client;

    public PostStore(FaunaClient client) {
        this.client = client;
    }


    public Post create(String title, List<String> tags) throws InterruptedException, ExecutionException {
        Value result =
            client.query(
                Select(
                    Value("data"),
                    Let("id", NewId()).in(
                        Create(
                            Ref(Class(CLASS_NAME), Var("id")),
                                Obj("data",
                                    Obj("id", Var("id"), "title", Value(title), "tags", Value(tags))
                                )
                        )
                    )
                )
            ).get();

        return toPost(result);
    }

    public Post read(String id) throws InterruptedException, ExecutionException {
        Value result =
            client.query(
                Select(
                    Value("data"),
                    Get(Ref(Class(CLASS_NAME), Value(id)))
                )
            ).get();

        return toPost(result);
    }

    public Post update(String id, String title, List<String> tags) throws InterruptedException, ExecutionException {
        Value result =
            client.query(
                Select(
                    Value("data"),
                    Update(
                        Ref(Class(CLASS_NAME), Value(id)),
                        Obj("data",
                            Obj("title", Value(title), "tags", Value(tags))
                        )
                    )
                )
            ).get();

        return toPost(result);
    }

    public Post delete(String id) throws InterruptedException, ExecutionException {
        Value result =
            client.query(
                Select(
                    Value("data"),
                    Delete(Ref(Class(CLASS_NAME), Value(id)))
                )
            ).get();

        return toPost(result);
    }

    private Post toPost(Value value) {
        return value.to(Post.class).get();
    }
}
