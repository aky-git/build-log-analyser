package org.akycorp.build.web;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.akycorp.build.analyser.AppSetting;
import org.akycorp.build.db.DBManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/*
 * @author  : ajay_kumar_yadav
 * @created : 06/05/21
 */
@Component
public class AppConfig {

    @PostConstruct
    public void init(){
	// set min build time for no alert to 4
        AppSetting.setMinBuildTimeForFlag(4);
        HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setDataSourceClassName(driver);
        hikariConfig.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        hikariConfig.setUsername("sa");
        hikariConfig.setJdbcUrl("jdbc:hsqldb:file:db/builddb;shutdown=true\", \"SA\", \"\"");
        hikariConfig.setPassword("");
        DataSource dataSource = new HikariDataSource(hikariConfig);
        DBManager.setDataSource(dataSource);
        DBManager.createDBTables();
    }
}
