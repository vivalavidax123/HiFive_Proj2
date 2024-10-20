package hifive.Managers;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import hifive.Game.GameConfigurations;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UIManager {
    private final CardGame game;
    private final Actor[] scoreActors;

    public UIManager(CardGame game) {
        this.game = game;
        this.scoreActors = new Actor[GameConfigurations.NB_PLAYERS];
    }

    // Initialize the score display for all players
    public void initScore() {
        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
            String text = "[0]";
            scoreActors[i] = new TextActor(text, Color.WHITE, game.bgColor, new Font("Arial", Font.BOLD, 36));
            game.addActor(scoreActors[i], GameConfigurations.SCORE_LOCATIONS[i]);
        }
    }

    // Update the score display for a specific player
    public void updateScore(int player, int score) {
        game.removeActor(scoreActors[player]);
        int displayScore = Math.max(score, 0);
        String text = "P" + player + "[" + displayScore + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, game.bgColor, new Font("Arial", Font.BOLD, 36));
        game.addActor(scoreActors[player], GameConfigurations.SCORE_LOCATIONS[player]);
    }

    // Set the status text in the game UI
    public void setStatus(String string) {
        game.setStatusText(string);
    }

    // Set up the card layout for all players and the playing area
    public void setupCardLayout(Hand[] hands, Hand playingArea) {
        playingArea.setView(game, new RowLayout(GameConfigurations.TRICK_LOCATION, (playingArea.getNumberOfCards() + 2) * GameConfigurations.TRICK_WIDTH));
        playingArea.draw();

        RowLayout[] layouts = new RowLayout[GameConfigurations.NB_PLAYERS];
        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
            layouts[i] = new RowLayout(GameConfigurations.HAND_LOCATIONS[i], GameConfigurations.HAND_WIDTH);
            layouts[i].setRotationAngle(90 * i);
            hands[i].setView(game, layouts[i]);
            hands[i].setTargetArea(new TargetArea(GameConfigurations.TRICK_LOCATION));
            hands[i].draw();
        }
    }

    // Display the game over screen with winners
    public void showGameOver(List<Integer> winners) {
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " + winners.get(0);
        } else {
            winText = "Game Over. Drawn winners are players: " + winners.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }
        game.addActor(new Actor("sprites/gameover.gif"), GameConfigurations.TEXT_LOCATION);
        setStatus(winText);
        game.refresh();
    }
}
