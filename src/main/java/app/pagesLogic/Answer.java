package app.pagesLogic;

import app.math.Fibonacci;
import app.math.LongArithmethic;
import app.math.LongArithmeticImplList;
import app.math.LongArithmeticMath;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Service("getAnswer")
//TODO создать абстрактный класс, наследовать от него.
public class Answer extends Page {
    public ModelAndView build() throws Exception {
        String a =(String) getParams().get("a");
        String b = (String) getParams().get("b");
        String operation = (String) getParams().get("operation");
        String ans = calc(a, b, operation);

        OperationsHistory operationsHistory = new OperationsHistory();
        HttpSession session = (HttpSession)getParams().get("session");
        operationsHistory.getHistory(session);

        Operation oper = new Operation(new Date(), a, b, operation, ans, UUID.randomUUID().toString());

        operationsHistory.addOperation(oper);

        session.getServletContext().setAttribute(session.getId(), operationsHistory.getHistory());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("answer");
        mav.addObject("operationsHistory", operationsHistory.getHistory());
        mav.addObject("answer", ans);
        System.out.println(session.getId());
        return mav;
    }

    public static String calc(String strA, String strB, String operation) throws IOException {
        LongArithmethic res;
        LongArithmethic a = new LongArithmeticImplList();
        LongArithmethic b = new LongArithmeticImplList();
        a.setValue(strA);
        b.setValue(strB);
        switch (operation) {
            case "sum":
                res = LongArithmeticMath.sum(a, b);
                break;
            case "sub":
                res = LongArithmeticMath.sub(a, b);
                break;
            case "mul":
                res = LongArithmeticMath.mul(a, b);
                break;
            case "div":
                res = LongArithmeticMath.div(a, b);
                break;
            case "fib":
                res = new Fibonacci(Integer.parseInt(strA)).number;
                break;
            default:
                throw new IOException("Unexpected operation!");
        }
        return res.toString();
    }

//    private void putDataInBD(Operation operation, HttpServletRequest req) {
//        try (Connection connection = dataBase.getValue().getConnection()) {
//            switch (operation.operation) {
//                case "fib":
//                    PreparedStatement fibonacci = connection.prepareStatement("insert into " + operation.operation + " (ID, FIRSTOPERAND, ANSWER, IDSESSION, TIME) values (?,?,?,?,?)");
//                    fibonacci.setString(1, operation.idOperation);
//                    fibonacci.setString(2, operation.a);
//                    fibonacci.setString(3, operation.result);
//                    fibonacci.setString(4, req.getSession().getId());
//                    fibonacci.setTimestamp(5, new Timestamp(operation.date.getTime()));
//                    fibonacci.executeUpdate();
//                    break;
//                default:
//                    PreparedStatement arithmetic = connection.prepareStatement("insert into " + operation.operation + " (ID, FIRSTOPERAND, SECONDOPERAND, ANSWER, IDSESSION, TIME) values (?,?,?,?,?,?)");
//                    arithmetic.setString(1, operation.idOperation);
//                    arithmetic.setString(2, operation.a);
//                    arithmetic.setString(3, operation.b);
//                    arithmetic.setString(4, operation.result);
//                    arithmetic.setString(5, req.getSession().getId());
//                    arithmetic.setTimestamp(6, new Timestamp(operation.date.getTime()));
//                    arithmetic.executeUpdate();
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
}
