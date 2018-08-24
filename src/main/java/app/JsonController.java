package app;

import app.database.JDBC;
import app.pagesLogic.Answer;
import app.pagesLogic.Operation;
import app.pagesLogic.Page;
import app.pagesLogic.RESTParams;
import app.rest.Constant;
import app.rest.UpdatePost;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
        String ans = Answer.calc(a, b, operation);
        Operation operationObject = new Operation(new Date(), a, b, operation, ans, UUID.randomUUID().toString());
        jdbc.putDataInBD(operationObject);
        return new ResponseEntity<>(operationObject, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/post", method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
    public @ResponseBody
    HttpStatus updateConst(@RequestBody UpdatePost post) throws Exception {
        String keyOld = post.getKeyOld();
        String keyNew = post.getKeyNew();
        String value = post.getValue();
        jdbc.putDataInBD(operationObject);
        return HttpStatus.OK;
    }

    @RequestMapping(path = "/rest/put", method = RequestMethod.PUT, headers = "Accept=application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Operation> putConst(@RequestBody Constant constant) throws Exception {
        String key = constant.getKey();
        String value = constant.getValue();
        jdbc.putDataInBD(operationObject);
        return new ResponseEntity<>(operationObject, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/patch", method = RequestMethod.PATCH, headers = "Accept=application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Operation> updateValue(@RequestBody UpdatePost post) throws Exception {
        String keyOld = post.getKeyOld();
        String keyNew = post.getKeyNew();
        String value = post.getValue();
        jdbc.putDataInBD(operationObject);
        return new ResponseEntity<>(operationObject, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/delete", method = RequestMethod.DELETE, headers = "Accept=application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Operation> deleteConst(@RequestBody UpdatePost post) throws Exception {
        String keyOld = post.getKeyOld();
        String keyNew = post.getKeyNew();
        String value = post.getValue();
        jdbc.putDataInBD(operationObject);
        return new ResponseEntity<>(operationObject, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/get", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<Constant>> getConstants() throws Exception {
        String ans = Answer.calc(a, b, operation);
        Operation operationObject = new Operation(new Date(), a, b, operation, ans, UUID.randomUUID().toString());
        List<Constant> constants = jdbc.putDataInBD(operationObject);
        return new ResponseEntity<>(constants, HttpStatus.OK);
    }
}
