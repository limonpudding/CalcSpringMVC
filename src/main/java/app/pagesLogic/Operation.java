package app.pagesLogic;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Operation {
    public String getOperation() {
        return operation;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getResult() {
        return result;
    }

    public Date getDate() {
        return date;
    }

    public String getIdOperation() {
        return idOperation;
    }

    String a;
    String b;
    String operation;
    String result;
    Date date;
    String idOperation;
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    Operation() {
    }

    public Operation(Date date, String a, String b, String operation, String result, String idOperation) {
        this.date = date;
        this.a = a;
        this.b = b;
        this.operation = operation;
        this.result = result;
        this.idOperation = idOperation;
    }

    public String date() {
        return formatForDateNow.format(date);
    }

    @Override
    public String toString() {
        if (operation.equals("fib"))
            return formatForDateNow.format(date) + " : " + operation + "(" + a + ") = " + result;
        return formatForDateNow.format(date) + " : " + a + " " + operation + " " + b + " = " + result;
    }
}
