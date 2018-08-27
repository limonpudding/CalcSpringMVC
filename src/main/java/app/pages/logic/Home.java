package app.pages.logic;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service("getHome")
public class Home extends Page {
    @Override
    public ModelAndView build() {
        ModelAndView mav = new ModelAndView("home");
        return mav;
    }
}