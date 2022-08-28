package orm.service.impl;

import orm.Session;
import orm.anotation.Table;
import orm.service.ActionPriority;
import orm.service.ActionService;

import java.lang.reflect.Field;
import java.util.Arrays;

public class DeleteAction  implements ActionService {

    private Session session;
    private Object o;

    public DeleteAction(Session session,Object o ) {
        this.session = session;
        this.o = o;
    }

    @Override
    public void sendRequest() throws IllegalAccessException {
        String tableName = o.getClass().getDeclaredAnnotation(Table.class).value();
        Field field = Arrays.stream(o.getClass().getDeclaredFields())
                .filter(x -> x.getName().equals("id"))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        field.setAccessible(true);
        Object idValue = field.get(o);

        String delSql = "DELETE FROM " + tableName + " WHERE id = " + idValue;
        session.doIt(delSql);
        session.removeAction(this);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.DELETE;
    }
}
