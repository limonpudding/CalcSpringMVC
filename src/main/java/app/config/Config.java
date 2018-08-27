package app.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@EnableTransactionManagement
@Configuration
public class Config extends WebMvcConfigurerAdapter {

    @Autowired
    ServletContext context;


    @Bean
    @Autowired
    public DataSourceTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    Logger rootLogger(){
        return LogManager.getRootLogger();
    }

    @Bean
    DataSource getDataSource() throws NamingException, SQLException {
        String dbName = context.getInitParameter("dbName");
        DataSource dataSource = (DataSource) new  InitialContext().lookup("java:comp/env/" + dbName);
        if (dataSource.getConnection().getMetaData().getDatabaseProductName().toUpperCase().contains("H2")) {

            try (Connection connection = dataSource.getConnection()) {
                Statement statement = connection.createStatement();
                statement.execute("" +
                        "create table CONSTANTS\n" +
                        "(\n" +
                        "  KEY            NVARCHAR2(40) default NULL not null\n" +
                        "    primary key,\n" +
                        "  VALUE  CLOB" +
                        ");\n" +
                        "create table DIV\n" +
                        "(\n" +
                        "  ID            NVARCHAR2(40) default NULL not null\n" +
                        "    primary key,\n" +
                        "  FIRSTOPERAND  CLOB,\n" +
                        "  SECONDOPERAND CLOB,\n" +
                        "  ANSWER        CLOB,\n" +
                        "  IDSESSION     NVARCHAR2(40) default NULL,\n" +
                        "  TIME          TIMESTAMP\n" +
                        ");\n" +
                        "\n" +
                        "create table FIB\n" +
                        "(\n" +
                        "  ID           NVARCHAR2(40) default NULL not null\n" +
                        "    primary key,\n" +
                        "  FIRSTOPERAND CLOB,\n" +
                        "  ANSWER       CLOB,\n" +
                        "  IDSESSION    NVARCHAR2(40) default NULL,\n" +
                        "  TIME         TIMESTAMP\n" +
                        ");\n" +
                        "create table MUL\n" +
                        "(\n" +
                        "  ID            NVARCHAR2(40) default NULL not null\n" +
                        "    primary key,\n" +
                        "  FIRSTOPERAND  CLOB,\n" +
                        "  SECONDOPERAND CLOB,\n" +
                        "  ANSWER        CLOB,\n" +
                        "  IDSESSION     NVARCHAR2(40) default NULL,\n" +
                        "  TIME          TIMESTAMP\n" +
                        ");\n" +
                        "create table SESSIONS\n" +
                        "(\n" +
                        "  ID        NVARCHAR2(40) default NULL not null\n" +
                        "    primary key,\n" +
                        "  IP        NVARCHAR2(25),\n" +
                        "  TIMESTART TIMESTAMP,\n" +
                        "  TIMEEND   TIMESTAMP\n" +
                        ");\n" +
                        "create table SUB\n" +
                        "(\n" +
                        "  ID            NVARCHAR2(40) default NULL not null\n" +
                        "    primary key,\n" +
                        "  FIRSTOPERAND  CLOB,\n" +
                        "  SECONDOPERAND CLOB,\n" +
                        "  ANSWER        CLOB,\n" +
                        "  IDSESSION     NVARCHAR2(40) default NULL,\n" +
                        "  TIME          TIMESTAMP\n" +
                        ");\n" +
                        "create table SUM\n" +
                        "(\n" +
                        "  ID            NVARCHAR2(40) default NULL not null\n" +
                        "    primary key,\n" +
                        "  FIRSTOPERAND  CLOB,\n" +
                        "  SECONDOPERAND CLOB,\n" +
                        "  ANSWER        CLOB,\n" +
                        "  IDSESSION     NVARCHAR2(40) default NULL,\n" +
                        "  TIME          TIMESTAMP\n" +
                        ");\n" +
                        "\n" +
                        "\n" +
                        "create view HISTORY as\n" +
                        "  SELECT\n" +
                        "    SESSIONS.id,\n" +
                        "    SESSIONS.ip,\n" +
                        "    SESSIONS.TIMESTART,\n" +
                        "    SESSIONS.TIMEEND,\n" +
                        "    'division' as operation,\n" +
                        "    DIV.FIRSTOPERAND,\n" +
                        "    DIV.SECONDOPERAND,\n" +
                        "    DIV.ANSWER,\n" +
                        "    DIV.TIME\n" +
                        "  FROM DIV\n" +
                        "    join SESSIONS on DIV.IDSESSION = SESSIONS.ID\n" +
                        "  union all\n" +
                        "  SELECT\n" +
                        "    SESSIONS.id,\n" +
                        "    SESSIONS.ip,\n" +
                        "    SESSIONS.TIMESTART,\n" +
                        "    SESSIONS.TIMEEND,\n" +
                        "    'multiply' as operation,\n" +
                        "    MUL.FIRSTOPERAND,\n" +
                        "    MUL.SECONDOPERAND,\n" +
                        "    MUL.ANSWER,\n" +
                        "    MUL.TIME\n" +
                        "  FROM MUL\n" +
                        "    join SESSIONS on MUL.IDSESSION = SESSIONS.ID\n" +
                        "  union all\n" +
                        "  SELECT\n" +
                        "    SESSIONS.id,\n" +
                        "    SESSIONS.ip,\n" +
                        "    SESSIONS.TIMESTART,\n" +
                        "    SESSIONS.TIMEEND,\n" +
                        "    'substraction' as operation,\n" +
                        "    SUB.FIRSTOPERAND,\n" +
                        "    SUB.SECONDOPERAND,\n" +
                        "    SUB.ANSWER,\n" +
                        "    SUB.TIME\n" +
                        "  FROM SUB\n" +
                        "    join SESSIONS on SUB.IDSESSION = SESSIONS.ID\n" +
                        "  union all\n" +
                        "  SELECT\n" +
                        "    SESSIONS.id,\n" +
                        "    SESSIONS.ip,\n" +
                        "    SESSIONS.TIMESTART,\n" +
                        "    SESSIONS.TIMEEND,\n" +
                        "    'summation' as operation,\n" +
                        "    SUM.FIRSTOPERAND,\n" +
                        "    SUM.SECONDOPERAND,\n" +
                        "    SUM.ANSWER,\n" +
                        "    SUM.TIME\n" +
                        "  FROM SUM\n" +
                        "    join SESSIONS on SUM.IDSESSION = SESSIONS.ID\n" +
                        "  union all\n" +
                        "  SELECT\n" +
                        "    SESSIONS.id,\n" +
                        "    SESSIONS.ip,\n" +
                        "    SESSIONS.TIMESTART,\n" +
                        "    SESSIONS.TIMEEND,\n" +
                        "    'fibonacci' as operation,\n" +
                        "    FIB.FIRSTOPERAND,\n" +
                        "    null        as SECONDOPERAND,\n" +
                        "    FIB.ANSWER,\n" +
                        "    FIB.TIME\n" +
                        "  FROM FIB\n" +
                        "    join SESSIONS on FIB.IDSESSION = SESSIONS.ID\n"
                );

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return dataSource;
        } else if (dataSource.getConnection().getMetaData().getDatabaseProductName().toUpperCase().contains("ORACLE")) {
            return dataSource;
        } else {
            return null;
        }
    }

}