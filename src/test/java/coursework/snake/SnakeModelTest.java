package coursework.snake;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Unit-тесты основных правил модели змейки. */
class SnakeModelTest {
    @Test
    void startsInsideThePlayArea() {
        SnakeModel model = new SnakeModel(800, 520);

        assertTrue(model.getX() >= 10 && model.getX() <= 790);
        assertTrue(model.getY() >= 10 && model.getY() <= 510);
    }

    @Test
    void speedCanBeChanged() {
        SnakeModel model = new SnakeModel(800, 520);

        assertDoesNotThrow(() -> model.setSpeed(200));
        assertThrows(IllegalArgumentException.class, () -> model.setSpeed(-1));
    }

    @Test
    void updateKeepsHeadInsideThePlayArea() {
        SnakeModel model = new SnakeModel(800, 520);

        for (int i = 0; i < 2000; i++) {
            model.update(0.016);
            assertTrue(model.getX() >= 10 && model.getX() <= 790);
            assertTrue(model.getY() >= 10 && model.getY() <= 510);
        }
    }

    @Test
    void modelReturnsExpectedNumberOfSegments() {
        SnakeModel model = new SnakeModel(800, 520);

        assertEquals(12, model.getSegments().size());
    }

    @Test
    void rejectsInvalidElapsedTime() {
        SnakeModel model = new SnakeModel(800, 520);

        assertThrows(IllegalArgumentException.class, () -> model.update(-0.1));
    }
}
