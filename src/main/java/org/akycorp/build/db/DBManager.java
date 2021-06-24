package org.akycorp.build.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.akycorp.build.model.BuildEvent;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/*
 * @author  : ajay_kumar_yadav
 * @created : 06/05/21
 */
public class DBManager {
    private static DataSource dataSource;
    public static void setDataSource(DataSource dataSource){
        DBManager.dataSource = dataSource;
    }
    public static DataSource getDataSource(){
        return dataSource;
    }

    public static void execute(Consumer<Connection> connectionConsumer){
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connectionConsumer.accept(connection);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(connection!=null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    public static void createDBTables() {
        DBManager.execute(connection -> {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("create table EVENT_TBL (");
                //sb.append("id int not null, ");
                sb.append("EVENT_ID varchar(20) not null, ");
                sb.append("EVENT_TYPE varchar(20), ");
                sb.append("EVENT_HOST varchar(20), ");
                sb.append("EVENT_DURATION numeric, ");
                sb.append("ALERT_FLAG varchar(20) not null, ");
                sb.append("primary key(EVENT_ID)");
                sb.append(");");
                Statement statement = connection.createStatement();
                statement.executeUpdate("DROP TABLE IF EXISTS EVENT_TBL");
                System.out.println(sb.toString());
                statement.executeUpdate(sb.toString());
                System.out.println("table created successfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        });
    }

    public static void saveEvent(BuildEvent event) {
        DBManager.execute(connection -> {
            PreparedStatement statement = null;
            try {
                StringBuilder sb = new StringBuilder("insert into EVENT_TBL values (?, ? ,?, ?, ?)");
                statement = connection.prepareStatement(sb.toString());
                statement.setString(1, event.getEventId());
                statement.setString(2, event.getType());
                statement.setString(3, event.getHost());
                statement.setLong(4, event.getDuration());
                statement.setString(5, event.getAlertFlag());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                if(statement!=null) {
                    try {
                        statement.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    public static ArrayList<Map<String, String>> fetchEvents(int limit) {
        ArrayList<Map<String, String>> resultList = new ArrayList<>();
        DBManager.execute((connection -> {
            Statement statement = null;
            ResultSet result = null;
            try {
                statement = connection.createStatement();
                String query = "SELECT * FROM EVENT_TBL limit";
                if(limit > 0){
                    query = query.concat(" " + limit);
                }
                result = statement.executeQuery(query);
                while(result.next()){
                    Map<String, String> row = new HashMap<>();
                    row.put("eventId", result.getString("EVENT_ID"));
                    row.put("eventType", result.getString("EVENT_TYPE"));
                    row.put("eventHost", result.getString("EVENT_HOST"));
                    row.put("eventDuration", result.getString("EVENT_DURATION"));
                    row.put("alertFlag", result.getString("ALERT_FLAG"));
                    resultList.add(row);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(statement!=null) {
                    try {
                        statement.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }));
        return resultList;
    }
}
