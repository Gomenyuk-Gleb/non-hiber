package orm;

import orm.anotation.Colum;
import orm.anotation.Table;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public class Session {

    private final DataSource dataSource;
    private static final String SQL = "Select * FROM %s where id = ?";
    private final HashMap<EntityKey<?>, Object> entityList = new HashMap<>();
    private final HashMap<EntityKey<?>, Object[]> entityName = new HashMap<>();
    private final HashMap<EntityKey<?>, Map<String, String>> fieldToUpdate = new HashMap<>();


    public Session(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T find(Class<T> aClass, Object id) {
        EntityKey<T> tEntityKey = new EntityKey<>(aClass, id);
        if (entityList.containsKey(tEntityKey)) {
            return aClass.cast(entityList.get(tEntityKey));
        }
        try (Connection con = dataSource.getConnection()) {
            String s = createSql(aClass);
            try (PreparedStatement ps = con.prepareStatement(s)) {
                ps.setObject(1, id);
                ResultSet resultSet = ps.executeQuery();
                T obj = createObj(tEntityKey, resultSet);
                entityList.put(tEntityKey, obj);
                return obj;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> String createSql(Class<T> aClass) {
        String value = aClass.getDeclaredAnnotation(Table.class).value();
        return String.format(SQL, value);
    }

    public <T> T createObj(EntityKey<T> entityKey, ResultSet resultSet) throws InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException {
        resultSet.next();
        Class<T> entity = entityKey.type();
        Object entityObj = entity.getConstructor().newInstance();
        Field[] declaredFields = Arrays.stream(entity.getDeclaredFields())
                .sorted(Comparator.comparing(Field::getName))
                .toArray(Field[]::new);
        Object[] arr = new Object[declaredFields.length];

        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            String declaredAnnotation = field.getDeclaredAnnotation(Colum.class).value();
            field.setAccessible(true);
            Object fieldValue = resultSet.getObject(declaredAnnotation);
            field.set(entityObj, fieldValue);
            arr[i] = fieldValue;
        }
        entityName.put(entityKey, arr);
        return entity.cast(entityObj);
    }


    public void close() {
        entityList.entrySet().stream()
                .filter(em -> {
                    try {
                        return checkChange(em);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(this::updateEntity);
    }

    private void updateEntity(Map.Entry<EntityKey<?>, Object> entityKeyObjectEntry) {
        try (Connection connection = dataSource.getConnection()) {
            prepare(connection, entityKeyObjectEntry.getKey());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void prepare(Connection connection, EntityKey<?> key) {
        String sqlUpdate = generatorSql(key);
        try (Statement preparedStatement = connection.createStatement()) {
            preparedStatement.execute(sqlUpdate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generatorSql(EntityKey<?> key) {

        Class<?> entityType = key.type();
        String tableName = entityType.getAnnotation(Table.class).value();
        String sql = "Update " + tableName + " SET ";
        Map<String, String> stringStringMap = fieldToUpdate.get(key);

        for (Map.Entry<String, String> stringStringEntry : stringStringMap.entrySet()) {
            sql += stringStringEntry.getKey() + " = " + "'" +stringStringEntry.getValue() + "' ,";
        }
        sql =  sql.substring(0, sql.length() -1);
        sql += " Where id = " + stringStringMap.get("id");

        return sql;
    }

    private boolean checkChange(Map.Entry<EntityKey<?>, Object> en) throws IllegalAccessException {
        boolean readyToUpdate = false;
        Map<String, String> stringStringMap = new HashMap<>();
        Object entity = en.getValue();
        Class<?> entityType = entity.getClass();
        Field[] currentFields = Arrays.stream(entityType.getDeclaredFields())
                .sorted(Comparator.comparing(Field::getName))
                .toArray(Field[]::new);

        Object[] snapFields = entityName.get(en.getKey());
        for (int i = 0; i < currentFields.length; i++) {
            Field currentField = currentFields[i];
            currentField.setAccessible(true);
            if(currentField.getName().equals("id")) {
                stringStringMap.put(currentField.getName(), currentField.get(entity).toString());
            }

            if (!currentField.get(entity).equals(snapFields[i])) {
                readyToUpdate = true;
                stringStringMap.put(currentField.getName(), currentField.get(entity).toString());
            }
        }

        if(readyToUpdate)
            fieldToUpdate.put(en.getKey(), stringStringMap);

        return readyToUpdate;
    }
}
