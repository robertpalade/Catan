package catan;

import catan.API.Response;
import catan.API.HttpClientPost;
import catan.API.request.GameRequest;
import catan.API.request.ManagerRequest;
import catan.API.request.Status;
import catan.API.request.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ConnectivitySimulation {
    String username = "catan";
    String password = "catan";
    String gameID = null;
    List<String> playersID = new ArrayList<>();

    // region Manager Commands

    public String createGame() throws IOException {
        Response response;
        response = HttpClientPost.managerPostTo(new ManagerRequest(username, password, "newGame","{\"scenario\": \"SettlersOfCatan\"}"));
        if (response.getCode() == Status.ERROR)
            return null;
        else {
            HashMap<String,String> args=GameRequest.getMapFromData(response.getData());
            return args.get("gameId");
        }
    }

    public String startGame(String gameId) throws IOException {
        Map<String,String> payload = new HashMap<>();
        payload.put("gameId",gameId);
        String jsonArgs = new ObjectMapper().writeValueAsString(payload);
        Response response;
        response = HttpClientPost.managerPostTo(new ManagerRequest(username, password,
                "startGame",jsonArgs));
        if (response.getCode() == Status.ERROR)
            return null;
        else
            return response.getStatus();
    }
    public boolean setMaxPlayers(String gameId, Integer no) throws IOException {
        Map<String,String> payload = new HashMap<>();
        payload.put("gameId",gameId);
        payload.put("maxPlayers",no.toString());
        String jsonArgs = new ObjectMapper().writeValueAsString(payload);
        Response response;
        response = HttpClientPost.managerPostTo(new ManagerRequest(username, password,
                "setMaxPlayers",jsonArgs));
        return response.getCode() != Status.ERROR;
    }

    public String addPlayer(String gameId) throws IOException {
        Map<String,String> payload = new HashMap<>();
        payload.put("gameId",gameId);
        String jsonArgs = new ObjectMapper().writeValueAsString(payload);
        Response response;
        response = HttpClientPost.managerPostTo(new ManagerRequest(username, password,
                "addPlayer",jsonArgs));
        if (response.getCode() == Status.ERROR)
            return null;
        else {
            HashMap<String,String> args=GameRequest.getMapFromData(response.getData());
            return args.get("playerId");
        }
    }

    // endregion

    // region User Commands

    public boolean rollDice(String gameID,String playerId) throws  IOException{
        Response response;
        response=HttpClientPost.userPostTo(new UserRequest(gameID,playerId,"rollDice",""));
        return response.getCode()!=Status.ERROR;
    }
    public boolean buyRoad(String gameId, String playerId, Integer spot) throws IOException {
        Map<String,String> payload = new HashMap<>();
        payload.put("spot",spot.toString());
        String jsonArgs = new ObjectMapper().writeValueAsString(payload);
        Response response;
        response = HttpClientPost.userPostTo( new UserRequest(gameId, playerId,
                "buyRoad/" + spot,jsonArgs));
        return response.getCode() != Status.ERROR;
    }
    public boolean endTurn(String gameId, String playerId) throws IOException {
        Response response;
        response = HttpClientPost.userPostTo(new UserRequest(gameId, playerId,
                "endTurn",""));
        return response.getCode() != Status.ERROR;
    }
    public boolean buyHouse(String gameId, String playerId, Integer spot) throws IOException {
        Map<String,String> payload = new HashMap<>();
        payload.put("spot",spot.toString());
        String jsonArgs = new ObjectMapper().writeValueAsString(payload);
        Response response;
        response = HttpClientPost.userPostTo( new UserRequest(gameId, playerId,
                "buyHouse",jsonArgs));
        return response.getCode() != Status.ERROR;
    }
    public boolean buyCity(String gameId, String playerId, Integer spot) throws IOException {
        Map<String,String> payload = new HashMap<>();
        payload.put("spot",spot.toString());
        String jsonArgs = new ObjectMapper().writeValueAsString(payload);
        Response response;
        response = HttpClientPost.userPostTo(new UserRequest(gameId, playerId,
                "buyCity",jsonArgs));
        return response.getCode() != Status.ERROR;
    }
    public boolean playDevCard(String gameId, String playerId, Integer spot) throws IOException {
        Response response;
        response = HttpClientPost.userPostTo(new UserRequest(gameId, playerId,
                "playDevCard",""));
        return response.getCode() != Status.ERROR;
    }
    public boolean selectPartner(String gameId, String playerId) throws IOException {
        Response response;
        response = HttpClientPost.userPostTo( new UserRequest(gameId, playerId,
                "selectPartner",""));
        return response.getCode() != Status.ERROR;
    }
    public boolean endTrade(String gameId, String playerId) throws IOException {
        Response response;
        response = HttpClientPost.userPostTo( new UserRequest(gameId, playerId,
                "endTrade",""));
        return response.getCode() != Status.ERROR;
    }
    public boolean startTrade(String gameId, String playerId) throws IOException {
        Response response;
        response = HttpClientPost.userPostTo( new UserRequest(gameId, playerId,
                "startTrade",""));
        return response.getCode() != Status.ERROR;
    }



    //endregion

    public void simulation() throws IOException, InterruptedException {
        // Configure the game
        gameID = createGame();
        setMaxPlayers(gameID, 2);
        playersID.add(addPlayer(gameID));
        playersID.add(addPlayer(gameID));
        playersID.add(addPlayer(gameID));
        setMaxPlayers(gameID,1);
        startGame(gameID);
        // Run the game
        while (true) {
            rollDice(gameID,playersID.get(0));
            startTrade(gameID,playersID.get(0));
            selectPartner(gameID, playersID.get(0));
            endTrade(gameID,playersID.get(0));
            buyHouse(gameID, playersID.get(0), 20);
            rollDice(gameID,playersID.get(0));
            sleep(100);
            buyRoad(gameID, playersID.get(1), 42);
            playDevCard(gameID,playersID.get(0),20);
            endTurn(gameID, playersID.get(0));
            sleep(100);
            rollDice(gameID,playersID.get(1));
            startTrade(gameID,playersID.get(1));
            selectPartner(gameID, playersID.get(1));
            endTrade(gameID,playersID.get(1));
            buyHouse(gameID, playersID.get(1), 22);
            sleep(100);
            endTurn(gameID, playersID.get(1));
        }
    }
}