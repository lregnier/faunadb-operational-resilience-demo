import com.faunadb.client.FaunaClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jmeter.protocol.java.sampler.JUnitSampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class PostStoreTest {

    private static final String FAUNADB_ENDPOINT_KEY = "faunadb_endpoint";
    private static final String FAUNADB_SECRET_KEY = "faunadb_secret";
    private static final String POST_ID_KEY = "post_id";

    private FaunaClient faunaClient;
    private PostStore postStore;

    @Before
    public void setUp() throws MalformedURLException {
        String faunaEndpoint = getJMeterVariable(FAUNADB_ENDPOINT_KEY);
        String faunaSecret = getJMeterVariable(FAUNADB_SECRET_KEY);

        faunaClient =
            FaunaClient.builder()
                .withEndpoint(faunaEndpoint)
                .withSecret(faunaSecret)
                .build();

        postStore = new PostStore(faunaClient);
    }

    @After
    public void tearDown() {
        faunaClient.close();
    }

    @Test
    public void create() throws InterruptedException, ExecutionException {
        // Fixtures
        String title = randomAlphanumeric(25);
        List<String> tags = Arrays.asList(randomAlphanumeric(5), randomAlphanumeric(5));

        // Run
        Post post = postStore.create(title, tags);

        // Assert
        assertEquals(title, post.getTitle());
        assertEquals(tags, post.getTags());

        // Save Id
        setJMeterVariable(POST_ID_KEY, post.getId());
    }

    @Test
    public void read() throws InterruptedException, ExecutionException {
        // Fixtures
        String id = getJMeterVariable(POST_ID_KEY);

        // Run
        Post post = postStore.read(id);

        // Assert
        assertEquals(id, post.getId());
    }

    @Test
    public void update() throws InterruptedException, ExecutionException {
        // Fixtures
        String id = getJMeterVariable(POST_ID_KEY);
        String title = randomAlphanumeric(25);
        List<String> tags = Arrays.asList(randomAlphanumeric(5), randomAlphanumeric(5));

        // Run
        Post post = postStore.update(id, title, tags);

        // Assert
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(tags, post.getTags());
    }

    @Test
    public void delete() throws InterruptedException, ExecutionException {
        // Fixtures
        String id = getJMeterVariable(POST_ID_KEY);

        // Run
        Post post = postStore.delete(id);

        // Assert
        assertEquals(id, post.getId());
    }

    private String randomAlphanumeric(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    private String getJMeterVariable(String key) {
        JUnitSampler sampler = new JUnitSampler();
        return sampler.getThreadContext().getVariables().get(key);
    }

    public void setJMeterVariable(String key, String value) {
        JUnitSampler sampler = new JUnitSampler();
        JMeterVariables vars = sampler.getThreadContext().getVariables();
        vars.put(key, value);
        sampler.getThreadContext().setVariables(vars);
    }
}
