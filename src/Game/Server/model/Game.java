package Game.Server.model;

public class Game {
    private String gameData = "Default Data";

    public String getGamedata (){
        return gameData;
    }

    public void setGamedata( String str ){
        System.out.println("got to the server");
        gameData = str;
    }
}
