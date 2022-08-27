package orm;

import javax.sql.DataSource;

public class SesionFactory {
    private final DataSource dataSource;

    public SesionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Session createSession() {
        return new Session(dataSource);
    }
}
