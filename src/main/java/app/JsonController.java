package app;

import app.database.JDBC;
import app.pagesLogic.Answer;
import app.pagesLogic.Operation;
import app.rest.Constant;
import app.rest.Key;
import app.rest.UpdatePost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class JsonController {
    private final HttpServletRequest req;
    private final JDBC jdbc;
    private final Logger rootLogger;
    private Logger logger = LogManager.getLogger(JsonController.class);
    private final String regex = "^[-+]?[0-9]+$";

    @Autowired
    public JsonController(HttpServletRequest req, JDBC jdbc, Logger rootLogger) {
        this.req = req;
        this.jdbc = jdbc;
        this.rootLogger = rootLogger;
    }

    @RequestMapping(path = "/rest/calc", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Operation> calc(
            @RequestParam(value = "a") String a,
            @RequestParam(value = "b") String b,
            @RequestParam(value = "operation") String operation) throws Exception {

        if (!a.matches(regex)) {
            a = jdbc.getConstantValueDB(a);
        }
        if (!b.matches(regex)) {
            b = jdbc.getConstantValueDB(b);
        }
        logger.info("Пользователь с IP: "+req.getRemoteAddr()+" начал выполнение операции \'"+operation+"\'");
        String ans = Answer.calc(a, b, operation);
        Operation operationObject = new Operation(new Date(), a, b, operation, ans, UUID.randomUUID().toString());
        jdbc.putDataInBD(operationObject);
        return new ResponseEntity<>(operationObject, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateConst(@RequestBody UpdatePost post) {
        String keyOld = post.getKeyOld();
        String keyNew = post.getKeyNew();
        String value = post.getValue();
        Constant constant = new Constant(keyNew, value);
        if (keyNew.matches(regex))
            logger.warn("Попытка переименовать константу в вид, содержащий только число. Её использование будет не возможно, до изменения");
        if (!value.matches(regex))
            logger.warn("Попытка присвоить значение константы, не представляющее собой число");

        jdbc.updatePostDB(keyOld, constant);
        logger.info("Пользователь с IP: "+req.getRemoteAddr()+" обновил константу \'"+keyOld+"\' на key:\'"+keyNew+"\', value:\'"+value+"\'");
    }

    @RequestMapping(path = "/rest", method = RequestMethod.PUT)
    public @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void putConst(@RequestBody Constant constant) {
        if (constant.getKey().matches(regex))
            logger.warn("Попытка добавить константу, состоящую только из числа. Её использование будет не возможно, до изменения");
        if (!constant.getValue().matches(regex))
            logger.warn("Попытка присвоить значение константы, не представляющее собой число");
        jdbc.putConstInDB(constant);
        logger.info("Пользователь с IP: "+req.getRemoteAddr()+" добавил константу key:\'"+constant.getKey()+"\', value:\'"+constant.getValue()+"\'");
    }

    @RequestMapping(path = "/rest", method = RequestMethod.PATCH)
    public @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateValue(@RequestBody Constant constant) {
        jdbc.updatePatchDB(constant);
        logger.info("Пользователь с IP: "+req.getRemoteAddr()+" обновил значение константы \'"+constant.getKey()+"\' на value:\'"+constant.getValue()+"\'");
    }

    @RequestMapping(path = "/rest", method = RequestMethod.DELETE)
    public @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteConst(@RequestBody Key key) {
        jdbc.deleteConstantDB(key.getKey());
        logger.info("Пользователь с IP: "+req.getRemoteAddr()+" удалил константу \'"+key+"\'");
    }

    @RequestMapping(path = "/rest", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<Constant>> getConstants() {
        List<Constant> constants = jdbc.getConstantsDB();
        logger.info("Пользователь с IP: "+req.getRemoteAddr()+" запросил список констант");
        return new ResponseEntity<>(constants, HttpStatus.OK);
    }
}
