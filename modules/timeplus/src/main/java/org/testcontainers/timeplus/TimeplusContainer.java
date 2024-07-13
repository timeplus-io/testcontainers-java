package org.testcontainers.timeplus;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Testcontainers implementation for Timeplus.
 */
public class TimeplusContainer extends JdbcDatabaseContainer<TimeplusContainer> {

    static final String NAME = "timeplus";

    static final String DOCKER_IMAGE_NAME = "timeplus/timeplusd";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("timeplus/timeplusd:develop");

    private static final Integer HTTP_PORT = 3218;

    private static final Integer NATIVE_PORT = 8463;

    private static final String DRIVER_CLASS_NAME = "com.timeplus.jdbc.TimeplusDriver";

    private static final String JDBC_URL_PREFIX = "jdbc:" + NAME + "://";

    private static final String TEST_QUERY = "SELECT 1";

    private String databaseName = "default";

    private String username = "default";

    private String password = "";

    public TimeplusContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    public TimeplusContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        addExposedPorts(HTTP_PORT, NATIVE_PORT);
        this.waitStrategy =
            Wait.forHttp("/timeplusd/v1/ping").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(1));
    }

    @Override
    protected void configure() {
        withEnv("CLICKHOUSE_DB", this.databaseName);
        withEnv("CLICKHOUSE_USER", this.username);
        withEnv("CLICKHOUSE_PASSWORD", this.password);
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return new HashSet<>(getMappedPort(HTTP_PORT));
    }

    @Override
    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    @Override
    public String getJdbcUrl() {
        return (
            JDBC_URL_PREFIX +
            getHost() +
            ":" +
            getMappedPort(NATIVE_PORT) +
            "/" +
            this.databaseName +
            constructUrlParameters("?", "&")
        );
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getTestQueryString() {
        return TEST_QUERY;
    }

    @Override
    public TimeplusContainer withUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public TimeplusContainer withPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public TimeplusContainer withDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }
}
