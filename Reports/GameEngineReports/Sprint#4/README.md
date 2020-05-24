# Build First Two Settlements and Roads
## Build a settlement for free
``` 
{
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "buildSettlement", 
 "arguments":
 {
  "intersection": "number of intersection (integer)" 
 } 
}
```
 - works only in the first two rounds 
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
## Build a road for free
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "buildRoad", 
 "arguments":
 { 
  "start": "number of first intersection (integer)", 
  "end": "number of last intersection (integer)" 
 } 
}
```
 - should be after ```buildSettlement```
 - the intersections order does not matter
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
# Dice
## Roll the dice
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "rollDice", 
 "arguments": null 
}
```
 - works only after the turn starts (after the last ```buildRoad``` or after ```endTurn```)
``` 
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 { 
  "dice_0": "second dice value (integer)", 
  "dice_1": "first dice value (integer)", 
  "player_0": "playerId", 
  "lumber_0": "number of received lumbers (integer)", 
  "wool_0": "number of received wools (integer)", 
  "grain_0": "number of received grains (integer)", 
  "brick_0": "number of received bricks (integer)", 
  "ore_0": "number of received ores (integer)", 
  "resourcesToDiscard_0": "0 or half (rounded down) if rolled seven and has eight or more resources (integer)", 
  "player_1": "playerId", 
  "lumber_1": "number of received lumbers (integer)", 
  "wool_1": "number of received wools (integer)", 
  "grain_1": "number of received grains (integer)", 
  "brick_1": "number of received bricks (integer)", 
  "ore_1": "number of received ores (integer)", 
  "resourcesToDiscard_1": "0 or half (rounded down) if rolled seven and has eight or more resources (integer)" 
 }
}
```
 -  ```_0``` (or ```_1```, ```_2```, ```_3```) groups information for each player
 - ```player_0``` contains the player identifier for which the information with ```_0``` will be about (and so on)
 - if the dice sum is not seven, ```resourcesToDiscard``` is 0
 - if the dice sum is seven, ```lumber```, ```wool```, ```grain```, ```brick```, ```ore```  are 0
 ## Discard half of your resource cards if the dice sum is seven and you have eight or more resource cards
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "discardResources", 
 "arguments":
 { 
  "lumber": "number of lumbers to discard (integer)", 
  "wool": "number of wools to discard (integer)", 
  "grain": "number of grains to discard (integer)", 
  "brick": "number of bricks to discard (integer)", 
  "ore": "number of ores to discard (integer)" 
 } 
}
```
 - for example, if ```resourcesToDiscard_0``` is greater than 0 (the dice sum is seven), the player with the identifier from ```player_0``` must send ```discardResources```, otherwise the game will not continue (available for all players)
``` 
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 { 
  "sentAll": "true or false (boolean)" 
 } 
}
```
 - if the code is 200, the specified resources are moved from the player to the bank
 - if the code is 202, the player must send again ```discardResources```
 - if ```sentAll``` is true, the game can continue
# Robber
## Move the Robber if you rolled seven or you use the Knight development card
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "moveRobber", 
 "arguments":
 { 
  "tile": "a number between 0 and 18 (integer)" 
 } 
}
```
 - if the dice sum is seven, the current player must send ```moveRobber```, otherwise the game will not continue
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 { 
 "0": "playerId from which to steal a resource", 
 "1": "playerId from which to steal a resource" 
 } 
}
```
 - if the code is 200, the robber is moved on the requested tile
 - the players identified by these players identifier have buildings on the tile where the robber was moved
 ## Steal a resource card if you moved the Robber
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "stealResource", 
 "arguments":
 { 
  "answer": "yes or no (just one per request)",
  "player": "playerId" 
 } 
}
```
 - if ```answer``` is ```no```, the current player will not steal any resource card and the game continues
 - ```player``` should contain the player identifier from the ```moveRobber``` response arguments
 - if the current player moved the robber or uses the Knight development card, he must send ```stealResource```, otherwise the game will not continue
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 { 
  "resource": "random stolen resource" 
 } 
}
```
 - if the ```answer``` was ```yes``` and the code is 200, the stolen resource is moved from the selected player to the current player
# Trade
## Trade with other player
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "playerTrade", 
 "arguments":
 { 
  "lumber_o": "number of lumbers to offer (integer)", 
  "wool_o": "number of wools to offer (integer)", 
  "grain_o": "number of grains to offer (integer)", 
  "brick_o": "number of bricks to offer (integer)", 
  "ore_o": "number of ores to offer (integer)", 
  "lumber_r": "number of requested lumbers (integer)", 
  "wool_r": "number of requested wools (integer)", 
  "grain_r": "number of requested grains (integer)", 
  "brick_r": "number of requested bricks (integer)", 
  "ore_r": "number of requested ores (integer)" 
 } 
}
```
 - ```playerTrade``` can be sent whenever between ```rollDice``` and ```endTurn``` (if there is no special case)
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
## Express your intention to take part in the trade set by the current player
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "wantToTrade", 
 "arguments": null 
}
```
 - the current player is not allowed to send ```wantToTrade```
```
{ 
  "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
## Notify the current player about the other players who want to trade with him
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "sendPartners", 
 "arguments": null 
}
```
 - the current player must send ```sendPartner``` if he sent ```playerTrade```
``` 
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 { 
  "player_0": "playerId", 
  "player_1": "playerId" 
 } 
}
```
## Select the player with who you want to trade
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "selectPartner", 
 "arguments":
 { 
  "player": "playerId" 
 } 
}
```
 - the current player must send ```selectPartner``` if he sent ```sendPartners```
 - ```player``` should contain the player identifier from the ```sendPartners``` response arguments
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
 - if the code is ```200```, the specified resources in ```playerTrade``` are transferred from the current player to the selected player and vice versa
