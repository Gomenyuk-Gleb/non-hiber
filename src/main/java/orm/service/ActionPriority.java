package orm.service;

public enum ActionPriority  {

    DELETE(2), INSERT(1);

    public int priority;
    ActionPriority(int priority) {
        this.priority = priority;
    }

}
