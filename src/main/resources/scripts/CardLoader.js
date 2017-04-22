var PowerCard = Java.type("model.PowerCard");

var loadCards = function(cardJSON, deckList){
    var cards = JSON.parse(cardJSON);
    cards.forEach(function(card){
        card.onDamage = new Function("owner", "players", "dice", "action", "actionEngine", card.onDamage);
        card.onAttack = new Function("owner", "players", "dice", "action", "actionEngine", card.onAttack);
        card.onRoll = new Function("owner", "players", "dice", "action", "actionEngine", card.onRoll);
        card.onHealthGain = new Function("owner", "players", "dice", "action", "actionEngine", card.onHealthGain);
        card.onPointGain = new Function("owner", "players", "dice", "action", "actionEngine", card.onPointGain);
        card.onEnergyGain = new Function("owner", "players", "dice", "action", "actionEngine", card.onEnergyGain);
        card.onMove = new Function("owner", "players", "dice", "action", "actionEngine", card.onMove);
        card.onTurnStart = new Function("owner", "players", "dice", "action", "actionEngine", card.onTurnStart);
        card.onTurnEnd = new Function("owner", "players", "dice", "action", "actionEngine",card.onTurnEnd);

        deckList.add(new PowerCard(card.title, card.cost, card.description, card.type, card));
    });

}