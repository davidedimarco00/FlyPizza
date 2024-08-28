!start.

/*Initial Beliefs*/

//At the beginning the drone knows just where is the pizzeria

pizzeriaLocation(26,26).





+!start <-
    //.print("Drone pronto per ricevere la destinazione, mi trovo nel garage");
    +at(pizzeria);
    .send(pizzeria, tell, at(pizzeria));  // Invia alla pizzeria che il drone è pronto
    .send(robot, tell, brokenDrone(drone1, 49,49)).


//MOVING PLANS
+!moveToDestination(D, X, Y) <-
    //.print(D, " Inizio a muovermi verso la destinazione.");
    -at(pizzeria);
    .send(pizzeria, achieve, left(D, pizzeria)); //dico alla pizzeria che non sono più dentro la pizzeria
    //.print(D, "Sto uscendo dalla pizzeria"); //lo mostro a video
    move(X,Y);
    !continueMoving(D, X, Y).  // Adotta un obiettivo intermedio

+!continueMoving(D, X, Y) : not at(D, pizzeria) <-  // Piano per continuare a muoversi quando non sono più nella pizzeria
    ?current_position(CurrentX, CurrentY);
    //.print(D, "Continuo a muovermi verso la destinazione. Current Position: " , CurrentX, " ", "CurrentY", Y);
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
    //.print(D," ha consegnato la pizza");
    .wait(500); //aspetto 2 secondi
    pizza_delivered;
    ?pizzeriaLocation(LocX, LocY);
    -atDestination(true);
    -order(Drone, X, Y);
    !moveToPizzeria(D, LocX, LocY).






//RETURN TO PIZZERIA

+!moveToPizzeria(D,X,Y) <-
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla pizzeria
        .wait(2000); //aspetto 2 s
        +at(pizzeria);
        .send(pizzeria, tell, at(pizzeria)); //dico alla pizzeria che sono arrivato
    } else {
        //.print("Sto tornando alla pizzeria");
        move(X,Y);
        !moveToPizzeria(D,X,Y);
    }.


//UPDATE BELIEFS
+!receiveOrder(Drone, X,Y) <- //quando ricevo la credenza di ricevere un ordine.
    //.print("Ho ricevuto l'ordine di andare a posizione ",X, " ",Y);

    //qui devo abbassare il numero di pizze da consegnare modificando quindi il modello
    +order(Drone, X, Y);
    .wait(2000); //aspetto 2 secondi prima di partire
    !moveToDestination(Drone, X,Y). //inizio a muovermi verso la destinazione dell'ordine


+at(D, pizzeria) <- //quando rilevo che sono in pizzeria
    .print(D, " Sono in pizzeria."). //lo mostro a video
