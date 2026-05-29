package pl.arekbednarz.coupons.utils;

import org.jboss.logging.Logger;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static pl.arekbednarz.coupons.utils.TestContainerNetworks.NETWORK;


@SuppressWarnings("ALL")
public class PostgresqlTestContainer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger LOG = Logger.getLogger(PostgresqlTestContainer.class);

	private static final String USERNAME = "postgres";
	private static final String PASSWORD = "secret";
	private static final String NETWORK_ALIAS = "postgres.docker";
	private static final String DATABASE_NAME = "coupons";

	private static final PostgreSQLContainer<?> CONTAINER =
		new PostgreSQLContainer<>("postgres:16-alpine")
			.withUsername(USERNAME)
			.withPassword(PASSWORD)
			.withNetwork(NETWORK)
			.withDatabaseName(DATABASE_NAME)
			.withNetworkAliases(NETWORK_ALIAS)
			.withExposedPorts(5432)
			.waitingFor(Wait.forListeningPort())
			.withReuse(false);

	static {
		LOG.info("Starting PostgreSQL container, this may take a while...");
		CONTAINER.start();
		LOG.info("STARTED PostgreSQL container");
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {

		var username = CONTAINER.getUsername();
		var password = CONTAINER.getPassword();
		var url = CONTAINER.getJdbcUrl();
		var host = CONTAINER.getHost();
		var port = CONTAINER.getMappedPort(5432);

		TestPropertyValues.of(
			"spring.datasource.url=" + url,
			"spring.datasource.username=" + username,
			"spring.datasource.password=" + password,
			"spring.jpa.hibernate.ddl-auto=create-drop",
			"spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect")
			.applyTo(applicationContext.getEnvironment());
	}
}
