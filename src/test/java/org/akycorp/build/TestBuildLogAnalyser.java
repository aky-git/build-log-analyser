package org.akycorp.build;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.akycorp.build.analyser.AppSetting;
import org.akycorp.build.analyser.BuildLogAnalyser;
import org.akycorp.build.db.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author  : ajay_kumar_yadav
 * @created : 07/05/21
 */
public class TestBuildLogAnalyser {
    @Before
    public void setUpDB(){
	AppSetting.setMinBuildTimeForFlag(4);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        hikariConfig.setUsername("sa");
        hikariConfig.setJdbcUrl("jdbc:hsqldb:file:db/testdb;shutdown=true\", \"SA\", \"\"");
        hikariConfig.setPassword("");
        DataSource dataSource = new HikariDataSource(hikariConfig);
        DBManager.setDataSource(dataSource);
        DBManager.createDBTables();
    }

    @Test(expected = NoSuchFileException.class)
    public void testProcess_FileNotFound() throws Exception {
        BuildLogAnalyser buildLogAnalyser = new BuildLogAnalyser("someransomm_file.txt");
        buildLogAnalyser.process();
    }

    @Test
    public void testProcess_EventPresent_WithAlert() throws Exception {
        System.out.println(Paths.get(".").toAbsolutePath().toString());
        BuildLogAnalyser buildLogAnalyser = new BuildLogAnalyser("src/test/resources/test-sample-log.txt");
        buildLogAnalyser.process();
        ArrayList<Map<String, String>> result = DBManager.fetchEvents(10);
        Assert.assertTrue(result.size() == 3);

        List<Map<String, String>> alertList = result.stream().map(m -> m.entrySet().stream()
                .filter(map -> "alertFlag".equals(map.getKey()) && "true".equals(map.getValue()))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())))
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());
        Assert.assertTrue(alertList.size() == 2);
    }

    @Test
    public void testProcess_EventPresent_NoAlert() throws Exception {
        System.out.println(Paths.get(".").toAbsolutePath().toString());
        BuildLogAnalyser buildLogAnalyser = new BuildLogAnalyser("src/test/resources/test-sample-no-alert-log.txt");
        buildLogAnalyser.process();
        ArrayList<Map<String, String>> result = DBManager.fetchEvents(10);

        List<Map<String, String>> alertList = result.stream().map(m -> m.entrySet().stream()
                .filter(map -> "alertFlag".equals(map.getKey()) && "true".equals(map.getValue()))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())))
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());
        System.out.println(alertList);
        Assert.assertTrue(alertList.size() == 0);
    }
}
