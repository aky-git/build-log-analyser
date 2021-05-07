package org.akycorp.build.analyser;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.akycorp.build.db.DBManager;

import javax.sql.DataSource;

/*
 * @author  : ajay_kumar_yadav
 * @created : 05/05/21
 */
public class MainApplication {

    public static void main(String[] args) {
        MainApplication app = new MainApplication();
        app.setUpDB();
        app.start();
    }

    private void setUpDB() {
        HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setDataSourceClassName(driver);
        hikariConfig.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        hikariConfig.setUsername("sa");
        hikariConfig.setJdbcUrl("jdbc:hsqldb:file:db/builddb;shutdown=true\", \"SA\", \"\"");
        hikariConfig.setPassword("");
        DataSource dataSource = new HikariDataSource(hikariConfig);
        DBManager.setDataSource(dataSource);
        //create event table
        DBManager.createDBTables();
    }

    private void start() {
        try {
            BuildLogAnalyser buildLogAnalyser = new BuildLogAnalyser("src/main/resources/sample-log.txt");
            buildLogAnalyser.process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
