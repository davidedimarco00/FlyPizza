//Initial beliefs

current_position(27,26).
busy(no).

//initial goals

!start.

//PLANS

+!start <-
    .print("In attesa di partire per recuperare i droni...").


+!moveToDrone(D, X, Y) <-
    .print("Parto verso il drone ",D);
    +busy(yes);
    ?busy(BusyStatus);
    if (BusyStatus == no) {
        move(X,Y);
        -busy(no); +busy(yes);
    } else {
        .print("Sono occupato, attendo...");
        move(X,Y);
        !moveToDrone(D, X, Y);
    }.

+!fail <-
    .print("fail").

//BELIEFS UPDATE

+brokenDrone(D, X, Y) <-
    .print("Ho ricevuto il messaggio da parte di ",D, " alla posizione",X," ",Y," che dice di essere rotto");
    !moveToDrone(D, X, Y).

+brokenDrone(D, X, Y) : busy(yes) <-
    .print("Sono occupato").


