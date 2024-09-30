import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RussianRoulette {
    private int bulletPosition;
    private int currentChamber;
    public int score = 0;
    private final Random random = new Random();
    private String playerName;
    private int multiplier = 1;
    private LeaderboardManager leaderboard;

    public RussianRoulette(String playerName) {
        this.playerName = playerName;
        this.leaderboard = new LeaderboardManager();
        spinCylinder();
    }

    public void spinCylinder() {
        bulletPosition = random.nextInt(6) + 1; // Exclusive bound!
        currentChamber = random.nextInt(6) + 1;
        multiplier = 1;
    }

    public boolean pullTrigger() {
        boolean result = currentChamber == bulletPosition;
        if (!result) {
            score += multiplier;
            multiplier++;
            currentChamber = (currentChamber % 6) + 1;
        } else {
            leaderboard.updateLeaderboard(playerName, score);
        }
        return result;
    }

    public void resetGame() {
        score = 0;
        spinCylinder();
    }

    public int getMultiplier() {
        return multiplier;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboard;
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

            scores.sort(Collections.reverseOrder());

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

        public String displayLeaderboard() {
            StringBuilder leaderboard = new StringBuilder("Leaderboard:\n");
            for (PlayerScore score : scores) {
                leaderboard.append(score).append("\n");
            }
            return leaderboard.toString();
        }
    }
}