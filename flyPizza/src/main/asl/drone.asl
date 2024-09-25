!start.

/*Initial Beliefs*/

//At the beginning the drone knows just where is the pizzeria.

pizzeriaLocation(26,26).


+!start <-
    .my_name(AgentName);
    +at(pizzeria); +charging(no); +batteryLevel(100); +broken(no);
    .send(pizzeria, tell, at(pizzeria, AgentName));  //Comunico alla pizzeria che sono in pizzeria
    .send(pizzeria, tell, charging(no, AgentName)); //Comunico alla pizzeria che non sono in carica
    .send(pizzeria, tell, batteryLevel(100, AgentName)); //Comunico alla pizzeria che ho la carica al 100%
    .send(pizzeria, tell, broken(no, AgentName)). //comunico alla pizzeria che non sono rotto
    //.send(robot, tell, brokenDrone(drone1, 49,49)).




//MOVING PLANS
+!moveToDestination(D, X, Y) <-
    //.print(D, " Inizio a muovermi verso la destinazione.");
    -at(pizzeria);
    .send(pizzeria, achieve, left(pizzeria, D)); //dico alla pizzeria che non sono più dentro la pizzeria
    //.print(D, "Sto uscendo dalla pizzeria"); //lo mostro a video
    move(X,Y);
    !continueMoving(D, X, Y).  // Adotta un obiettivo intermedio

+!continueMoving(D, X, Y) : not at(D, pizzeria) <-  // Piano per continuare a muoversi quando non sono più nella pizzeria
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla destinazione che mi è stata assegnata
        .print("Sono arrivato alla destinazione");
        .wait(2000); //aspetto 2 s
        +atDestination(true); //stop del motore
        !deliverPizza(D); //Consegno la pizza
    } else {
        move(X,Y);
        !continueMoving(D,X,Y)
    }.

//DELIVER PLANS
+!deliverPizza(D) <-
    .print(D," ha consegnato la pizza");
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
    } else {
        //.print("Sto tornando alla pizzeria");
        move(X,Y);
        !moveToPizzeria(D,X,Y);
    }.


//UPDATE BELIEFS
+!receiveOrder(Drone, X,Y) <- //quando ricevo la credenza di ricevere un ordine.
    //.print("Ho ricevuto l'ordine di andare a posizione ",X, " ",Y);

    //qui devo abbassare il numero di pizze da consegnare modificando quindi il modello
    -order(_,_,_);+order(Drone, X, Y);
    !moveToDestination(Drone, X,Y). //inizio a muovermi verso la destinazione dell'ordine


+at(D, pizzeria) <- //quando rilevo che sono in pizzeria
    .print(D, " Sono in pizzeria."). //lo mostro a video
