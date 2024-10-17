package scores;

import hifive.HiFive;
import hifive.PropertiesLoader;
import org.junit.Test;

import java.util.Properties;
import java.util.Scanner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class TestScoring {
    class RoundData {
        int roundNumber;
        String playerSelections;
        int[] playerScores = new int[4];

        @Override
        public String toString() {
            return "Round " + roundNumber + ". Player Selections: "  + playerSelections + ". Scores: " +
                    playerScores[0] + "," + playerScores[1] + "," + playerScores[2] + "," + playerScores[3];
        }
    }

    private RoundData convertFromLogLine(String logLine) {
        if (!logLine.startsWith("Round")) {
            return null;
        }

        String[]scoreSplit = logLine.split("Score:");
        String roundMovementPart = scoreSplit[0];
        String scoreDataPart = scoreSplit[1];

        String[]roundSplit = roundMovementPart.split(":");
        String roundDataPart = roundSplit[0];
        String movementPart = roundSplit[1];
        movementPart = movementPart.substring(0, movementPart.length() - 1);
        int roundNumber = Integer.parseInt(roundDataPart.replaceAll("Round", ""));

        String[] playerScore = scoreDataPart.split(",");
        RoundData roundData = new RoundData();
        roundData.roundNumber = roundNumber;
        roundData.playerSelections = movementPart;
        for (int i = 0; i < roundData.playerScores.length; i++) {
            roundData.playerScores[i] = Integer.parseInt(playerScore[i]);
        }

        return roundData;
    }


    private int[] convertEndGameFromLogLine(String logLine) {
        if (!logLine.startsWith("EndGame:")) {
            return null;
        }

        String [] endGameScoreStrings = logLine.split("EndGame:");
        String endGameScoreString = endGameScoreStrings[1];
        String[] scoreStrings = endGameScoreString.split(",");
        int[] scores = new int[4];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = Integer.parseInt(scoreStrings[i]);
        }

        return scores;
    }

    private Scanner runningGame(String propertiesFile) {
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesFile);
        String logResult = new HiFive(properties).runApp();
        Scanner scanner = new Scanner(logResult);
        assertTrue(scanner.hasNextLine());
        System.out.println("logResult = " + logResult);
        return scanner;
    }

    @Test(timeout = 60000)
    public void test1WinnerOriginal() {
        String testProperties = "properties/test1.properties";
        Scanner scanner = runningGame(testProperties);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int[]endGameScores = convertEndGameFromLogLine(line);

            if (endGameScores != null) {
                assertArrayEquals(endGameScores, new int[] { 115, 3, 3, 3 });
            }
        }
    }

    @Test(timeout = 60000)
    public void test1WinnerWildCard() {
        String testProperties = "properties/test2.properties";
        Scanner scanner = runningGame(testProperties);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int[]endGameScores = convertEndGameFromLogLine(line);

            if (endGameScores != null) {
                assertArrayEquals(endGameScores, new int[] { 90, 19, 19, 19 });
            }
        }
    }

    @Test(timeout = 60000)
    public void test2WinnerOriginal() {
        String testProperties = "properties/test3.properties";
        Scanner scanner = runningGame(testProperties);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int[]endGameScores = convertEndGameFromLogLine(line);

            if (endGameScores != null) {
                assertArrayEquals(endGameScores, new int[] { 90, 55, 3, 3 });
            }
        }
    }

    @Test(timeout = 600000)
    public void test2WinnerWildCard() {
        String testProperties = "properties/test4.properties";
        Scanner scanner = runningGame(testProperties);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int[]endGameScores = convertEndGameFromLogLine(line);

            if (endGameScores != null) {
                assertArrayEquals(endGameScores, new int[] { 80, 55, 3, 3 });
            }
        }
    }

    @Test(timeout = 600000)
    public void test0Winner() {
        String testProperties = "properties/test5.properties";
        Scanner scanner = runningGame(testProperties);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int[]endGameScores = convertEndGameFromLogLine(line);

            if (endGameScores != null) {
                assertArrayEquals(endGameScores, new int[] { 19, 15, 17, 21 });
            }
        }
    }
}
