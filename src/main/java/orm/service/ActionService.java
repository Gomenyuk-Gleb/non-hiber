package orm.service;

import orm.Session;
import orm.service.impl.InsertAction;

public interface ActionService {

    void sendRequest() throws IllegalAccessException;

    ActionPriority getPriority();
}
