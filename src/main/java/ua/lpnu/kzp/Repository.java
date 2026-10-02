package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Type-safe in-memory repository.
 *
 * @param <T> type of stored objects
 */
public final class Repository<T> {

    private final List<T> items = new ArrayList<>();

    /**
     * Adds a non-null object to the repository.
     *
     * @param item object to add
     */
    public void add(T item) {
        items.add(
            Objects.requireNonNull(
                item,
                "Item cannot be null"
            )
        );
    }

    /**
     * Returns an immutable snapshot of all stored objects.
     *
     * @return immutable repository snapshot
     */
    public List<T> all() {
        return List.copyOf(items);
    }

    /**
     * Returns objects matching the specified condition.
     *
     * @param condition search predicate
     * @return matching objects
     */
    public List<T> find(
        Predicate<? super T> condition
    ) {
        Objects.requireNonNull(
            condition,
            "Condition cannot be null"
        );

        return items.stream()
            .filter(condition)
            .toList();
    }
}
