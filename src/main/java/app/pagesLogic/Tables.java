package app.pagesLogic;

import app.database.JDBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Tables extends Page {

    @Autowired
    JDBC jdbc;

    @Override
    public ModelAndView build() throws Exception {
        String modeSort = (String) getParams().get("mode");
        String orderSort = (String) getParams().get("order");
        String id = (String) getParams().get("id");
        ModelAndView mav;
        if ("1".equals(getParams().get("table"))) {
            mav = new ModelAndView("createTableSessions");
            mav.addObject("fullSessionsHistory", jdbc.selectSessionsFromBD(modeSort, orderSort));
        } else {
            mav = new ModelAndView("createTableOperations");
            mav.addObject("operationsHistory", jdbc.selectDataFromBD(modeSort, orderSort, id));
        }
        return mav;
    }

}

