package coursework.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Хранит состояние змейки и рассчитывает её движение.
 * При отражении от горизонтальной границы меняется вертикальная составляющая
 * скорости, а при отражении от вертикальной — горизонтальная.
 */
public class SnakeModel {
    private static final int SEGMENT_COUNT = 12;
    private static final double SEGMENT_SPACING = 15;
    private static final double HEAD_RADIUS = 10;

    private final double width;
    private final double height;
    private final Random random = new Random();
    private final List<Point> trail = new ArrayList<>();

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private double speed = 120;

    /**
     * Создаёт модель для области заданного размера.
     *
     * @param width  ширина области в пикселях
     * @param height высота области в пикселях
     */
    public SnakeModel(double width, double height) {
        if (width <= 2 * HEAD_RADIUS || height <= 2 * HEAD_RADIUS) {
            throw new IllegalArgumentException("Размеры области слишком малы");
        }
        this.width = width;
        this.height = height;
        reset();
    }

    /**
     * Устанавливает случайное начальное направление и очищает след.
     */
    public void reset() {
        x = width / 2;
        y = height / 2;
        double angle = random.nextDouble() * Math.PI * 2;
        velocityX = Math.cos(angle);
        velocityY = Math.sin(angle);
        trail.clear();
        for (int i = 0; i < 500; i++) {
            trail.add(new Point(x, y));
        }
    }

    /**
     * Меняет скорость движения.
     *
     * @param pixelsPerSecond скорость в пикселях в секунду
     */
    public void setSpeed(double pixelsPerSecond) {
        if (!Double.isFinite(pixelsPerSecond) || pixelsPerSecond < 0) {
            throw new IllegalArgumentException("Скорость должна быть неотрицательным числом");
        }
        speed = pixelsPerSecond;
    }

    /**
     * Обновляет позицию и отражает змейку от границ области.
     *
     * @param deltaSeconds время с предыдущего кадра в секундах
     */
    public void update(double deltaSeconds) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0) {
            throw new IllegalArgumentException("Время кадра должно быть неотрицательным");
        }

        x += velocityX * speed * deltaSeconds;
        y += velocityY * speed * deltaSeconds;

        if (x < HEAD_RADIUS) {
            x = HEAD_RADIUS;
            velocityX = Math.abs(velocityX);
        } else if (x > width - HEAD_RADIUS) {
            x = width - HEAD_RADIUS;
            velocityX = -Math.abs(velocityX);
        }

        if (y < HEAD_RADIUS) {
            y = HEAD_RADIUS;
            velocityY = Math.abs(velocityY);
        } else if (y > height - HEAD_RADIUS) {
            y = height - HEAD_RADIUS;
            velocityY = -Math.abs(velocityY);
        }

        trail.add(0, new Point(x, y));
        int maxTrailSize = (int) ((SEGMENT_COUNT * SEGMENT_SPACING * 2) / Math.max(speed * deltaSeconds, 1)) + 10;
        while (trail.size() > Math.max(500, maxTrailSize)) {
            trail.remove(trail.size() - 1);
        }
    }

    /**
     * Возвращает позиции головы и сегментов, расположенных вдоль пройденного пути.
     *
     * @return список точек от головы к хвосту
     */
    public List<Point> getSegments() {
        List<Point> result = new ArrayList<>();
        result.add(new Point(x, y));
        double targetDistance = SEGMENT_SPACING;
        double accumulatedDistance = 0;

        for (int i = 1; i < trail.size() && result.size() < SEGMENT_COUNT; i++) {
            Point previous = trail.get(i - 1);
            Point current = trail.get(i);
            accumulatedDistance += distance(previous, current);
            if (accumulatedDistance >= targetDistance) {
                result.add(current);
                targetDistance += SEGMENT_SPACING;
            }
        }

        while (result.size() < SEGMENT_COUNT) {
            result.add(new Point(x, y));
        }
        return List.copyOf(result);
    }

    private double distance(Point first, Point second) {
        return Math.hypot(first.x() - second.x(), first.y() - second.y());
    }

    /**
     * Возвращает текущую координату X головы.
     */
    public double getX() {
        return x;
    }

    /**
     * Возвращает текущую координату Y головы.
     */
    public double getY() {
        return y;
    }

    /**
     * Возвращает горизонтальную составляющую направления.
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Возвращает вертикальную составляющую направления.
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Неизменяемая точка на экране.
     */
    public record Point(double x, double y) {
    }
}
