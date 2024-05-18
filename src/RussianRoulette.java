import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RussianRoulette {
    private int bulletPosition;
    private int currentChamber;
    private int score = 0;
    private Random random = new Random();
    private Scanner scanner = new Scanner(System.in);
    private String playerName;

    private static final String FILE_PATH = "RussianRoulette.java";
    private static String highScore = "48";
    private static String highScoreHolder = "Ryan";

    private int multiplier = 1;

    public RussianRoulette(String playerName) {
        this.playerName = playerName;
        spinCylinder();
    }

    private void spinCylinder() {
        bulletPosition = random.nextInt(6) + 1; //Exclusive bound!
        currentChamber = random.nextInt(6) + 1;
        System.out.println("The cylinder is spun... No one knows where the bullet is.");
        System.out.println("Because you're a coward, I've reset the multiplier to 1.");
        multiplier = 1;
    }

    public void playGame() {
        LeaderboardManager leaderboard = new LeaderboardManager();
        System.out.println("Russian Roulette: Type 'pull' to pull the trigger, 'spin' to spin the cylinder again, 'leaderboard' to see cheaters, or 'quit' to exit. ");

        String input;
        while (true) {
            System.out.print("> ");
            input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "pull":
                    if (pullTrigger()) {
                        updateHighScore();
                        System.out.println("You died lol.");
                        System.out.println("Score : " + score + "             High Score : " + highScore + " by " + highScoreHolder);
                        leaderboard.updateLeaderboard(playerName, score);
                        return;
                    } else {
                        score += 1 * multiplier;
                        multiplier++;
                        System.out.println("You're safe... for now.             Multiplier Increased! [x" + multiplier + "]");
                        System.out.println("Score : " + score);
                        updateHighScore();
                        currentChamber = (currentChamber % 6) + 1;
                    }
                    break;
                case "spin":
                    spinCylinder();
                    break;
                case "leaderboard":
                    leaderboard.displayLeaderboard();
                    break;
                case "quit":
                    System.out.println("Game over.");
                    updateHighScore();
                    return;
                default:
                    System.out.println("Type 'pull', 'spin', or 'quit'.");
            }
        }
    }

    private boolean pullTrigger() {
        return currentChamber == bulletPosition;
    }

    private void readHighScore(){
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                if (line.startsWith("private static String highScore =")) {
                    highScore = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
                    highScoreHolder = lines.get(16).substring(lines.get(16).indexOf('"') + 1, lines.get(16).lastIndexOf('"'));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Ermm... well this is awkward... >.< ur file sucks mister..");
        }
    }

    private void updateHighScore(){
        if (score > Integer.parseInt(highScore)) {
            highScore = String.valueOf(score);
            highScoreHolder = playerName;
            try {
                List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
                lines.set(15, "    private static String highScore = \"" + highScore + "\"; // High score");
                lines.set(16, "    private static String highScoreHolder = \"" + highScoreHolder + "\"; // High score holder");
                Files.write(Paths.get(FILE_PATH), lines);
                System.out.println("High Score Updated: " + highScore + " by " + highScoreHolder);
            } catch (IOException e) {
                System.out.println("Ermm... well this is awkward... >.< ur file sucks..");
            }
        } else {
            System.out.println("High Score Remains: " + highScore + " by " + highScoreHolder);
        }
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = inputScanner.nextLine().trim();

        RussianRoulette game = new RussianRoulette(name);
        game.readHighScore();
        System.out.println("High Score : " + highScore + " by " + highScoreHolder);
        game.playGame();
    }

    public class PlayerScore implements Comparable<PlayerScore> {
        private String playerName;
        private int score;

        public PlayerScore(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(PlayerScore other) {
            return Integer.compare(other.score, this.score);
        }

        @Override
        public String toString() {
            return playerName + ": " + score;
        }
    }


    public class LeaderboardManager {
        private static final String LEADERBOARD_FILE = "leaderboard.txt";
        private List<PlayerScore> scores;

        public LeaderboardManager() {
            scores = new ArrayList<>();
            loadScores();
        }

        private void loadScores() {
            try (BufferedReader reader = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    scores.add(new PlayerScore(parts[0], Integer.parseInt(parts[1].trim())));
                }
                Collections.sort(scores);
            } catch (IOException e) {
                System.out.println("where's ur file???");
            }
        }

        public void updateLeaderboard(String playerName, int score) {
            Optional<PlayerScore> existingScore = scores.stream()
                    .filter(s -> s.getPlayerName().equals(playerName))
                    .findFirst();

            if (existingScore.isPresent()) {
                if (existingScore.get().getScore() < score) {
                    existingScore.get().setScore(score);
                }
            } else {
                scores.add(new PlayerScore(playerName, score));
            }

            Collections.sort(scores, Collections.reverseOrder());

            if (scores.size() > 10) {
                scores = scores.subList(0, 10);
            }

            saveScores();
        }


        private void saveScores() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
                for (PlayerScore score : scores) {
                    writer.println(score);
                }
            } catch (IOException e) {
                System.out.println("Error writing to the leaderboard file.");
            }
        }

        public void displayLeaderboard() {
            System.out.println("Leaderboard:");
            for (PlayerScore score : scores) {
                System.out.println(score);
            }
        }
    }


}
