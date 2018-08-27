package app.pages.logic;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service("getCalc")
public class Calc extends Page {
    @Override
    public ModelAndView build() {
        ModelAndView mav = new ModelAndView("input");
        return mav;
    }
}
