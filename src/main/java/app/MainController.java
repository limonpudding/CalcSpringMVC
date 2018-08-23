package app;

import app.database.JDBC;
import app.pagesLogic.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    AnnotationConfigWebApplicationContext context;
    @Autowired
    HttpServletRequest req;
    @Autowired
    JDBC jdbc;
    @Autowired
    Page getAbout;
    @Autowired
    Page getHome;
    @Autowired
    Page getTables;
    @Autowired
    Page getAnswer;
    @Autowired
    Page getError;
    @Autowired
    Page getCalc;
    @Autowired
    Page getOpHistory;

    private void init(){
        jdbc.updateSessionEndTime(req);
    }

    //TODO привязать через Autowired и Qualifier реализации созданного абстрактного класса для каждого представления свою.

    @RequestMapping(path = "/")
    public ModelAndView getHome() throws Exception {
        init();
        System.out.println(jdbc.toString()+" вот оно");
        return getHome.build();
    }

    @RequestMapping(path = "/calc")
    public ModelAndView getCalc() throws Exception {
        init();
        return getCalc.build();
    }

    @RequestMapping(path = "/ophistory")
    public ModelAndView getOperationHistory() throws Exception {
        init();
        return getOpHistory.build();
    }

    @RequestMapping(path = "/answer")
    public ModelAndView getAnswer(
            @RequestParam(value = "a") String a,
            @RequestParam(value = "b") String b,
            @RequestParam(value = "operation") String operation,
            HttpSession session) throws Exception {
        init();
        //TODO убрать получение экземпляра напрямую из контекста.
        Map<String, Object> params = new HashMap<>();
        params.put("a", a);
        params.put("b", b);
        params.put("operation", operation);
        params.put("session", session);
        getAnswer.setParams(params);
        return getAnswer.build();
    }

    @RequestMapping(path = "/tables")
    public ModelAndView getTables(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "mode") String mode,
            @RequestParam(value = "order") String order,
            @RequestParam(value = "table") String table) throws Exception {
        init();
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("mode", mode);
        params.put("order", order);
        params.put("table", table);
        getTables.setParams(params);
        return getTables.build();
    }

    @RequestMapping(path = "/about")
    public ModelAndView getAbout() throws Exception {
        init();
        return getAbout.build();
    }

    @RequestMapping(path = "/*")
    public ModelAndView getError() throws Exception {
        init();
        return getError.build();
    }
}