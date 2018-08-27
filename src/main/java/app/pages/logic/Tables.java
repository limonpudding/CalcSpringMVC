package app.pages.logic;

import app.database.JDBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service("getTables")
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

