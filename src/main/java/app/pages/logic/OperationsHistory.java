package app.pages.logic;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.LinkedList;

class OperationsHistory {

    LinkedList<Operation> operationsHistory = new LinkedList<>();

    public LinkedList<Operation> getHistory(HttpSession session) {
        Object attribute = session.getServletContext().getAttribute(session.getId());
        if (!(attribute != null && attribute instanceof Collection)) {
            operationsHistory = new LinkedList<>();
        } else {
            operationsHistory = (LinkedList<Operation>) attribute;
        }
        return operationsHistory;
    }

    public LinkedList<Operation> getHistory(){
        return operationsHistory;
    }

    public void addOperation(Operation operation) {
        operationsHistory.addFirst(operation);
    }
}