## Trade directly with the Bank or via a Port
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "noPlayerTrade", 
 "arguments":
 { 
  "port": "-1 if with bank or the number of the intersection of a port (integer)", 
  "offer": "offered resource", 
  "request": "requested resource" 
 } 
}
```
 - ```noPlayerTrade``` is for bank trade (if sent port is -1) and for port trade otherwise
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
 - if the code is ```200```, the specified resources are transferred from the current player to the bank and vice versa
# Buy Properties
## Buy a road
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "buyRoad", 
 "arguments":
 { 
  "start": "number of first intersection (integer)", 
  "end": "number of last intersection (integer)" 
 } 
}
```
 - ```buyRoad``` can be sent whenever between ```rollDice``` and ```endTurn``` (if there is no special case)
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
## Buy a settlement
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "buySettlement", 
 "arguments":
 { 
  "intersection": "number of intersection (integer)" 
 } 
 }
 ```
 - ```buySettlement``` can be sent whenever between ```rollDice``` and ```endTurn``` (if there is no special case)
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
## Buy a city
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "buyCity", 
 "arguments":
 { 
   "intersection": "number of intersection (integer)" 
 } 
}
```
 - ```buyCity``` can be sent whenever between ```rollDice``` and ```endTurn``` (if there is no special case)
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
# Developments
## Buy a development
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "buyDevelopment", 
 "arguments": null 
}
```
 - ```buyDevelopment``` can be sent whenever between ```rollDice``` and ```endTurn``` (if there is no special case)
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 {  
  "development": "random bought development"  
 }
}
```
## Use a development
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "useDevelopment", 
 "arguments":
 { 
  "development": "knight or monopoly or roadBuilding or yearOfPlenty (just one per request)" 
 } 
}
```
 - ```useDevelopment``` can be sent whenever between ```rollDice``` and ```endTurn``` (if there is no special case)
 - for ```knight```, the current player must send next ```moveRobber``` and ```stealResource``` requests
 - for ```monopoly```, the current player must send next ```takeResourceFromAll``` request
 - for ```roadBuilding```, the current player must send next two valid ```buildDevelopmentRoad``` requests
 - for ```yearOfPlenty```, the current player must send next ```takeTwoResources``` request
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
## Take all resource cards of the specified type from all the other players if you use Monopoly development card
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "takeResourceFromAll", 
 "arguments":
 { 
  "resource": "requested resource" 
 } 
}
```
 - ```takeResourceFromAll``` must be sent after a valid ```useDevelopment``` with ```monopoly``` request
