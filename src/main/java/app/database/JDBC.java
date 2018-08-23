package app.database;

import app.pagesLogic.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    DataSource getDataSource;

    private static final String QUERY = "SELECT * FROM HISTORY";
    private JdbcTemplate jdbcTemplate;
    @PostConstruct
    public void init() {
        System.out.println("JDBCExample postConstruct is called. datasource = " + getDataSource);
        jdbcTemplate = new JdbcTemplate(getDataSource);
    }

    public void updateSessionEndTime(HttpServletRequest req){
        try (Connection connection = getDataSource.getConnection()) {
            PreparedStatement insert = connection.prepareStatement("insert into SESSIONS (ID, IP, TIMESTART, TIMEEND) values (?,?,?,?)");
            PreparedStatement update = connection.prepareStatement("update SESSIONS set TIMEEND=? where SESSIONS.ID =?");

            insert.setString(1, req.getSession().getId());
            insert.setString(2, req.getRemoteAddr());
            insert.setTimestamp(3, new java.sql.Timestamp(req.getSession().getCreationTime()));
            insert.setTimestamp(4, new java.sql.Timestamp(req.getSession().getCreationTime()));

            update.setTimestamp(1, new java.sql.Timestamp(req.getSession().getLastAccessedTime()));
            update.setString(2, req.getSession().getId());

            if (req.getSession().isNew()) {
                insert.executeUpdate();
            } else {
                update.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void putDataInBD(Operation operation, HttpSession session) {
        try (Connection connection = getDataSource.getConnection()) {
            switch (operation.getOperation()) {
                case "fib":
                    PreparedStatement fibonacci = connection.prepareStatement("insert into " + operation.getOperation() + " (ID, FIRSTOPERAND, ANSWER, IDSESSION, TIME) values (?,?,?,?,?)");
                    fibonacci.setString(1, operation.getIdOperation());
                    fibonacci.setString(2, operation.getA());
                    fibonacci.setString(3, operation.getResult());
                    fibonacci.setString(4, session.getId());
                    fibonacci.setTimestamp(5, new Timestamp(operation.getDate().getTime()));
                    fibonacci.executeUpdate();
                    break;
                default:
                    PreparedStatement arithmetic = connection.prepareStatement("insert into " + operation.getOperation()+ " (ID, FIRSTOPERAND, SECONDOPERAND, ANSWER, IDSESSION, TIME) values (?,?,?,?,?,?)");
                    arithmetic.setString(1, operation.getIdOperation());
                    arithmetic.setString(2, operation.getA());
                    arithmetic.setString(3, operation.getB());
                    arithmetic.setString(4, operation.getResult());
                    arithmetic.setString(5, session.getId());
                    arithmetic.setTimestamp(6, new Timestamp(operation.getDate().getTime()));
                    arithmetic.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<SessionsRow> selectSessionsFromBD(String mode, String order) {
        try (Connection connection = getDataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = getResultSessionsSet(mode, order, statement);
            return createSessionsList(rs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<DBRow> selectDataFromBD(String mode, String order, String id) {
        try (Connection connection = getDataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = getResultSet(mode, order, statement, id);
            return createRowList(rs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private ResultSet getResultSet(String mode, String order, Statement statement, String id) throws SQLException {
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

        rs = statement.executeQuery(QUERY + " where '" + id + "'=ID ORDER BY " + modeStr + " " + orderStr);
        return rs;
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

    private List<DBRow> createRowList(ResultSet rs) throws SQLException {
        List<DBRow> rows = new ArrayList<>();
        while (rs.next()) {
            DBRow row = new DBRow(rs);
            rows.add(row);
        }
        return rows;
    }

    public class DBRow {
        public String id;
        public String ip;
        public String sessionStartTime;
        public String sessionEndTime;
        public String operationName;
        public String op1;
        public String op2;
        public String answer;
        public String time;

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
        public String id;
        public String ip;
        public String sessionStartTime;
        public String sessionEndTime;
        public String operation;

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
