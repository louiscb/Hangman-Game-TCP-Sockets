package server.controller;

import server.model.GameSetup;

public class Controller {
    private GameSetup model = new GameSetup();

    public void setInput (String inputData) {
        model.setGameData(inputData);
    }

    public String getOutput () {
        return model.getGameData();
    }


}