``` 
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 { 
  "player_0": "playerId", 
  "resources_0": "number of resources to steal (integer)", 
  "player_1": "playerId", 
  "resources_1": "number of resources to steal (integer)" 
 } 
}
``` 
 - ```resources_0``` contains the number of resources of the requested type that were stolen from ```player_0```
 - if the code is 200, all the resources of the requested type are moved from all the players who have ```resources``` greater than 0 to the current player
## Build two roads for free if you use Road Building development card (use this request twice)
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "buildDevelopmentRoad", 
 "arguments":
 { 
  "start": "number of first intersection", 
  "end": "number of last intersection" 
 } 
}
```
 - this request must be sent after a valid ```useDevelopment``` with ```roadBuilding``` request
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
## Take two resource cards if you use Year of Plenty development card
```
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "takeTwoResources", 
 "arguments":
 { 
  "resource_0": "second requested resource", 
  "resource_1": "first requested resource" 
 } 
}
```
  - this request must be sent after a valid ```useDevelopment``` with ```yearOfPlenty``` request
 ``` 
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments": null 
}
```
 - if the code is 200, the requested resources are moved from the bank to the current player
# Update
## See what you already own, what you can buy, where you can build, if you have the Largest Army or the Longest Road and how many public or hidden Victory Points you have 
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "update", 
 "arguments": null 
}
```
 - ```update``` can be sent whenever
```
{
 "code": "HttpStatus code",
 "status": "message (success or error type)",
 "arguments":
 {
  "active": "true or false (boolean)",
  "lumber": "number of owned lumbers (integer)",
  "wool": "number of owned wools (integer)",
  "grain": "number of owned grains (integer)",
  "brick": "number of owned bricks (integer)",
  "ore": "number of owned ores (integer)",
  "knight": "number of owned knights (integer)",
  "monopoly": "number of owned monopolies (integer)",
  "roadBuilding": "number of owned roadBuildings (integer)",
  "victoryPoint": "number of owned victoryPoints (integer)",
  "yearOfPlenty": "number of owned yearsOfPlenty (integer)",
  "settlements":
  [
   "number of owned intersection (integer)",
   "number of owned intersection (integer)"
  ],
  "cities":
  [
   "number of owned intersection (integer)",
   "number of owned intersection (integer)"
  ],
  "roads":
  [
   [
    "number of start intersection (integer)",
    "number of end intersection (integer)"
   ],
   [
    "number of start intersection (integer)",
    "number of end intersection (integer)"
   ]
  ],
  "usedKnights": "number of used Knight development cards (integer)",
  "roadsToBuild": "number of remaining roads to build when using Road Building development card",
  "hasLargestArmy": "true or false (boolean)",
  "hasLongestRoad": "true or false (boolean)",
  "publicScore": "number of public points, without Victory Points development cards (integer)",
  "hiddenScore": "number of hidden points, with Victory Points development cards (integer)",
  "canBuyRoad": "true or false (boolean)",
  "canBuySettlement": "true or false (boolean)",
  "canBuyCity": "true or false (boolean)",
  "canBuyDevelopment": "true or false (boolean)",
  "availableSettlementPositions":
  [
   "number of available intersection (integer)",
   "number of available intersection (integer)"
  ],
  "availableCityPositions":
  [
   "number of available intersection (integer)",
   "number of available intersection (integer)"
  ],
  "availableRoadPositions":
  [
   [
    "number of start intersection (integer)",
    "number of end intersection (integer)"
   ],
   [
    "number of start intersection (integer)",
    "number of end intersection (integer)"
   ]
  ]
 }
}
```
 - ```active``` refers to in the game (not stepped over when when it is your turn)
 - ```canBuy``` refers to ```affords```
 - ```available``` refers to positions that respect the game rules
