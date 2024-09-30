import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RunGameToApplication extends Application {
    private RussianRoulette game;
    private TextArea gameOutput;
    private Label scoreLabel;
    private Label multiplierLabel;
    private TextField nameInput;

    @Override
    public void start(Stage primaryStage) {
        nameInput = new TextField();
        nameInput.setPromptText("Enter your name");

        Button startButton = new Button("Start Game");
        Button pullButton = new Button("Pull");
        Button spinButton = new Button("Spin");
        Button leaderboardButton = new Button("Leaderboard");
        Button quitButton = new Button("Quit");
        Button restartButton = new Button("Restart");
        gameOutput = new TextArea();
        gameOutput.setEditable(false);
        gameOutput.setWrapText(true);
        scoreLabel = new Label("Score: 0");
        multiplierLabel = new Label("Multiplier: 1");

        startButton.setOnAction(event -> {
            String playerName = nameInput.getText();
            if (!playerName.isEmpty()) {
                game = new RussianRoulette(playerName);
                gameOutput.appendText("Game started for " + playerName + "\n");
                scoreLabel.setText("Score: 0");
                multiplierLabel.setText("Multiplier: 1");
            } else {
                gameOutput.appendText("Please enter a name to start the game.\n");
            }
        });

        pullButton.setOnAction(event -> {
            if (game != null) {
                boolean result = game.pullTrigger();
                if (result) {
                    gameOutput.appendText("You lost!\n");
                } else {
                    gameOutput.appendText("You're safe... for now.\n");
                }
                scoreLabel.setText("Score: " + game.score);
                multiplierLabel.setText("Multiplier: " + game.getMultiplier());
            } else {
                gameOutput.appendText("Please start the game first.\n");
            }
        });

        spinButton.setOnAction(event -> {
            if (game != null) {
                game.spinCylinder();
                gameOutput.appendText("The cylinder has been spun.\n");
                multiplierLabel.setText("Multiplier: 1");
            } else {
                gameOutput.appendText("Please start the game first.\n");
            }
        });

        leaderboardButton.setOnAction(event -> {
            if (game != null) {
                gameOutput.appendText(game.getLeaderboardManager().displayLeaderboard() + "\n");
            } else {
                gameOutput.appendText("Please start the game first.\n");
            }
        });

        quitButton.setOnAction(event -> {
            Platform.exit();
        });

        restartButton.setOnAction(event -> {
            if (game != null) {
                game.resetGame();
                gameOutput.clear();
                gameOutput.appendText("Game has been reset.\n");
                scoreLabel.setText("Score: 0");
                multiplierLabel.setText("Multiplier: 1");
            } else {
                gameOutput.appendText("Please start the game first.\n");
            }
        });

        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);

        root.add(nameInput, 0, 0, 2, 1);
        root.add(startButton, 2, 0);
        root.add(pullButton, 0, 1);
        root.add(spinButton, 1, 1);
        root.add(leaderboardButton, 2, 1);
        root.add(quitButton, 0, 2);
        root.add(restartButton, 1, 2);
        root.add(scoreLabel, 2, 2);
        root.add(multiplierLabel, 2, 3);
        root.add(new ScrollPane(gameOutput), 0, 4, 3, 1);

        Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().add("styles.css");

        primaryStage.setTitle("Russian Roulette");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}