package beatbinder.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class tagTest {
    @Test
    void testDefaultConstructor() {
        Tag tag = new Tag();
        assertEquals(-1, tag.getID());
        assertNull(tag.getName());
    }

    @Test
    void testConstructorWithName() {
        Tag tag = new Tag("Chill");
        assertEquals(-1, tag.getID());
        assertEquals("Chill", tag.getName());
    }

    @Test
    void testConstructorWithIdAndName() {
        Tag tag = new Tag(42, "Workout");
        assertEquals(42, tag.getID());
        assertEquals("Workout", tag.getName());
    }

    @Test
    void testCopyConstructor() {
        Tag original = new Tag(7, "Party");
        Tag copy = new Tag(original);

        assertEquals(original.getID(), copy.getID());
        assertEquals(original.getName(), copy.getName());
        assertNotSame(original, copy);
    }

    @Test
    void testImmutability() {
        Tag tag = new Tag(1, "Jazz");
        // There are no setters, so fields cannot be modified after creation.
        assertEquals("Jazz", tag.getName());
        assertEquals(1, tag.getID());
    }
}
