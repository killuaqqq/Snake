package coursework.snake;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JavaFX-приложение для демонстрации движения змейки с отражением от границ.
 */
public class SnakeApp extends Application {
    private static final Logger LOGGER = LogManager.getLogger(SnakeApp.class);
    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 520;

    private final SnakeModel model = new SnakeModel(CANVAS_WIDTH, CANVAS_HEIGHT);
    private Canvas canvas;
    private AnimationTimer timer;
    private long previousFrameTime;
    private boolean paused;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var iconStream = getClass().getResourceAsStream("/snake.png");

        if (iconStream == null) {
            System.out.println("ОШИБКА: snake.png не найдена!");
        } else {
            Image icon = new Image(iconStream);
            stage.getIcons().clear();
            stage.getIcons().add(icon);
            System.out.println("Иконка загружена: " + icon.getWidth()
                    + "x" + icon.getHeight());
        }
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        Slider speedSlider = new Slider(40, 300, 120);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(65);
        speedSlider.setBlockIncrement(10);

        Label speedLabel = new Label();
        speedLabel.textProperty().bind(speedSlider.valueProperty().asString("Скорость: %.0f px/с"));
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                model.setSpeed(newValue.doubleValue()));

        Button pauseButton = new Button("Пауза");
        pauseButton.setOnAction(event -> {
            paused = !paused;
            pauseButton.setText(paused ? "Продолжить" : "Пауза");
            LOGGER.info(paused ? "Движение приостановлено" : "Движение продолжено");
        });

        Button restartButton = new Button("Заново");
        restartButton.setOnAction(event -> {
            model.reset();
            LOGGER.info("Змейка перезапущена");
            draw();
        });

        Button exitButton = new Button("Выход");
        exitButton.setOnAction(event -> stage.close());


        HBox controls = new HBox(
                12,
                speedLabel,
                speedSlider,
                pauseButton,
                restartButton,
                exitButton
        );
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(12));

        BorderPane root = new BorderPane(canvas);
        root.setBottom(controls);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f4f6f8;");

        canvas.setFocusTraversable(true);
        canvas.widthProperty().addListener((obs, oldValue, newValue) -> draw());
        canvas.heightProperty().addListener((obs, oldValue, newValue) -> draw());

        Scene scene = new Scene(root);
        stage.setTitle("Движение змейки");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (previousFrameTime == 0) {
                    previousFrameTime = now;
                    return;
                }
                double elapsedSeconds = (now - previousFrameTime) / 1_000_000_000.0;
                previousFrameTime = now;

                if (!paused) {
                    model.update(Math.min(elapsedSeconds, 0.05));
                    draw();
                }
            }
        };
        timer.start();
        LOGGER.info("Приложение запущено");
    }

    /**
     * Отрисовывает фон, сегменты змейки и её голову.
     */
    private void draw() {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphics.setStroke(Color.rgb(225, 231, 235));
        for (int x = 0; x < canvas.getWidth(); x += 40) {
            graphics.strokeLine(x, 0, x, canvas.getHeight());
        }
        for (int y = 0; y < canvas.getHeight(); y += 40) {
            graphics.strokeLine(0, y, canvas.getWidth(), y);
        }

        var segments = model.getSegments();
        for (int i = segments.size() - 1; i >= 0; i--) {
            SnakeModel.Point point = segments.get(i);
            double diameter = i == 0 ? 20 : 16;
            graphics.setFill(i == 0 ? Color.DARKGREEN : Color.LIMEGREEN);
            graphics.fillOval(point.x() - diameter / 2, point.y() - diameter / 2,
                    diameter, diameter);
        }
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        LOGGER.info("Приложение закрыто");
    }
}
