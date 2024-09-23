!start.

/*Initial Beliefs*/

//At the beginning the drone knows just where is the pizzeria, the battery level and the battery drainRate

pizzeriaLocation(26,26).
consumptionRate(1).


+!start <-
    +at(pizzeria);
    +batteryLevel(100);
    .send(pizzeria, tell, at(pizzeria));  // Invia alla pizzeria che il drone è pronto
    .send(pizzeria, tell, batteryLevel(100)).  // Invia alla pizzeria che il drone è carico


    //.send(robot, tell, brokenDrone(drone1, 49,49)).


//MOVING PLANS
+!moveToDestination(D, X, Y) <-
    -at(pizzeria);
    .send(pizzeria, tell, left(D, pizzeria)); //dico alla pizzeria che non sono più dentro la pizzeria
    move(X,Y);
    !continueMoving(D, X, Y).

+!continueMoving(D, X, Y) : not at(D, pizzeria) <-  // Piano per continuare a muoversi quando non sono più nella pizzeria
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla destinazione che mi è stata assegnata
        .print("Sono arrivato alla destinazione");
        .wait(1000); //aspetto 1 s
        +atDestination(true); //stop del motore
        !deliverPizza(D); //Consegno la pizza
    } else {
        !decreaseBattery;
        move(X,Y);
        !continueMoving(D,X,Y)
    }.


//DELIVER PLANS
+!deliverPizza(D) <-
    .print(D," ha consegnato la pizza");
    pizza_delivered;
    .wait(500);
    ?pizzeriaLocation(LocX, LocY);
    -atDestination(true);
    -order(Drone, X, Y);
    !moveToPizzeria(D, LocX, LocY).




//RETURN TO PIZZERIA

+!moveToPizzeria(D,X,Y) <-
    ?current_position(CurrentX, CurrentY);
    !decreaseBattery;
    if (CurrentX = X & CurrentY = Y) { //se sono arrivato alla pizzeria
        .wait(2000); //aspetto 2 s
        +at(pizzeria);
        ?batteryLevel(CurrentBattery);
        .send(pizzeria, tell, batteryLevel(CurrentBattery)); //dico alla pizzeria il mio livello di carica
        .send(pizzeria, tell, at(pizzeria)); //dico alla pizzeria che sono arrivato
    } else {
        //.print("Sto tornando alla pizzeria");
        !checkBattery(X,Y);
        move(X,Y);
        !moveToPizzeria(D,X,Y);
    }.




//BATTERY MANAGEMENT

+!checkBattery(DestX, DestY) <-
    !calculateBatteryRequired(DestX, DestY, RequiredBattery);
    ?batteryLevel(CurrentBattery);
    //.println("Batteria attuale: ", CurrentBattery, ", Batteria necessaria: ", RequiredBattery);
    if (CurrentBattery >= RequiredBattery) {
        //.println("Batteria sufficiente per raggiungere la destinazione.");
        -sufficientBattery(_);
        +sufficientBattery(yes);
    }
    else {
        .println("Batteria insufficiente. È necessario ricaricare.");
        -sufficientBattery(_);+sufficientBattery(no);
    }.

+!decreaseBattery <-
    ?batteryLevel(CurrentBattery);
    {NewBatteryLevel = CurrentBattery - 1};
    -batteryLevel(CurrentBattery);
    +batteryLevel(NewBatteryLevel).



+!recharge <-
    .println("Sto caricando...");
    .wait(10000);
    -batteryLevel(_);
    +batteryLevel(100);
    .println("Sono carico");
    .send(pizzeria, tell, batteryLevel(100)).






//UPDATE BELIEFS
+!receiveOrder(Drone, X, Y) <-
    +order(Drone, X, Y);
    .wait(2000);
    !checkBattery(X, Y);
    ?sufficientBattery(Status);
    if (Status == yes) {
        .println("Batteria sufficiente. Parto verso la destinazione.");
        !moveToDestination(Drone,X, Y);
    }
    else {
        .print("Batteria insufficiente. Ricarico");
        !recharge;
    }.


+!calculateBatteryRequired(DestX, DestY, RequiredBattery) <-
    ?current_position(CurrentX, CurrentY);
    {DX = DestX - CurrentX};
    {DY = DestY - CurrentY};
    {Distance = math.sqrt(DX * DX + DY * DY)};
    ?consumptionRate(ConsumptionRate);
    {RequiredBattery = Distance * 2 * ConsumptionRate}.


+at(D, pizzeria) <- //quando rilevo che sono in pizzeria
    .print(D, " Sono in pizzeria."). //lo mostro a video
