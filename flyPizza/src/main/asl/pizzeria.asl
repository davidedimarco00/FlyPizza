//pizzeria.asl
//Initial beliefs

open(pizzeria).
droneAvailable(5).



+numberOfAvailablePizzas(X) <- !check_pizza(X).

!start.


+!check_pizza(X): true <-
    .print("Pizze disponibili: ",X).



+!start : true <-
    .print("Pizzeria started");
    !check_pizza(2000);
    !startDelivering.


+!deliverPizza : true <-
    .print("Consegno pizza!");
    !pizza_delivered.

+!pizza_delivered : numberOfAvailablePizzas(N) <-
    .print("PIZZA CONSEGNATA! Pizze disponibili: ", N).


+!startDelivering : open(pizzeria) & numberOfAvailablePizzas(N) & N > 0 <-
    .print("Start con partenza ordini.");
    !deliverPizza.