package hifive.UIComponent;

import ch.aplu.jcardgame.*;
import java.util.List;

public interface IUIManager {
    void initScore();
    void updateScore(int player, int score);
    void setStatus(String status);
    void setupCardLayout(Hand[] hands, Hand playingArea);
    void showGameOver(List<Integer> winners);
}
