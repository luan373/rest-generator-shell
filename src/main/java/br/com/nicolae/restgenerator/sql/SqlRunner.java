package br.com.nicolae.restgenerator.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import br.com.nicolae.restgenerator.generator.exceptions.SqlException;
import br.com.nicolae.restgenerator.generator.exceptions.enums.SqlErrorCode;
import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Field;
import br.com.nicolae.restgenerator.xml.entities.Sql;
import br.com.nicolae.restgenerator.xml.enuns.DataType;
import br.com.nicolae.restgenerator.xml.enuns.SGBD;

public class SqlRunner {

	public static void executeSQL(Sql sql) throws SqlException {
		DataSource dataSource = getDataSource(sql);

		if (sql.getQuery().contains("UPDATE") || sql.getQuery().contains("DELETE")) {
			throw new SqlException("A consulta não pode conter UPDATE ou DELETE.", SqlErrorCode.SQL_QUERY_ERROR);
		}

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.query(sql.getQuery(), (ResultSetExtractor<List<String>>) rs -> {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    EnvVariableStorageSingleton.getInstance().put(rsmd.getColumnName(i),
                            new Field(rs.getString(i), DataType.STRING));
                }

            }
            return null;
        });
	}

	private static DataSource getDataSource(Sql sql) {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUrl(sql.getUrl() != null ? sql.getUrl() : getURL(sql));
		driverManagerDataSource.setUsername(sql.getUserName());
		driverManagerDataSource.setPassword(sql.getPassword());
		driverManagerDataSource.setDriverClassName(getDriverClassName(sql.getSgbd()));

		return driverManagerDataSource;
	}

	private static String getURL(Sql sql) {
		String url;

		switch (sql.getSgbd()) {
		case MYSQL: {
			url = "jdbc:mysql://" + sql.getHost() + ":" + sql.getPort() + "/" + sql.getDatabase();
			break;
		}
		case ORACLE: {
			url = "jdbc:oracle:thin:@" + sql.getHost() + ":" + sql.getPort() + ":" + sql.getDatabase();
			break;
		}
		case POSTGRES: {
			url = "jdbc:postgresql://" + sql.getHost() + ":" + sql.getPort() + "/" + sql.getDatabase();
			break;
		}
		case SQL_SERVER: {
			url = "jdbc:sqlserver://;serverName=" + sql.getHost() + ";port=" + sql.getPort() + ";databaseName="
					+ sql.getDatabase() + ";encrypt=true;trustServerCertificate=true;";
			break;
		}
		case H2: {
			url = "jdbc:h2:mem:" + sql.getDatabase();
			break;
		}
		default:
			throw new IllegalArgumentException("Valor não reconhecido: " + sql.getSgbd()
					+ " Os parâmetros aceitos são: MYSQL, ORACLE, POSTGRES, SQL_SERVER, H2.");
		}

		return url;
	}

	private static String getDriverClassName(SGBD sgbd) {
		String driverClassName;
		switch (sgbd) {
		case MYSQL: {
			driverClassName = "com.mysql.cj.jdbc.Driver";
			break;
		}
		case ORACLE: {
			driverClassName = "oracle.jdbc.driver.OracleDriver";
			break;
		}
		case POSTGRES: {
			driverClassName = "org.postgresql.Driver";
			break;
		}
		case SQL_SERVER: {
			driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			break;
		}
		case H2: {
			driverClassName = "org.h2.Driver";
			break;
		}
		default:
			throw new IllegalArgumentException("Valor não reconhecido: " + sgbd
					+ " Os parâmetros aceitos são: MYSQL, ORACLE, POSTGRES, SQL_SERVER, H2.");
		}

		return driverClassName;
	}

}
