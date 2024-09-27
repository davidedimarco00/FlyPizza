!start.

/*Initial Beliefs*/

//At the beginning the drone knows just where is the pizzeria.

pizzeriaLocation(26,26).

+!start <-
    .my_name(AgentName);
    +at(pizzeria); +charging(no); +broken(no);
    .send(pizzeria, tell, at(pizzeria, AgentName));  //Comunico alla pizzeria che sono in pizzeria
    .send(pizzeria, tell, charging(no, AgentName)); //Comunico alla pizzeria che non sono in carica
    ?batteryLevel(Level);
    .send(pizzeria, achieve, updateBatteryLevel(Level, AgentName)); //Comunico alla pizzeria che ho la carica al 100%
    .send(pizzeria, tell, broken(no, AgentName)). //comunico alla pizzeria che non sono rotto
    //.send(robot, tell, brokenDrone(drone1, 49,49)).




//MOVING PLANS
+!moveToDestination(D, X, Y) <-
    -at(pizzeria); //tolgo la credenza che sia alla pizzeria
    .send(pizzeria, achieve, left(pizzeria, D)); //dico alla pizzeria che non sono più dentro la pizzeria
    move(X,Y);
    !continueMoving(D, X, Y).  // obiettivo intermedio (prossimo step)



+!continueMoving(D, X, Y) : not at(D, pizzeria) <-  // Piano per continuare a muoversi quando non sono più nella pizzeria
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla destinazione che mi è stata assegnata
        .print("Sono arrivato alla destinazione");
        +atDestination(true); //stop del motore
        !deliverPizza(D); //Consegno la pizza
    } else {
        .wait(200);
        move(X,Y);
        !continueMoving(D,X,Y)
    }.



-!continueMoving(D, X, Y) <-   //fail plans, in some cases happen that the currentPosition is not updated sync
    .print("Waiting for position update...");
    .wait(400);
    !continueMoving(D, X, Y).



//DELIVER PLANS


+!deliverPizza(D) <-
    .print(D," ha consegnato la pizza");
    .wait(2000); //simulo la consegna
    pizza_delivered;
    ?pizzeriaLocation(LocX, LocY);
    -atDestination(true);
    -order(D, X, Y);
    !moveToPizzeria(D, LocX, LocY).



//RETURN TO PIZZERIA

+!moveToPizzeria(D,X,Y) <-
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla pizzeria
        +at(pizzeria);
        .send(pizzeria, tell, at(pizzeria, D)); //dico alla pizzeria che sono arrivato
        .print("Sono in pizzeria");
        !checkBattery(D);
    } else {
        .wait(200); //aspetto per fare vedere a video il movimento altrimenti va troppo veloce
        move(X,Y);
        !moveToPizzeria(D,X,Y);
    }.


-!moveToPizzeria(D, X, Y) <-
    .print("Failed to continue moving. Waiting for position update...");
    .wait(400);
    !moveToPizzeria(D, X, Y).


//BATTERY PERCEPTION CHECK

+!checkBattery(D) <-
    ?batteryLevel(Level);
    .send(pizzeria, achieve, updateBatteryLevel(Level, D));
    .print(D,":ha la batteria al ", Level).


//UPDATE BELIEFS
+!receiveOrder(Drone, X,Y) <- //quando ricevo la credenza di ricevere un ordine.
    //qui devo abbassare il numero di pizze da consegnare modificando quindi il modello
    -order(_,_,_);
    +order(Drone, X, Y);
    !moveToDestination(Drone, X,Y). //inizio a muovermi verso la destinazione dell'ordine


+at(D, pizzeria) <- //quando rilevo che sono in pizzeria
    .print(D, " Sono in pizzeria."). //lo mostro a video
