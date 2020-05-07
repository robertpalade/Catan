package catan.game.turn;

import catan.API.response.Code;
import catan.API.response.Messages;
import catan.API.response.Response;
import catan.game.game.Game;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ankzz.dynamicfsm.action.FSMAction;
import com.github.ankzz.dynamicfsm.fsm.FSM;
import org.apache.http.HttpStatus;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TurnFlow {
    public final Game game;
    public FSM fsm;
    public Response response;
    public Messages messages;

    public TurnFlow(Game game) throws IOException, SAXException, ParserConfigurationException {
        this.game = game;
        messages = Messages.getInstance();
        fsm = new FSM("stateConfig.xml", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                response = new Response(HttpStatus.SC_ACCEPTED, "The message has no assigned function.", "");
                return true;
            }
        });
        fsm.setAction("rollDice", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                game.rollDice();
                return false;
            }
        });
        fsm.setAction("rollSeven", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                try {
                    String data = new ObjectMapper().writeValueAsString(arguments);
                    response = new Response(HttpStatus.SC_OK, "The dice sum is seven.", data);
                    return true;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        fsm.setAction("rollNotSeven", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                try {
                    String data = new ObjectMapper().writeValueAsString(arguments);
                    response = new Response(HttpStatus.SC_OK, "The dice sum is not seven.", data);
                    return true;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        fsm.setAction("moveRobber", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, Integer> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, Integer>>(){});
                int tile = requestArguments.get("tile");
                Code code = game.moveRobber(tile);
                if (code == null) {
                    response = new Response(HttpStatus.SC_OK, "The robber was moved successfully.", "");
                }
                response = new Response(HttpStatus.SC_ACCEPTED, messages.getMessage(code), "");
                return true;
            }
        });
        fsm.setAction("stealResource", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, String> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, String>>(){});
                String playerId = requestArguments.get("playerId");
                //TODO: Add game.stealResource(String playerId).
                response = new Response(HttpStatus.SC_OK, "The resource was stolen successfully.", "");
                return true;
            }
        });
        fsm.setAction("playerTrade", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                //TODO: Create offerRequest map.
                //TODO: Call game.playerTrade(Map<String, Integer> offerRequest).
                response = new Response(HttpStatus.SC_OK, "The trade has started successfully.", "");
                return true;
            }
        });
        fsm.setAction("sendOpponents", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                //TODO: Add game.sendOpponents().
                response = new Response(HttpStatus.SC_OK, "The trade partners were sent successfully.", "");
                return true;
            }
        });
        fsm.setAction("selectOpponent", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, String> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, String>>(){});
                if (requestArguments != null) {
                    String playerId = requestArguments.get("playerId");
                }
                //TODO: Add game.selectOpponent(String playerId).
                response = new Response(HttpStatus.SC_OK, "The trade was made successfully.", "");
                return true;
            }
        });
        fsm.setAction("noPlayerTrade", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, String> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, String>>(){});
                String firstOffer = requestArguments.get("offer_1");
                String secondOffer = requestArguments.get("offer_2");
                String thirdOffer = requestArguments.get("offer_3");
                String request = requestArguments.get("request");
                //TODO: Add game.noPlayerTrade(String firstOffer, secondOffer, thirdOffer, String request).
                response = new Response(HttpStatus.SC_OK, "The trade was made successfully.", "");
                return true;
            }
        });
        fsm.setAction("buildSettlement", new FSMAction() {
            @Override
            public boolean action(String curState, String message, String nextState, Object args) {
                response = new Response(HttpStatus.SC_OK, "The settlement was built successfully.", "");
                if (!game.buildSettlement(Integer.parseInt(((HashMap<String, String>) args).get("intersection")))) {
                    response = new Response(HttpStatus.SC_ACCEPTED, "Placing the house is not possible!", "");
                    return false;
                }
                return true;
            }
        });
        fsm.setAction("buildRoad", new FSMAction() {
            @Override
            public boolean action(String curState, String message, String nextState, Object args) {
                response = new Response(HttpStatus.SC_OK, "The road was built successfully.", "");
                if (!game.buildRoad(Integer.parseInt(((HashMap<String, String>) args).get("start")),
                        Integer.parseInt(((HashMap<String, String>) args).get("end")))) {
                    response = new Response(HttpStatus.SC_ACCEPTED, "Placing the road is not possible!", "");
                    return false;
                }
                game.changeTurn(1);
                return true;
            }
        });
        fsm.setAction("buyRoad", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, Integer> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, Integer>>(){});
                int start = requestArguments.get("start");
                int end = requestArguments.get("end");
                if (game.buyRoad(start, end)) {
                    response = new Response(HttpStatus.SC_OK, "The road was built successfully.", "");
                    return true;
                }
                response = new Response(HttpStatus.SC_ACCEPTED, "You do not have enough resources or the spot is unavailable.", "");
                return false;            }
        });
        fsm.setAction("buySettlement", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, Integer> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, Integer>>(){});
                int intersection = requestArguments.get("intersection");
                if (game.buySettlement(intersection)) {
                    response = new Response(HttpStatus.SC_OK, "The settlement was built successfully.", "");
                    return true;
                }
                response = new Response(HttpStatus.SC_ACCEPTED, "You do not have enough resources or the spot is unavailable.", "");
                return false;
            }
        });
        fsm.setAction("buyCity", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, Integer> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, Integer>>(){});
                int intersection = requestArguments.get("intersection");
                if (game.buyCity(intersection)) {
                    response = new Response(HttpStatus.SC_OK, "The city was built successfully.", "");
                    return true;
                }
                response = new Response(HttpStatus.SC_ACCEPTED, "You do not have enough resources or the spot is unavailable.", "");
                return false;
            }
        });
        fsm.setAction("buyDevelopment", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, String> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, String>>(){});
                String development = requestArguments.get("development");
                //TODO: Add game.buyDevelopment(String development).
                response = new Response(HttpStatus.SC_OK, "The development was built successfully.", "");
                return true;
            }
        });
        fsm.setAction("useDevelopment", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                Map<String, String> requestArguments = new ObjectMapper().convertValue(arguments,
                        new TypeReference<HashMap<String, String>>(){});
                String development = requestArguments.get("development");
                String resource = requestArguments.get("resource");
                //TODO: Add game.useDevelopment(String development, String resource).
                fsm.ProcessFSM("useRoadBuilding");
                response = new Response(HttpStatus.SC_OK, "The development was used successfully.", "");
                return true;
            }
        });
        fsm.setAction("endTurn", new FSMAction() {
            @Override
            public boolean action(String currentState, String message, String nextState, Object arguments) {
                game.changeTurn(1);
                response = new Response(HttpStatus.SC_OK, "The turn was changed successfully.", "");
                return true;
            }
        });
    }
}
