package com.digirati.taxonomy.manager.lookup.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionProvider {

    private static final Logger logger = LogManager.getLogger(ConnectionProvider.class);

    Connection getConnection() throws SQLException {
        try {
            Properties dbConfigProps = loadDbProperties("db-config.properties");
            String connectionString =
                    "jdbc:"
                            + dbConfigProps.getProperty("db.type")
                            + "://"
                            + dbConfigProps.getProperty("db.host")
                            + ":"
                            + dbConfigProps.getProperty("db.port")
                            + "/"
                            + dbConfigProps.getProperty("db.name");

            Properties properties = loadDbProperties("db-credentials.properties");
            return DriverManager.getConnection(connectionString, properties);
        } catch (SQLException e) {
            logger.error("Unable to connect to database", e);
            throw e;
        }
    }

    private Properties loadDbProperties(String propertiesFileName) {
        try (InputStream dbCredentials =
                getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(dbCredentials);
            return properties;
        } catch (IOException e) {
            logger.error("Unable to load database credentials", e);
            throw new RuntimeException(e);
        }
    }
}
