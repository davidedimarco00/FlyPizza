!start.

/*Initial Beliefs*/

+!start <-
    .print("Pizzeria avviata.");
    !waitForOrders.

+!waitForOrders <-
    .print("Sono in attesa di ricevere degli ordini....");
    .wait(5000); //gli ordini partono dopo 5 secondi dall'apertura della pizzeria
    !generateOrders.

+!generateOrders <-
    .print("Genero una destinazione...");
    !generateRandomDestination(X, Y); //creo una nuova destinazione casuale
    .print("Nuova destinazione: ", X, " ",Y);
    !checkAvailableDrone(X,Y); //guardo quale drone è disponibile
    .wait(3000);
    !generateOrders.


+!checkAvailableDrone(X, Y) : at(pizzeria)[source(Drone)] <- // Un drone è presente in pizzeria
    .print(Drone, " in pizzeria, gli assegno l'ordine");
    !assignOrderTo(Drone, X, Y). // Assegno l'ordine al drone disponibile

+!checkAvailableDrone(X,Y) : not (at(pizzeria)[source(drone1)] |
                                  at(pizzeria)[source(drone2)] |
                                  at(pizzeria)[source(drone3)]) <-
    .print("Nessun drone in pizzeria"); // Scrivo che non c'è nessuno
    !waitForDrone.  // Aspetto che arrivi qualcuno


+!waitForDrone : at(pizzeria)[source(Drone)] <- // Quando un drone torna alla pizzeria
    .print(Drone, " è tornato alla pizzeria. Riprendo a generare ordini.");
    !generateOrders.

+!waitForDrone <-
    .print("Attendo l'arrivo di qualcuno").

+!assignOrderTo(Drone, X,Y) <-
    .send(Drone, achieve, receiveOrder(Drone, X,Y));
    .print("Ordine assegnato a ", Drone).




//BELIEFS UPDATE

+at(pizzeria)[source(D)] <-
    .print(D, " dice di essere alla pizzeria").


+!left(D, pizzeria) <-
    -at(pizzeria)[source(D)]. //rimuovo la credenza che il drone sia alla pizzeria.



//GENERATE DESTINATION AND CUSTOM FUNCTION

+!generateRandomDestination(X, Y) <-
    utils.random_destination(50,X,Y).
