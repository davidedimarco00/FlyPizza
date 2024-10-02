!start.

/*Initial Beliefs*/

//At the beginning the drone knows just where is the pizzeria.

pizzeriaLocation(26,26).

+!start <-
    .my_name(AgentName);
    +at(pizzeria); +charging(no); //+broken(no);
    .send(pizzeria, tell, at(pizzeria, AgentName));  //Comunico alla pizzeria che sono in pizzeria
    .send(pizzeria, tell, charging(no, AgentName)); //Comunico alla pizzeria che non sono in carica
    ?batteryLevel(Level);
    .send(pizzeria, achieve, updateBatteryLevel(Level, AgentName)); //Comunico alla pizzeria che ho la carica al 100%
    .send(pizzeria, tell, broken(no, AgentName)). //comunico alla pizzeria che non sono rotto
    //.send(robot, tell, brokenDrone(drone1, 49,49)).




//MOVING PLANS
+!moveToDestination(D, X, Y) : not at(D, pizzeria) & charging(no) & broken(no) <-
    -at(pizzeria); //tolgo la credenza che sia alla pizzeria
    .send(pizzeria, achieve, left(pizzeria, D)); //dico alla pizzeria che non sono più dentro la pizzeria
    move(X,Y);
    !continueMoving(D, X, Y).  // obiettivo intermedio (prossimo step)

+!moveToDestination(D, X, Y) : broken(yes) <-
    .print("Non posso muovermi, SONO ROTTO").



+!continueMoving(D, X, Y) : not at(D, pizzeria) & charging(no) & broken(no) <-  // Piano per continuare a muoversi quando non sono più nella pizzeria e si ha batteria
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla destinazione che mi è stata assegnata
        +atDestination(true); //stop del motore
        !deliverPizza(D); //Consegno la pizza
    } else {
        .wait(200);
        move(X,Y);
        !continueMoving(D,X,Y);
    }.

+!continueMoving(D, X, Y) : broken(yes)  <-
    .print(D, " in attesa di aiuto alla posizione ", X, Y).



-!continueMoving(D, X, Y) <-
    .print("Waiting for position update...");
    .wait(400);
    !continueMoving(D, X, Y).



//DELIVER PLANS


+!deliverPizza(D) <-
    .print(D," ha consegnato la pizza");
    .wait(4000); //simulo la consegna
    pizza_delivered;
    ?pizzeriaLocation(LocX, LocY);
    -atDestination(true);
    -order(D, X, Y);
    !moveToPizzeria(D, LocX, LocY).



//RETURN TO PIZZERIA

+!moveToPizzeria(D,X,Y) : not at(D, pizzeria) & charging(no) & broken(no)  <-
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla pizzeria
        +at(pizzeria);
        .send(pizzeria, tell, at(pizzeria, D)); //dico alla pizzeria che sono arrivato
        !checkBattery(D);
    } else {
        .wait(200); //aspetto per fare vedere a video il movimento altrimenti va troppo veloce
        move(X,Y);
        !moveToPizzeria(D,X,Y);
    }.


+!moveToPizzeria(D,X,Y) : not at(D, pizzeria) & broken(yes)  <-
    .print("sono rotto!!!!!!!").


-!moveToPizzeria(D, X, Y) <-
    .print("Failed to continue moving. Waiting for position update...");
    .wait(400);
    !moveToPizzeria(D, X, Y).


//BATTERY PERCEPTION CHECK AND RECHARGE

+!checkBattery(D) <-
    ?batteryLevel(Level);
    .send(pizzeria, achieve, updateBatteryLevel(Level, D));
    if (Level <= 30) { //se sono scarico a meno del 30 allora mi metto a caricare
        -charging(_);+charging(yes); //rimuovo le precedenti credenze e poi lo dico alla pizzeria
        .send(pizzeria, tell, charging(yes, D));
        !charge(D);
    }.


+!charge(D) <-
    .print(D," IN RICARICA..........");
    .wait(10000); //aspetto 10 secondi giusto per simulare la ricarica
    charge_drone(D); //la batteria viene ricaricata e viene aggiunta la credenza come percezione dall'environment
    -charging(_);+charging(no); //tolgo le credenze dal drone stesso
    .send(pizzeria, tell, charging(no, D)); //informo la pizzeria che il drone non è piu in carica
    .send(pizzeria, achieve, updateBatteryLevel(100,D)).//invio il livello di batteria alla pizzeria

//---------------------------BROKEN STATE------------------------------------------

+!communicateToPizzeria(CurrentX, CurrentY) <-
    .send(pizzeria, tell, broken(yes)). //dico alla pizzeria che sono rotto





//UPDATE BELIEFS
+!receiveOrder(Drone, X,Y) <- //quando ricevo la credenza di ricevere un ordine.
    -order(_,_,_);
    +order(Drone, X, Y);
    !moveToDestination(Drone, X,Y). //inizio a muovermi verso la destinazione dell'ordine








+broken(yes) <-
    ?current_position(CurrentX, CurrentY);
    !communicateToPizzeria(CurrentX, CurrentY);
    .print("Sono guasto alla posizione ", CurrentX, " ", CurrentY).
    //qui devo mandare un messaggio al robot con la mia posizione che mi deve venire a recuperare.


/*+at(D, pizzeria) <- //quando rilevo che sono in pizzeria
    .print(D, " Sono in pizzeria."). //lo mostro a video*/
