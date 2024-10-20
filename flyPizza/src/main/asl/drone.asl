!start.
/* Initial Beliefs */

// At the beginning the drone knows just where is the pizzeria.
pizzeriaLocation(26,26).
enginePower(low).

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
    ?enginePower(Mode);
    !moveToDestination(X,Y,Mode);  // Inizio a muovermi verso la destinazione dell'ordine
    !deliverPizza(Drone);
    ?enginePower(M);
    !moveToDestination(26,26,M);
    .send(pizzeria, tell, at(pizzeria, Drone));
    !checkBattery(Drone).

// MOVE TO DESTINATION

+!moveToDestination(X, Y, Mode) : not broken(_, yes) <-
    ?current_position(CurrentX, CurrentY);
    ?batteryLevel(Level);

 // Calcola la distanza tra la posizione attuale e la destinazione
     {Distance = math.sqrt((X - CurrentX) * (X - CurrentX) + (Y - CurrentY) * (Y - CurrentY))};

     // Calcolo del consumo di batteria per la modalita full
     FullModeBatteryRequired = Distance * 2;  // Modalità FULL consuma 2 unità per unità di distanza

     // Decisione della modalità in base al livello della batteria

     if (Level >= FullModeBatteryRequired) {
         !switchToFullPower;
     } else {
          if (Level <= FullModeBatteryRequired) {
             !switchToLowPower;
         } else {
             -broken(D, _);
             +broken(D, yes);
             .send(pizzeria, tell, broken(D, CurrentX, CurrentY));  // Informo la pizzeria del guasto
         }
     };

    if (CurrentX = X & CurrentY = Y) {
        .print("Arrivato a ", X, ", ", Y);
    } else {
        ?enginePower(M);
        if (M = full) {
             .wait(200);
        } else {
            .wait(400);
        }

        if (Level <= 0) { //gestione dell'errore della batteria scarica.
            -broken(D, _);
            +broken(D, yes);
            .print(D, "è GUASTO per BATTERIA SCARICA !");
        } else {
            //check engine performance
            ?enginePower(M);   //catturo la modalità
            move(X, Y, M);
            !moveToDestination(X, Y, M);
        }
    }.


-!moveToDestination(X, Y, Mode) <-
    .print("In attesa della posizione attuale...");
    .wait(200);
    ?enginePower(M);
    !moveToDestination(X,Y, M).



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



//QUANDO RILEVO DI ESSERE ROTTO


+broken(D, yes) <- //
    ?current_position(CurrentX, CurrentY);
    .drop_all_intentions; //https://www.emse.fr/~boissier/enseignement/maop11/doc/jason-api/api/jason/stdlib/drop_all_intentions.html
    .drop_all_desires;
    .print("Il ",D," è guasto a ",CurrentX," ", CurrentY, ". Tutte le INTENZIONI E DESIRED sono interrotti lo dico alla pizzeria e al robot.");
    .send(robot,tell, brokenDrone(D,CurrentX, CurrentY)).

+batteryLevel(Level) <-
    .my_name(D);
     if (Level = 100) {
        -charging(_);
        +charging(no);
        .send(pizzeria, tell, charging(no, D));
        .send(pizzeria, tell, at(pizzeria, D));

     }.


//switch tra le varie modalità


+!switchToFullPower <-
    -enginePower(low);
    +enginePower(full).

+!switchToLowPower <-
    -enginePower(full);
    +enginePower(low).