package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static final HikariDataSource dataSource;

    static {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/licencias_database");
        config.setUsername("postgres");
        config.setPassword("postgres");

        config.setMaximumPoolSize(10); //máx. conexiones concurrentes
        config.setMinimumIdle(2); //mínimo de conexiones inactivas

        config.setIdleTimeout(30000); //tiempo que una conexión puede estar inactiva antes de cerrarse
        config.setConnectionTimeout(30000); //tiempo de espera para obtener conexión

        config.setLeakDetectionThreshold(15000); //detecta fugas de conexión

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool(){
        if(dataSource!=null && !dataSource.isClosed()){
            dataSource.close();
            System.out.println("Connection Pool cerrado...");
        }
    }

}
