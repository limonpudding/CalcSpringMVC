package app.pages.logic;

public class RESTParams {
    private String a;
    private String b;
    private String operation;


    RESTParams(){}

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getOperation() {
        return operation;
    }

    public void setA(String a) {
        this.a = a;
    }

    public void setB(String b) {
        this.b = b;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public RESTParams(String a, String b, String operation) {
        this.a = a;
        this.b = b;
        this.operation = operation;
    }
}
