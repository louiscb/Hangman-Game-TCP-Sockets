package server.model;

public class Game {
    String word;

    public String startGame() {
        word = Word.getRandomWord();
        return "Start Game";
    }

    public String endGame() {
        return null;
    }

    public String gameEntry(String input) {
        return word;
    }
}
