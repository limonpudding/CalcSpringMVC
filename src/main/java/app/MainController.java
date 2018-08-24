package app;

import app.database.JDBC;
import app.pagesLogic.Answer;
import app.pagesLogic.Operation;
import app.pagesLogic.Page;
import app.pagesLogic.RESTParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {

    private final HttpServletRequest req;
    private final JDBC jdbc;
    private final Page getAbout;
    private final Page getHome;
    private final Page getTables;
    private final Page getAnswer;
    private final Page getError;
    private final Page getCalc;
    private final Page getOpHistory;
    private final Logger rootLogger;

    @Autowired
    public MainController(HttpServletRequest req, JDBC jdbc, Page getAbout, Page getHome, Page getTables, Page getAnswer, Page getError, Page getCalc, Page getOpHistory, Logger rootLogger) {
        this.req = req;
        this.jdbc = jdbc;
        this.getAbout = getAbout;
        this.getHome = getHome;
        this.getTables = getTables;
        this.getAnswer = getAnswer;
        this.getError = getError;
        this.getCalc = getCalc;
        this.getOpHistory = getOpHistory;
        this.rootLogger = rootLogger;
    }

    private void init() {
        if (req.getSession().isNew()) {
            jdbc.insertSessionTime();
            rootLogger.info("Подключился пользователь с IP: "+req.getRemoteAddr());
        } else {
            jdbc.updateSessionEndTime();
        }
    }

    //TODO привязать через Autowired и Qualifier реализации созданного абстрактного класса для каждого представления свою.

    @RequestMapping(path = "/")
    public ModelAndView getHome() throws Exception {
        init();
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