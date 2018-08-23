package app.database;

import app.pagesLogic.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JDBC {

    @Autowired
    DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        System.out.println("JDBCExample postConstruct is called. datasource = " + dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insertSessionTime(HttpServletRequest req) {
        final String INSERT_SQL = "insert into SESSIONS (ID, IP, TIMESTART, TIMEEND) values (?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL);
            preparedStatement.setString(1, req.getSession().getId());
            preparedStatement.setString(2, req.getRemoteAddr());
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(req.getSession().getCreationTime()));
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(req.getSession().getCreationTime()));
            return preparedStatement;
        });
    }

    public void updateSessionEndTime(HttpServletRequest req) {
        final String UPDATE_SQL = "update SESSIONS set TIMEEND=? where SESSIONS.ID =?";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setTimestamp(1, new java.sql.Timestamp(req.getSession().getLastAccessedTime()));
            preparedStatement.setString(2, req.getSession().getId());
            return preparedStatement;
        });
    }

    public void putDataInBD(Operation operation, HttpSession session) {
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
    }

    public List<SessionsRow> selectSessionsFromBD(String mode, String order) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = getResultSessionsSet(mode, order, statement);
            return createSessionsList(rs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
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

        final String SELECT_SQL = "SELECT * FROM HISTORY where ?=ID ORDER BY ? ?";
        List<DBRow> dbRows = jdbcTemplate.query(SELECT_SQL, (rs, rowNum) -> {
            DBRow row = new DBRow();
            row.setId(rs.getString(1));
            row.setIp(rs.getString(2));
            row.setSessionStartTime(rs.getString(3));
            row.setSessionEndTime(rs.getString(4));
            row.setOperationName(rs.getString(5));
            row.setOp1(rs.getString(6);
            row.setOp2(rs.getString(7));
            row.setAnswer(rs.getString(8));
            row.setTime(rs.getString(9));
            return row;
        });
        return dbRows;
    }

    private ResultSet getResultSet(String mode, String order, Statement statement, String id) throws SQLException {
        ResultSet rs;

    }

    private ResultSet getResultSessionsSet(String mode, String order, Statement statement) throws SQLException {
        Class a = (org.apache.tiles.extras.complete.CompleteAutoloadTilesListener.class);
        ResultSet rs;
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

        rs = statement.executeQuery("select * from (select distinct sessions.id, sessions.ip,sessions.timestart,sessions.timeend, 'false' as operation from SESSIONS left join history on SESSIONS.id = HISTORY.id where operation is null\n" +
                "union all\n" +
                "select distinct sessions.id, sessions.ip,sessions.timestart,sessions.timeend, 'true' as operation from SESSIONS left join history on SESSIONS.id = HISTORY.id where operation is not null) order by " + modeStr + " " + orderStr);
        return rs;
    }


    private List<SessionsRow> createSessionsList(ResultSet rs) throws SQLException {
        List<SessionsRow> rows = new ArrayList<>();
        while (rs.next()) {
            SessionsRow row = new SessionsRow(rs);
            rows.add(row);
        }
        return rows;
    }

    public class DBRow {
        private String id;
        private String ip;
        private String sessionStartTime;
        private String sessionEndTime;
        private String operationName;
        private String op1;
        private String op2;

        private DBRow(ResultSet rs) throws SQLException {
            id = rs.getString(1);
            ip = rs.getString(2);
            sessionStartTime = rs.getString(3);
            sessionEndTime = rs.getString(4);
            operationName = rs.getString(5);
            op1 = rs.getString(6);
            op2 = rs.getString(7);
            answer = rs.getString(8);
            time = rs.getString(9);
        }

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

        public String answer;
        public String time;

        public DBRow() {
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

        private String operation;

        private SessionsRow(ResultSet rs) throws SQLException {
            id = rs.getString(1);
            ip = rs.getString(2);
            sessionStartTime = rs.getString(3);
            sessionEndTime = rs.getString(4);
            operation = rs.getString(5);
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
