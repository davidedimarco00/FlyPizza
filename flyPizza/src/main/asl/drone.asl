// drone.asl

!start.

+!start : true <-
    .print("Drone started").

+!move : true <-
    .print("Drone moving");
    move;
    .wait(1000);  // wait for 1 second
    pick;
    !move.