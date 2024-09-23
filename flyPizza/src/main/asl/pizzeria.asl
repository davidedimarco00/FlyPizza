!start.

/* Initial Beliefs */
pizzeriaLocation(26, 26).
consumptionRate(1).

+!start <-
    .print("Pizzeria avviata.");
    !waitForOrders.

+!waitForOrders <-
    .print("Sono in attesa di ricevere degli ordini....");
    .wait(5000); // Gli ordini partono dopo 5 secondi dall'apertura della pizzeria
    !generateOrders.

+!generateOrders <-
    .print("Genero una destinazione...");
    !generateRandomDestination(X, Y); // Crea una nuova destinazione casuale
    .print("Nuova destinazione: ", X, " ", Y);
    !checkAvailableDrone(X, Y); // Guarda quale drone è disponibile
    .wait(3000);
    !generateOrders.

+!checkAvailableDrone(X, Y) : at(pizzeria)[source(Drone)]  <- // Un drone è presente in pizzeria e il suo livello di batteria è buono per fare la consegna
    .print(Drone, " in pizzeria e carico");
    !assignOrderTo(Drone, X, Y). // Assegna l'ordine al drone disponibile

+!checkAvailableDrone(X, Y) : not (at(pizzeria)[source(drone1)] |
                                   at(pizzeria)[source(drone2)] |
                                   at(pizzeria)[source(drone3)]) <-
    .print("Nessun drone in pizzeria"); // Scrive che non c'è nessuno
    !waitForDrone.  // Aspetta che arrivi qualcuno

+!waitForDrone : at(pizzeria)[source(Drone)] <- // Quando un drone torna alla pizzeria
    .print(Drone, " è tornato alla pizzeria. Riprendo a generare ordini.");
    !generateOrders.

+!waitForDrone <-
    .print("Attendo l'arrivo di qualcuno").

+!assignOrderTo(Drone, X, Y) <-
    ?batteryLevel(Level)[source(Drone)];
    .print("Batteria di ", Drone, " è ", Level);
    !calculateBatteryRequired(X, Y, RequiredBattery);
    .print("Batteria necessaria per raggiungere la destinazione: ", RequiredBattery);
    if (Level >= RequiredBattery) {
        .send(Drone, achieve, receiveOrder(Drone, X, Y));
        .print("Ordine assegnato a ", Drone);
    }
    else {
        .print("Non posso assegnare l'ordine a ", Drone, " perché la batteria è ", Level, " e servirebbe almeno ", RequiredBattery);
        !checkAvailableDrone(X, Y); // Guarda quale drone è disponibile;
    }.

/* BELIEFS UPDATE */

+at(pizzeria)[source(D)] <-
    .print(D, " dice di essere alla pizzeria").

+left(D, pizzeria)[source(D)] <-
    -at(pizzeria)[source(D)]; // Rimuove la credenza che il drone sia alla pizzeria.
    -batteryLevel(_)[source(D)]; //rimuove la credenza dell'ultimo stato della batteria
    .print(D," ha lasciato la pizzeria").



+batteryLevel(BatteryValue)[source(D)] <-
    .print(D, " ha la batteria al ", BatteryValue).



/* GENERATE DESTINATION */

+!generateRandomDestination(X, Y) <-
    utils.random_destination(50, X, Y).

+!calculateBatteryRequired(DestX, DestY, RequiredBattery) <-
    ?pizzeriaLocation(PizzaX, PizzaY);
    {DX = DestX - PizzaX};
    {DY = DestY - PizzaY};
    {Distance = math.sqrt(DX * DX + DY * DY)};
    ?consumptionRate(ConsumptionRate);
    {RequiredBattery = Distance * 2 * ConsumptionRate}.  //la distanza per 2 perche deve avere anche energia per arrivare alla pizzeria
