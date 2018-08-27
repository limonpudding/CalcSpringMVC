package app.database;

import app.pages.logic.Operation;
import app.rest.Constant;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class JDBC {

    @Autowired
    private HttpSession session;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private Logger rootLogger;
    private JdbcTemplate jdbcTemplate;


    JDBC() {
    }

    @PostConstruct
    public void init() {
        System.out.println("JDBCExample postConstruct is called. datasource = " + dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void putConstInDB(Constant constant) {
        final String INSERT_SQL = "insert into CONSTANTS (KEY, VALUE) values (?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL);
            preparedStatement.setString(1, constant.getKey());
            preparedStatement.setString(2, constant.getValue());
            return preparedStatement;
        });
    }

    public void updatePostDB(String key, Constant constant) {
        final String UPDATE_SQL = "update CONSTANTS set CONSTANTS.KEY = ?, CONSTANTS.VALUE = ? WHERE CONSTANTS.KEY = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, constant.getKey());
            preparedStatement.setString(2, constant.getValue());
            preparedStatement.setString(3, key);
            return preparedStatement;
        });
    }

    public void deleteConstantDB(String key) {
        final String DELETE_SQL = "DELETE FROM CONSTANTS WHERE CONSTANTS.KEY = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);
            preparedStatement.setString(1, key);
            return preparedStatement;
        });
    }

    public void updatePatchDB(Constant constant) {
        final String UPDATE_SQL = "update CONSTANTS set CONSTANTS.VALUE = ? WHERE CONSTANTS.KEY = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, constant.getValue());
            preparedStatement.setString(2, constant.getKey());
            return preparedStatement;
        });
    }

    public List<Constant> getConstantsDB() {
        final String SELECT_SQL = "SELECT CONSTANTS.KEY, CONSTANTS.VALUE FROM CONSTANTS";
        List<Constant> dbRows = jdbcTemplate.query(SELECT_SQL,
                (rs, rowNum) -> new Constant(rs.getString("KEY"), rs.getString("VALUE")));
        return dbRows;
    }

    public String getConstantValueDB(String key) {
        final String SELECT_SQL = "SELECT CONSTANTS.VALUE FROM CONSTANTS where CONSTANTS.KEY = '" + key + "'";
        List<String> dbRows = jdbcTemplate.query(SELECT_SQL,
                (rs, rowNum) -> rs.getString(1));
        return dbRows.get(0);
    }

    public void insertSessionTime() {
        final String INSERT_SQL = "insert into SESSIONS (ID, IP, TIMESTART, TIMEEND) values (?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL);
            preparedStatement.setString(1, req.getSession().getId());
            preparedStatement.setString(2, req.getRemoteAddr());
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(req.getSession().getCreationTime()));
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(req.getSession().getCreationTime()));
            return preparedStatement;
        });
        rootLogger.info("В базу даных добалена новая сессия с ID: " + session.getId());
    }

    @Transactional
    public void updateSessionEndTime() {
        final String UPDATE_SQL = "update SESSIONS set TIMEEND = ? where SESSIONS.ID = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setTimestamp(1, new java.sql.Timestamp(session.getLastAccessedTime()));
            preparedStatement.setString(2, session.getId());
            return preparedStatement;
        });
        rootLogger.info("В базе данных обновлена сессия с ID: " + session.getId());
    }

    public void putDataInBD(Operation operation) {

        String OPER_SQL = "insert into " + operation.getOperation() + " (ID, FIRSTOPERAND, SECONDOPERAND, ANSWER, IDSESSION, TIME) values (?,?,?,?,?,?)";
        String FIB_SQL = "insert into " + operation.getOperation() + " (ID, FIRSTOPERAND, ANSWER, IDSESSION, TIME) values (?,?,?,?,?)";
        switch (operation.getOperation()) {
            case "fib":
                jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(FIB_SQL);
                    preparedStatement.setString(1, operation.getIdOperation());
                    preparedStatement.setString(2, operation.getA());
                    preparedStatement.setString(3, operation.getResult());
                    preparedStatement.setString(4, session.getId());
                    preparedStatement.setTimestamp(5, new Timestamp(operation.getDate().getTime()));
                    return preparedStatement;
                });
                break;
            default:
                jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(OPER_SQL);
                    preparedStatement.setString(1, operation.getIdOperation());
                    preparedStatement.setString(2, operation.getA());
                    preparedStatement.setString(3, operation.getB());
                    preparedStatement.setString(4, operation.getResult());
                    preparedStatement.setString(5, session.getId());
                    preparedStatement.setTimestamp(6, new Timestamp(operation.getDate().getTime()));
                    return preparedStatement;
                });
        }
        rootLogger.info("В базу данных была добавлена операция с ID: " + operation.getIdOperation());
    }

    public List<SessionsRow> selectSessionsFromBD(String mode, String order) {
        String orderStr;
        String modeStr;
        if ("desc".equals(order)) {
            orderStr = "desc";
        } else {
            orderStr = "asc";
        }
        if (mode == null)
            mode = "";
        switch (mode) {
            case "idSession":
                modeStr = "ID";
                break;
            case "ip":
                modeStr = "IP";
                break;
            case "timeStart":
                modeStr = "TIMESTART";
                break;
            case "timeEnd":
                modeStr = "TIMEEND";
                break;
            default:
                modeStr = "ID";
        }


        final String SELECT_SQL = "" +
                "select * from (select distinct sessions.id, sessions.ip,sessions.timestart,sessions.timeend, 'false' as operation from SESSIONS left join history on SESSIONS.id = HISTORY.id where operation is null\n" +
                "union all\n" +
                "select distinct sessions.id, sessions.ip,sessions.timestart,sessions.timeend, 'true' as operation from SESSIONS left join history on SESSIONS.id = HISTORY.id where operation is not null) order by " + modeStr + " " + orderStr;
        List<SessionsRow> dbRows = jdbcTemplate.query(SELECT_SQL,
                (rs, rowNum) -> {
                    SessionsRow row = new SessionsRow();
                    row.setId(rs.getString(1));
                    row.setIp(rs.getString(2));
                    row.setSessionStartTime(rs.getString(3));
                    row.setSessionEndTime(rs.getString(4));
                    row.setOperation(rs.getString(5));
                    return row;
                });
        return dbRows;
    }

    public List<DBRow> selectDataFromBD(String mode, String order, String id) {
        String orderStr;
        String modeStr;
        if ("desc".equals(order)) {
            orderStr = "desc";
        } else {
            orderStr = "asc";
        }
        if (mode == null)
            mode = "";
        switch (mode) {
            case "operation":
                modeStr = "OPERATION";
                break;
            case "firstOper":
                modeStr = "FIRSTOPERAND";
                break;
            case "secondOper":
                modeStr = "SECONDOPERAND";
                break;
            case "answer":
                modeStr = "ANSWER";
                break;
            case "time":
                modeStr = "TIME";
                break;
            default:
                modeStr = "TIME";
        }

        final String SELECT_SQL = "SELECT OPERATION, FIRSTOPERAND, SECONDOPERAND, ANSWER, TIME FROM HISTORY where '" + id + "'=ID ORDER BY " + modeStr + " " + orderStr;
        List<DBRow> dbRows = jdbcTemplate.query(SELECT_SQL,
                (rs, rowNum) -> {
                    DBRow row = new DBRow();
                    row.setOperationName(rs.getString("OPERATION"));
                    row.setOp1(rs.getString("FIRSTOPERAND"));
                    row.setOp2(rs.getString("SECONDOPERAND"));
                    row.setAnswer(rs.getString("ANSWER"));
                    row.setTime(rs.getString("TIME"));
                    return row;
                });
        return dbRows;
    }

    public class DBRow {
        private String operationName;
        private String op1;
        private String op2;
        private String answer;
        private String time;

        public void setOperationName(String operationName) {
            this.operationName = operationName;
        }

        public void setOp1(String op1) {
            this.op1 = op1;
        }

        public void setOp2(String op2) {
            this.op2 = op2;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String operationName() {
            return operationName;
        }

        public String op1() {
            return op1;
        }

        public String op2() {
            return op2;
        }

        public String answer() {
            return answer;
        }

        public String time() {
            return time;
        }
    }

    public class SessionsRow {
        private String id;
        private String ip;
        private String sessionStartTime;
        private String sessionEndTime;
        private String operation;

        public void setId(String id) {
            this.id = id;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setSessionStartTime(String sessionStartTime) {
            this.sessionStartTime = sessionStartTime;
        }

        public void setSessionEndTime(String sessionEndTime) {
            this.sessionEndTime = sessionEndTime;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String id() {
            return id;
        }

        public String ip() {
            return ip;
        }

        public String sessionStartTime() {
            return sessionStartTime;
        }

        public String sessionEndTime() {
            return sessionEndTime;
        }

        public String operation() {
            return operation;
        }
    }
}
