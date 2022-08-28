import org.postgresql.ds.PGSimpleDataSource;
import orm.SesionFactory;
import orm.Session;

import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) throws IllegalAccessException {
        DataSource dataSource = init();
        SesionFactory sesionFactory = new SesionFactory(dataSource);
        Session session = sesionFactory.createSession();

        Products products = new Products();
        products.setName("Test");
        products.setPrice(10909);
        session.persist(products);

        session.remove(products);
        session.persist(products);


        session.close();

    }

    private static DataSource init() {
        PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setURL("jdbc:postgresql://crawler_sitechecker_postgres:5432/postgres");
        pgSimpleDataSource.setUser("postgres");
        pgSimpleDataSource.setPassword("postgres");
        return pgSimpleDataSource;
    }
}
