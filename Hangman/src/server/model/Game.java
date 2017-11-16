package server.model;

class Game {
    private String word, gameWord, message; //word is the full word we get from the text file // gameWord is the hidden word we send to the client

    private static String startMsg = "Hello, welcome to Hangman! Enter a word or character as a guess. Enter 'Play Again' to restart, or 'End Game' to quit.";
    private static String loseMsg = "You lose";
    private static String winMsg = "You win!";
    private static String inGameMsgCorrectGuess = "Correct Guess";
    private static String inGameMsgWrongGuess = "Wrong Guess";
    private static String hiddenChar = "_";
    private int numAttempts, score = 0;
    private boolean wonGame;

    String startGame() {
        word = Word.getRandomWord();
        numAttempts = word.length();
        message = startMsg;
        wonGame = false;

        StringBuilder sb = new StringBuilder();

        for (char c: word.toCharArray()) {
            sb.append(hiddenChar);
        }

        gameWord = sb.toString();

        System.out.println("Word is " + word);

        return createResponse();
    }

    String gameEntry(String input) {
        boolean correctGuess = false;
        wonGame = false;

        if (numAttempts != 0) {
            if (input.length() > 1) {
                if (input.equals(word)) {
                    wonGame = true;
                    gameWord = word;
                }
            } else if (input.length() == 1) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < word.length(); i++) {
                    char c = word.charAt(i);
                    if (c == input.charAt(0)) {
                        sb.append(c);
                        correctGuess = true;
                    } else {
                        sb.append(gameWord.charAt(i));
                    }
                }

                gameWord = sb.toString();
                if (!gameWord.contains(hiddenChar))
                    wonGame = true;
            }

            if (correctGuess) {
                message = inGameMsgCorrectGuess;
            } else {
                message = inGameMsgWrongGuess;
                numAttempts--;
            }
        }

        if (wonGame) {
            return winGame();
        } else if (numAttempts < 1) {
            return loseGame();
        }

        return createResponse();
    }

    String restart() {
        return startGame();
    }

    private String loseGame() {
        message = loseMsg;
        score+=-1;
        return createResponse();
    }

    private String winGame() {
        message = winMsg;
        score++;
        return createResponse();
    }

    private String createResponse() {
        String response;

        StringBuilder sb = new StringBuilder();

        sb.append(score);
        sb.append("/");
        sb.append(numAttempts);
        sb.append("/");
        sb.append(gameWord);
        sb.append("/");
        sb.append(message);
        sb.append("/");

        response = sb.toString();
        return response;
    }
}
