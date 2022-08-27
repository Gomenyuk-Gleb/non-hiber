package orm;

import java.util.Objects;

public record EntityKey<T>(Class<T> type, Object id) {
}
