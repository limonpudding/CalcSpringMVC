package app.pagesLogic;

import app.database.JDBC;
import app.math.Fibonacci;
import app.math.LongArithmethic;
import app.math.LongArithmeticImplList;
import app.math.LongArithmeticMath;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    JDBC jdbc;
    @Autowired
    Logger rootLogger;
    @Autowired
    HttpServletRequest req;

    public ModelAndView build() throws Exception {
        String a = (String) getParams().get("a");
        String b = (String) getParams().get("b");
        String operation = (String) getParams().get("operation");
        if (Integer.parseInt(a) > 50000 && "fib".equals(operation)) {
            rootLogger.warn("Запущена операция фибоначчи с параметром: " + Integer.parseInt(a)+", пользователем с IP: "+req.getRemoteAddr());
        }
        String ans = calc(a, b, operation);

        OperationsHistory operationsHistory = new OperationsHistory();
        HttpSession session = (HttpSession) getParams().get("session");
        operationsHistory.getHistory(session);

        Operation oper = new Operation(new Date(), a, b, operation, ans, UUID.randomUUID().toString());

        jdbc.putDataInBD(oper);

        operationsHistory.addOperation(oper);

        session.getServletContext().setAttribute(session.getId(), operationsHistory.getHistory());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("answer");
        mav.addObject("operationsHistory", operationsHistory.getHistory());
        mav.addObject("answer", ans);
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


}
