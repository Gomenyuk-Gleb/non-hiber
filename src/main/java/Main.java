import org.postgresql.ds.PGSimpleDataSource;
import orm.SesionFactory;
import orm.Session;

import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        DataSource dataSource = init();
        SesionFactory sesionFactory = new SesionFactory(dataSource);
        Session session = sesionFactory.createSession();

        Products products = session.find(Products.class, 7);
        products.setName("seven");
        products.setPrice(777);
        System.out.println(products);

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
