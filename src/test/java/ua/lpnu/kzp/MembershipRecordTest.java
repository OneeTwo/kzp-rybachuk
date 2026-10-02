package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class MembershipRecordTest {

    @Test
    void createsValidMembershipRecord() {
        MembershipRecord record =
            new MembershipRecord(
                "Ivan",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                12
            );

        assertEquals("Ivan", record.getCustomer());
        assertEquals(
            MembershipKind.MONTHLY,
            record.getKind()
        );
        assertEquals(
            LocalDate.of(2026, 10, 1),
            record.getStartDate()
        );
        assertEquals(
            LocalDate.of(2026, 10, 31),
            record.getEndDate()
        );
        assertEquals(12, record.getVisits());
    }

    @Test
    void equalRecordsAreEqual() {
        MembershipRecord first =
            new MembershipRecord(
                "Maria",
                MembershipKind.ANNUAL,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                100
            );

        MembershipRecord second =
            new MembershipRecord(
                "Maria",
                MembershipKind.ANNUAL,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                100
            );

        assertEquals(first, second);
        assertEquals(
            first.hashCode(),
            second.hashCode()
        );
    }

    @Test
    void rejectsNegativeVisits() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MembershipRecord(
                "Ivan",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                -1
            )
        );
    }

    @Test
    void rejectsEndDateBeforeStartDate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MembershipRecord(
                "Ivan",
                MembershipKind.MONTHLY,
                LocalDate.of(2026, 10, 31),
                LocalDate.of(2026, 10, 1),
                5
            )
        );
    }

    @Test
    void fieldsHaveCsvColumnAnnotations()
        throws NoSuchFieldException {

        checkColumn("customer", "клієнт");
        checkColumn("kind", "тип");
        checkColumn("startDate", "початок");
        checkColumn("endDate", "кінець");
        checkColumn("visits", "відвідування");
    }

    private void checkColumn(
        String fieldName,
        String expectedColumn
    ) throws NoSuchFieldException {

        Field field =
            MembershipRecord.class
                .getDeclaredField(fieldName);

        CsvColumn annotation =
            field.getAnnotation(CsvColumn.class);

        assertNotNull(annotation);
        assertEquals(
            expectedColumn,
            annotation.value()
        );
    }
}
