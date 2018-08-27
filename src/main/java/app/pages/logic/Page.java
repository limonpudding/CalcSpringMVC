package app.pages.logic;

import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

public abstract class Page {
    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    Map<String, Object> params;
    public abstract ModelAndView build() throws Exception;
}
