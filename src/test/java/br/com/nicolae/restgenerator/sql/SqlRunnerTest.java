package br.com.nicolae.restgenerator.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import br.com.nicolae.restgenerator.generator.exceptions.SqlException;
import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Sql;
import br.com.nicolae.restgenerator.xml.enuns.SGBD;

class SqlRunnerTest {

	private static final String DB_NAME = "testdb";

	@BeforeEach
	void setupDatabase() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:h2:mem:" + DB_NAME + ";DB_CLOSE_DELAY=-1");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setDriverClassName("org.h2.Driver");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("DROP TABLE IF EXISTS users");
		jdbcTemplate.execute("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(255))");
		jdbcTemplate.update("INSERT INTO users (id, name) VALUES (?, ?)", 1, "Alice");
		jdbcTemplate.update("INSERT INTO users (id, name) VALUES (?, ?)", 2, "Bob");
	}

	@BeforeEach
	@AfterEach
	void clearStorage() {
		EnvVariableStorageSingleton.clear();
	}

	@Test
	void shouldExecuteSelectAndStoreVariables() throws SqlException {
		Sql sql = new Sql();
		sql.setSgbd(SGBD.H2);
		sql.setUrl("jdbc:h2:mem:" + DB_NAME + ";DB_CLOSE_DELAY=-1");
		sql.setUserName("sa");
		sql.setPassword("");
		sql.setQuery("SELECT id, name FROM users WHERE id = 1");

		SqlRunner.executeSQL(sql);

		assertEquals("1", EnvVariableStorageSingleton.getInstance().get("ID").getField());
		assertEquals("Alice", EnvVariableStorageSingleton.getInstance().get("NAME").getField());
	}

	@Test
	void shouldRejectUpdateQuery() {
		Sql sql = new Sql();
		sql.setSgbd(SGBD.H2);
		sql.setUrl("jdbc:h2:mem:" + DB_NAME + ";DB_CLOSE_DELAY=-1");
		sql.setUserName("sa");
		sql.setPassword("");
		sql.setQuery("UPDATE users SET name = 'Charlie' WHERE id = 1");

		assertThrows(SqlException.class, () -> SqlRunner.executeSQL(sql));
	}
}
