package app.pages.logic;

import app.database.JDBC;
import app.math.Fibonacci;
import app.math.LongArithmethic;
import app.math.LongArithmeticImplList;
import app.math.LongArithmeticMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service("getAnswer")
public class Answer extends Page {

    private static Logger logger = LogManager.getLogger(Answer.class);
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
        if (Integer.parseInt(strA) > 50000 && "fib".equals(operation)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Запущена операция фибоначчи с параметром: " + Integer.parseInt(strA));
            }
        }
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
