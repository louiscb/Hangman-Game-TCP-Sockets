package Game.Server.model;

public class Game {
    private String gameData = "Default Data";

    public String getGamedata (){
        return gameData;
    }

    public void setGamedata(String str){
        if (str.equals("start"))
            gameData = "starting code";
        else if (str.equals("end"))
            gameData = "goodbye";
    }
}
