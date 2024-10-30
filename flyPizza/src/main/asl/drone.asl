!start.
/* Initial Beliefs */

// At the beginning the drone knows just where is the pizzeria.
pizzeriaLocation(26,26).
enginePower(low).

+!start <-
    .my_name(AgentName);
    +charging(no);
    .send(pizzeria, tell, at(pizzeria, AgentName));
    .send(pizzeria, tell, charging(no, AgentName));
    ?batteryLevel(Level);
    .send(pizzeria, achieve, updateBatteryLevel(Level, AgentName));
    .send(pizzeria, tell, broken(AgentName, no)).

// RECEIVE ORDER AND DELIVERY

+!receiveOrder(Drone, X,Y) <-  //when order is received
    -order(_,_,_);
    +order(Drone, X, Y);
    .send(pizzeria, achieve, left(pizzeria, Drone));
    ?enginePower(Mode);
    !moveToDestination(X,Y,Mode);  //start move to destination
    !deliverPizza(Drone);
    ?enginePower(M);
    !moveToDestination(26,26,M);
    .send(pizzeria, tell, at(pizzeria, Drone));
    !checkBattery(Drone).

// MOVE TO DESTINATION

+!moveToDestination(X, Y, Mode) : not broken(_, yes) <-
    ?current_position(CurrentX, CurrentY);
    ?batteryLevel(Level);
    .my_name(D);
    //calculate distance from the actual position to destination
     {Distance = math.sqrt((X - CurrentX) * (X - CurrentX) + (Y - CurrentY) * (Y - CurrentY))};

     //calculate battery required in full mode
     FullModeBatteryRequired = Distance * 2;  // Modalità FULL consuma 2 unità per unità di distanza
     if (Level >= FullModeBatteryRequired) {
         !switchToFullPower;
     } else {
          if (Level <= FullModeBatteryRequired) {
             !switchToLowPower;
         } else {
             -broken(D, _);
             +broken(D, yes);
             .send(pizzeria, tell, broken(D, CurrentX, CurrentY));  //send message to pizzeria that i am broken
         }
     };

    if (CurrentX = X & CurrentY = Y) {
        .print("Arrived at ", X, ", ", Y);
    } else {
        ?enginePower(M);
        if (M = full) {
             .wait(200);
        } else {
            .wait(400);
        }

        if (Level <= 0) { //handling battery low
             break_drone(D);
            .print(D, "is broken due to battery low !");
        } else {
            //check engine performance
            ?enginePower(M);
            move(X, Y, M);
            !moveToDestination(X, Y, M);
        }
    }.


-!moveToDestination(X, Y, Mode) <-
    .print("Waiting for position update...");
    .wait(200);
    ?enginePower(M);
    !moveToDestination(X,Y, M).



// DELIVER PIZZA

+!deliverPizza(D) : not broken(_, yes) <-
    .print(D, " has delivered pizza");
    .wait(500);
    pizza_delivered.

// COME BACK TO PIZZERIA

// QUI RIUSO IL PIANO moveToDestination con le coordinate cambiate

// BATTERY CHECK

+!checkBattery(D) : not broken(_, yes)  <-
    ?batteryLevel(Level);
    .print("i'M CHECKING THE BATTERY. I HAVE ", Level, "%");
    .send(pizzeria, achieve, updateBatteryLevel(Level, D));
    if (Level <= 50) {
        .send(pizzeria, tell, charging(yes, D));
        !charge(D);
    } else {
        .send(pizzeria, achieve, processOrderQueue);
    }.


// IF I AM NOT CHARGE I CHARGE

+!charge(D) : not broken(_, yes) <-
    .print(D, " CHARGING............");
    .wait(2000);
    charge_drone(D);
    -charging(_);
    +charging(no);
    .send(pizzeria, tell, charging(no, D)).




//WHEN I M BROKEN


+broken(D, yes) <- //
    ?current_position(CurrentX, CurrentY);
    .drop_all_intentions; //https://www.emse.fr/~boissier/enseignement/maop11/doc/jason-api/api/jason/stdlib/drop_all_intentions.html
    .drop_all_desires;
    .my_name(D);
    .print("The ",D," is broken at ",CurrentX," ", CurrentY, ". All intention are no more available");
    .send(robot,tell, brokenDrone(D,CurrentX, CurrentY)).


+batteryLevel(Level) <-
    .my_name(D);
     if (Level = 100) {
        -charging(_);
        +charging(no);
        .send(pizzeria, tell, charging(no, D));
        .send(pizzeria, tell, at(pizzeria, D));
        .send(pizzeria, achieve, updateBatteryLevel(100, D));

     }.


//switch between modalities

+!switchToFullPower <-
    -enginePower(low);
    +enginePower(full).

+!switchToLowPower <-
    -enginePower(full);
    +enginePower(low).