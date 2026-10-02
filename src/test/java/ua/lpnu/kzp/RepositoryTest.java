package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void addAndAllReturnStoredItems() {
        Repository<String> repository = new Repository<>();

        repository.add("first");
        repository.add("second");

        assertEquals(
            List.of("first", "second"),
            repository.all()
        );
    }

    @Test
    void allReturnsImmutableSnapshot() {
        Repository<String> repository = new Repository<>();

        repository.add("first");

        List<String> snapshot = repository.all();

        assertThrows(
            UnsupportedOperationException.class,
            () -> snapshot.add("second")
        );
    }

    @Test
    void snapshotDoesNotChangeAfterRepositoryModification() {
        Repository<String> repository = new Repository<>();

        repository.add("first");

        List<String> snapshot = repository.all();

        repository.add("second");

        assertEquals(
            List.of("first"),
            snapshot
        );

        assertEquals(
            List.of("first", "second"),
            repository.all()
        );
    }

    @Test
    void findReturnsMatchingItems() {
        Repository<String> repository = new Repository<>();

        repository.add("Anna");
        repository.add("Ivan");
        repository.add("Andrii");

        List<String> result =
            repository.find(name ->
                name.startsWith("A")
            );

        assertEquals(
            List.of("Anna", "Andrii"),
            result
        );
    }

    @Test
    void addRejectsNull() {
        Repository<String> repository = new Repository<>();

        assertThrows(
            NullPointerException.class,
            () -> repository.add(null)
        );
    }

    @Test
    void findRejectsNullPredicate() {
        Repository<String> repository = new Repository<>();

        assertThrows(
            NullPointerException.class,
            () -> repository.find(null)
        );
    }
}