# Turn
## End you turn in order to let the next player start his
``` 
{ 
 "gameId": "gameId", 
 "playerId": "playerId", 
 "command": "endTurn", 
 "arguments": null 
}
```
 -  ```endTurn``` can be sent whenever after ```rollDice```
```
{ 
 "code": "HttpStatus code", 
 "status": "message (success or error type)", 
 "arguments":
 {
  "nextPlayer": "playerId"
 }
} 
```
# Dice
## Error messages
 - You do not have more than seven resource cards in order to discard half of them.
## Success messages
 - The dice sum is seven.
 - The dice sum is not seven.
 - The resource cards were discarded successfully.
# Player 
## Resource Cards
### Error Messages
#### Not Enough Resource Cards
 - You do not have enough Lumber resource cards.
 - You do not have enough Wool resource cards.
 - You do not have enough Grain resource cards.
 - You do not have enough Brick resource cards.
 - You do not have enough Ore resource cards.
#### No Resource Card
 - You do not have Lumber resource cards.
 - You do not have Wool resource cards.
 - You do not have Grain resource cards.
 - You do not have Brick resource cards.
 - You do not have Ore resource cards.
## Development Cards
### Error Messages
- You do not have Knight development cards.
- You do not have Monopoly development cards.
- You do not have Road Building development cards.
- You do not have Year of Plenty development cards.
### Success Messages
- The development was bought successfully.
- You can use Knight development card.
- You can use Monopoly development card.
- You can use Road Building development card.
- You can use Year of Plenty development card.
- The resource cards were stolen successfully.
- The resource cards were taken successfully.
## Player Turn
### Error Messages
- It is not your turn.
### Success Messages
- The turn was changed successfully.
# Bank
## Resource Cards
### Error Messages
#### Not Enough Resource Cards
- The bank does not have enough Lumber resource cards.
- The bank does not have enough Wool resource cards.
- The bank does not have enough Grain resource cards.
- The bank does not have enough Brick resource cards.
- The bank does not have enough Ore resource cards.
#### No Resource Card
- The bank does not have Lumber resource cards.
- The bank does not have Wool resource cards.
- The bank does not have Grain resource cards.
- The bank does not have Brick resource cards.
- The bank does not have Ore resource cards.
## Development Cards
### Error Messages
- The bank does not have any development cards.
- The bank does not have Knight development cards.
- The bank does not have Monopoly development cards.
- The bank does not have Road Building development cards.
- The bank does not have Year of Plenty development cards.
## Bank Roads
### Error Messages
- You do not have roads in bank.
- You do not have settlements in bank.
- You do not have cities in bank.
# Roads
## Error Messages
- Invalid position for road.
- It does not connect to your last intersection.
- It does not connect to one of your intersections.
- It does not connect to one of your roads.
- Road already existent.
- You have no more roads to build.
## Success Messages
- The road was built successfully.
- The road was built successfully. You have no more roads to build using the Road Building development card.
- The road was bought successfully.
# Settlements
## Error Messages
- Invalid position for settlement.
- Intersection already occupied.
- The two roads distance rule is not satisfied.
- It does not connect to one of your roads.
- You have no more settlements to build.
## Success Messages
- The settlement was built successfully.
- The settlement was bought successfully.
# Cities
## Error Messages
- Invalid position for city.
- You have no more cities to build.
## Success Messages
- The city was bought successfully.
# Robber
## Error Messages
- You can not let the robber on the same tile.
- You can not steal a resource card from yourself.
- The player does not have resource cards.
## Success Messages
- The robber was moved successfully.
- The resource card was stolen successfully.
# Trade
## Player Trade
### Error Messages
- No trade available.
- You are already in trade.
- The selected player is not in trade.
- The offer does not match the port.
### Success Messages
- The trade has started successfully.
- You can take part in the trade.
- The trade partners were sent successfully.
- The trade was made successfully.
## Bank and Port Trade
### Success Messages
- The trade was made successfully.
# Game
## Success Messages
- The game has ended successfully.
- The game has ended because there are not enough active players.
# Requests
## Error Messages
- Invalid request.
- Forbidden request.
# Automaton
## Error Messages
- The message has no assigned action.