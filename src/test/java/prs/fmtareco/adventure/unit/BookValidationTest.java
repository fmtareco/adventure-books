package prs.fmtareco.adventure.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.support.TestBookFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookValidationTest {


    @Test
    void validBookShouldPass() {
        Book book = TestBookFactory.createValidBook();
        assertSame(Book.Condition.OK, book.getCondition());
        assertTrue(book.isValid());
    }

    @Test
    void noBeginShouldFail() {
        Book book = TestBookFactory.createNoBeginBook();
        assertSame(Book.Condition.INVALID_BEGIN, book.getCondition());
        assertFalse(book.isValid());
    }

    @Test
    void multipleBeginsShouldFail() {
        Book book = TestBookFactory.createMultipleBeginsBook();
        assertSame(Book.Condition.INVALID_BEGIN, book.getCondition());
        assertFalse(book.isValid());
    }

    @Test
    void noEndShouldFail() {
        Book book = TestBookFactory.createNoEndBook();
        assertSame(Book.Condition.NO_END, book.getCondition());
        assertFalse(book.isValid());
    }

    @Test
    void invalidGotoShouldFail() {
        Book book = TestBookFactory.createBookWithInvalidGoTo();
        assertSame(Book.Condition.INVALID_GOTO, book.getCondition());
        assertFalse(book.isValid());
    }

    @Test
    void nonEndWithoutOptionsShouldFail() {
        Book book = TestBookFactory.createBookWithNonEndWithoutOptions();
        assertSame(Book.Condition.NO_OPTIONS, book.getCondition());
        assertFalse(book.isValid());
    }


}
