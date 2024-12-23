package hifive.UIComponent;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import hifive.GameConfigurations;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UIManager implements IUIManager {
    private final GameConfigurations config;
    private final CardGame game;
    private final Actor[] scoreActors;

    public UIManager(GameConfigurations config, CardGame game) {
        this.config = config;
        this.game = game;
        this.scoreActors = new Actor[config.NB_PLAYERS];
    }

    @Override
    public void initScore() {
        for (int i = 0; i < config.NB_PLAYERS; i++) {
            String text = "[0]";
            scoreActors[i] = new TextActor(text, Color.WHITE, game.bgColor, new Font("Arial", Font.BOLD, 36));
            game.addActor(scoreActors[i], config.SCORE_LOCATIONS[i]);
        }
    }

    @Override
    public void updateScore(int player, int score) {
        game.removeActor(scoreActors[player]);
        int displayScore = Math.max(score, 0);
        String text = "P" + player + "[" + displayScore + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, game.bgColor, new Font("Arial", Font.BOLD, 36));
        game.addActor(scoreActors[player], config.SCORE_LOCATIONS[player]);
    }

    @Override
    public void setStatus(String status) {
        game.setStatusText(status);
    }

    @Override
    public void setupCardLayout(Hand[] hands, Hand playingArea) {
        playingArea.setView(game, new RowLayout(config.TRICK_LOCATION, (playingArea.getNumberOfCards() + 2) * config.TRICK_WIDTH));
        playingArea.draw();

        RowLayout[] layouts = new RowLayout[config.NB_PLAYERS];
        for (int i = 0; i < config.NB_PLAYERS; i++) {
            layouts[i] = new RowLayout(config.HAND_LOCATIONS[i], config.HAND_WIDTH);
            layouts[i].setRotationAngle(90 * i);
            hands[i].setView(game, layouts[i]);
            hands[i].setTargetArea(new TargetArea(config.TRICK_LOCATION));
            hands[i].draw();
        }
    }

    @Override
    public void showGameOver(List<Integer> winners) {
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " + winners.get(0);
        } else {
            winText = "Game Over. Drawn winners are players: " + winners.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }
        game.addActor(new Actor("sprites/gameover.gif"), config.TEXT_LOCATION);
        setStatus(winText);
        game.refresh();
    }
}
