import faunadb.FaunaClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jmeter.protocol.java.sampler.JUnitSampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class PostStoreTest {

    private static final String FAUNADB_ENDPOINT_KEY = "faunadb_endpoint";
    private static final String FAUNADB_SECRET_KEY = "faunadb_secret";
    private static final String LAST_TXN_TIME_KEY = "last_txn_time_key";
    private static final String POST_ID_KEY = "post_id";

    private static final Duration DEFAULT_TIMEOUT = Duration.create(15, TimeUnit.SECONDS);

    private FaunaClient faunaClient;
    private PostStore postStore;

    @Before
    public void setUp() {
        String faunaEndpoint = getJMeterVariable(FAUNADB_ENDPOINT_KEY);
        String faunaSecret = getJMeterVariable(FAUNADB_SECRET_KEY);
        String lastTxnTime = getJMeterVariable(LAST_TXN_TIME_KEY);

        faunaClient = FaunaClient.apply(faunaSecret, faunaEndpoint, null, null);
        if(lastTxnTime != null) {
            faunaClient.syncLastTxnTime(Long.parseLong(lastTxnTime));
        }

        postStore = new PostStore(faunaClient);
    }

    @After
    public void tearDown() {
        Long lastTxnTime = faunaClient.lastTxnTime();
        setJMeterVariable(LAST_TXN_TIME_KEY, lastTxnTime.toString());
        faunaClient.close();
    }

    @Test
    public void create() throws Exception {
        // Fixtures
        String title = randomAlphanumeric(25);
        Seq<String> tags = buildSeq(randomAlphanumeric(5), randomAlphanumeric(5));

        // Run
        Post post = await(postStore.create(title, tags));

        // Assert
        assertEquals(title, post.title());
        assertEquals(tags, post.tags());

        // Save Id
        setJMeterVariable(POST_ID_KEY, post.id());
    }

    @Test
    public void read() throws Exception {
        // Fixtures
        String id = getJMeterVariable(POST_ID_KEY);

        // Run
        Post post = await(postStore.read(id));

        // Assert
        assertEquals(id, post.id());
    }

    @Test
    public void update() throws Exception {
        // Fixtures
        String id = getJMeterVariable(POST_ID_KEY);
        String title = randomAlphanumeric(25);
        Seq<String> tags = buildSeq(randomAlphanumeric(5), randomAlphanumeric(5));

        // Run
        Post post = await(postStore.update(id, title, tags));

        // Assert
        assertEquals(id, post.id());
        assertEquals(title, post.title());
        assertEquals(tags, post.tags());
    }

    @Test
    public void delete() throws Exception {
        // Fixtures
        String id = getJMeterVariable(POST_ID_KEY);

        // Run
        Post post = await(postStore.delete(id));

        // Assert
        assertEquals(id, post.id());
    }

    private String randomAlphanumeric(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    private String getJMeterVariable(String key) {
        JUnitSampler sampler = new JUnitSampler();
        return sampler.getThreadContext().getVariables().get(key);
    }

    private void setJMeterVariable(String key, String value) {
        JUnitSampler sampler = new JUnitSampler();
        JMeterVariables vars = sampler.getThreadContext().getVariables();
        vars.put(key, value);
        sampler.getThreadContext().setVariables(vars);
    }

    private <T> Seq<T> buildSeq(T... a) {
        List<T> list = Arrays.asList(a);
        return JavaConverters.collectionAsScalaIterableConverter(list).asScala().toSeq();
    }

    private <T> T await(Future<T> future) throws Exception {
        return Await.result(future, DEFAULT_TIMEOUT);
    }

}
