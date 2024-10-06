!start.
/* Initial Beliefs */

// At the beginning the drone knows just where is the pizzeria.
pizzeriaLocation(26,26).

+!start <-
    .my_name(AgentName);
    +charging(no);
    .send(pizzeria, tell, at(pizzeria, AgentName));  // Comunico alla pizzeria che sono in pizzeria
    .send(pizzeria, tell, charging(no, AgentName));  // Comunico alla pizzeria che non sono in carica
    ?batteryLevel(Level);
    .send(pizzeria, achieve, updateBatteryLevel(Level, AgentName)); // Comunico alla pizzeria che ho la carica al 100%
    .send(pizzeria, tell, broken(AgentName, no)).  // Comunico alla pizzeria che non sono rotto

// RECEIVE ORDER AND DELIVERY

+!receiveOrder(Drone, X,Y) <-  // QUANDO RICEVO UN ORDINE
    -order(_,_,_);
    +order(Drone, X, Y);
    .send(pizzeria, achieve, left(pizzeria, Drone));
    !moveToDestination(X,Y);  // Inizio a muovermi verso la destinazione dell'ordine
    !deliverPizza(Drone);
    !moveToDestination(26,26);
    .send(pizzeria, tell, at(pizzeria, Drone));
    !checkBattery(Drone).

// MOVE TO DESTINATION

+!moveToDestination(X, Y) : not broken(_, yes) <-
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) {
        .print("Arrivato a ", X, ", ", Y);
    } else {
        .wait(200);
        move(X, Y);
        !moveToDestination(X, Y);
    }.

-!moveToDestination(X, Y) <-
    .print("In attesa della posizione attuale...");
    !moveToDestination(X,Y).

// DELIVER PIZZA

+!deliverPizza(D) : not broken(_, yes) <-  // Controllo che il drone non sia rotto
    .print(D, " ha consegnato la pizza");
    .wait(500);  // Simulo la consegna
    pizza_delivered.

// TORNO ALLA PIZZERIA

// QUI RIUSO IL PIANO moveToDestination con le coordinate cambiate

// CHECK DELLA BATTERIA

+!checkBattery(D) : not broken(_, yes)  <-  // Controllo che il drone non sia rotto
    ?batteryLevel(Level);
    .print("FACCIO IL CHECK DELLA BATTERIA ED HO ", Level, "%");
    .send(pizzeria, achieve, updateBatteryLevel(Level, D));
    if (Level <= 50) {
        .send(pizzeria, tell, charging(yes, D));
        !charge(D);
    } else {
        .send(pizzeria, achieve, processOrderQueue);
    }.


// NEL CASO SONO SCARICO MI RICARICO

+!charge(D) : not broken(_, yes) <-  // Controllo che il drone non sia rotto
    .print(D, " IN RICARICA............");
    .wait(4000);  // Aspetto per simulare la ricarica
    charge_drone(D);  // La batteria viene ricaricata e viene aggiunta la credenza come percezione dall'environment
    -charging(_);
    +charging(no);  // Tolgo le credenze dal drone stesso
    .send(pizzeria, tell, charging(no, D));  // Informo la pizzeria che il drone non è più in carica
    .send(pizzeria, achieve, updateBatteryLevel(100, D)).


//---------------------------BROKEN STATE------------------------------------------

+broken(D, yes) <-

    .print("Il drone è guasto. Tutte le operazioni sono interrotte.").

+broken(D, no) <-
    -broken(D, yes).
