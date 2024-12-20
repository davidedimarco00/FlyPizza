!start.


pizzeriaLocation(26,26).
busy(no).


+!start <-
    .my_name(RobotName);
    .print(RobotName, " is ready at the pizzeria.").

/* HANDLE BROKEN DRONE NOTIFICATION */
+brokenDrone(D, X, Y) : busy(no)  <-
    .send(pizzeria, achieve, updateBrokenStatus(D,yes));
    -busy(_);
    +busy(yes);
    !handleBrokenDrone(D, X, Y).

+brokenDrone(D, X, Y) : busy(yes)  <-
    .print("I am busy in another recovery").

+!processNextBrokenDrone : busy(no) & brokenDrone(D, X, Y) <-
    .print("HANDLE NEXT DRONE ", D);
    -busy(_);
    +busy(yes);
    !handleBrokenDrone(D, X, Y).


+!processNextBrokenDrone : not brokenDrone(D, X, Y) <-
    .print("No more drone to recover!").



+!handleBrokenDrone(D, X, Y) <-
    .print("Received notification that ", D, " is broken at position ", X, ", ", Y);
    !moveToLocation(X, Y);
    !pickupDrone(D);
    ?pizzeriaLocation(PX, PY);
    !moveToLocation(PX, PY);
    !dropOffDrone(D).


/* MOVEMENT PLAN */
+!moveToLocation(X, Y) <-
    ?current_position(CurrentX, CurrentY);
    if (CurrentX = X & CurrentY = Y) {
        .print("Arrived at destination ", X, ", ", Y);
    } else {
        .wait(200);
        move(X, Y,null);
        !moveToLocation(X, Y);
    }.

-!moveToLocation(X,Y) <-
    .print("Waiting position update...").

/* PICK UP DRONE */
+!pickupDrone(D) <-
    .print("Picking up drone ", D).

/* DROP OFF DRONE */
+!dropOffDrone(D) <-
    .print("Dropping off drone ", D, " at the pizzeria.");
    repair_drone(D);
    -busy(_);
    +busy(no);
    .send(pizzeria, achieve, updateBrokenStatus(D, no));
    .print(D, " has been repaired and is at the pizzeria. Continue with others if present.");
    -brokenDrone(_,_,_)[source(D)];
    !processNextBrokenDrone.