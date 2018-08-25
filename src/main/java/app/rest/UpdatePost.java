package app.rest;

public class UpdatePost {
    public String getKeyOld() {
        return keyOld;
    }

    public String getKeyNew() {
        return keyNew;
    }

    public String getValue() {
        return value;
    }

    public UpdatePost(){}

    public UpdatePost(String keyOld, String keyNew, String value) {
        this.keyOld = keyOld;
        this.keyNew = keyNew;
        this.value = value;

    }

    private String keyOld;
    private String keyNew;
    private String value;
}
