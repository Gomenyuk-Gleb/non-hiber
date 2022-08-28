package orm.service.impl;

import orm.Session;
import orm.anotation.Colum;
import orm.anotation.Table;
import orm.service.ActionPriority;
import orm.service.ActionService;

import javax.swing.*;
import java.lang.reflect.Field;

public class InsertAction implements ActionService {

    private Session session;
    private Object o;
    private static final String INSERT_SQL = "INSERT INTO %s  VALUES";


    public InsertAction(Session session, Object o) {
        this.session = session;
        this.o = o;
    }

    @Override
    public void sendRequest() throws IllegalAccessException {
        String tableName= o.getClass().getDeclaredAnnotation(Table.class).value();
        StringBuilder sbParam = new StringBuilder().append("(");
        StringBuilder sbInsert = new StringBuilder().append("(");

        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(o);
            if(field.isAnnotationPresent(Colum.class) && !field.getDeclaredAnnotation(Colum.class).value().equals("id") &&
                    !field.getDeclaredAnnotation(Colum.class).autoGeneration()) {
                sbParam.append("").append(field.getDeclaredAnnotation(Colum.class).value()).append("").append(",");
                sbInsert.append("'").append(value).append("'").append(",");
            }
        }
        sbParam.deleteCharAt(sbParam.length() - 1).append(")");
        sbInsert.deleteCharAt(sbInsert.length() - 1).append(");");
        final String resultSql = "INSERT INTO " + tableName + sbParam.toString() + " VALUES " + sbInsert;
        session.doIt(resultSql);
        session.removeAction(this);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.INSERT;
    }
}
