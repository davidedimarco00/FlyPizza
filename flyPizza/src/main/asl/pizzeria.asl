!start.

/*Initial Beliefs*/


pizzeriaLocation(26,26). //posizione della pizzeria
consumptionRate(1).

+!start <-
    .print("Pizzeria avviata.");
    +orderQueue([]);
    !waitForOrders.

+!waitForOrders <-
    .print("Sono in attesa di ricevere degli ordini....");
    .wait(7000); //gli ordini partono dopo 5 secondi dall'apertura della pizzeria
    !generateOrders.

+!generateOrders <-
    .print("Genero una destinazione...");
    !generateRandomDestination(X, Y); //creo una nuova destinazione casuale
    .print("Nuova destinazione: ", X, " ",Y);
    !checkAvailableDrone(X,Y); //guardo quale drone è disponibile
    .wait(3000);
    !generateOrders.


+!checkAvailableDrone(X, Y) <-
    //1) guardo quanta batteria serve per arrivare alla destinazione
    !calculateBatteryRequired(X, Y, RequiredBattery);
    .print("Batteria necessaria per raggiungere la destinazione: ", RequiredBattery);

    //2) assegno la consegna a chi si trova alla pizzeria, non sta ricaricando e ha un livello di batteria utile per la consegna
    .findall(Drone, (at(pizzeria, Drone) & charging(no, Drone) & batteryLevel(Level,Drone) & Level >= RequiredBattery), DronesAvailable);
    .print("Droni disponibili in pizzeria: ", DronesAvailable);

    //3) Verifico se ci sono droni disponibili
    if (DronesAvailable == []) {
        .print("Nessun drone disponibile in pizzeria per la consegna alla posizione ", X, " ", Y);
        .print("Metto l'ordine in coda...");

    // 4) Aggiungi l'ordine alla coda
        !enqueueOrder(order(X, Y));

        !waitForDrone;
    } else {
        // 5) Assegna l'ordine al primo drone disponibile
            [FirstDrone | _] = DronesAvailable;
            !assignOrderTo(FirstDrone, X, Y);
    }.


//--------------------------GESTIONE DEGLI ORDINI-------------------------------------------

+!processOrderQueue(Drone) <-
    .print("Processo la coda degli ordini per ", Drone);
    if (not .empty(orderQueue)) {
        !dequeueOrder(order(X, Y));
        .print("Assegno ordine dalla coda a ", Drone, ": ", X, " ", Y);
        !assignOrderTo(Drone, X, Y);
    } else {
        .print("Nessun ordine in coda da assegnare a ", Drone);
    }.

+!enqueueOrder(Order) <-
    -orderQueue(CurrentQueue);
    !appendOrder(Order, CurrentQueue, NewQueue);
    +orderQueue(NewQueue);
    .print("Ordine aggiunto alla coda: ", NewQueue).

+!appendOrder(Order, [], [Order]).
+!appendOrder(Order, [Head | Tail], [Head | NewTail]) <-
    !appendOrder(Order, Tail, NewTail).

+!dequeueOrder(Order) <-
    -orderQueue([Order | RestQueue]);
    +orderQueue(RestQueue).

+!assignOrderTo(Drone, X,Y) <-
    .print("Ordine assegnato a ", Drone);
    .send(Drone, achieve, receiveOrder(Drone, X,Y)).

//-------------------------------------------------------------------------------------------

+!waitForDrone <-
    .print("Attendo l'arrivo di qualcuno").

//----------------------------------------------------------BELIEFS UPDATE------------------------------------

//----------------------------POSITION----------------------------
+at(pizzeria, D)[source(D)] : orderQueue([]) <- // caso in cui la coda è vuota
    .print("La coda di ordini e vuota non ci sono ordini");
    .print(D, " dice di essere alla pizzeria").

+at(pizzeria, D)[source(D)] : orderQueue([_|_]) <- // caso in cui la coda non è vuota
    !processOrderQueue(D).


+!left(pizzeria, D) <-
    -at(pizzeria, D)[source(D)]; //rimuovo la credenza che il drone sia alla pizzeria
    .print(D, " dice di aver lasciato la pizzeria").


//----------------------------CHARGING STATE----------------------------
+charging(no, D)[source(D)] <-
    -charging(yes, D)[source(D)]; //rimuove la credenza che sia in carica
    .print(D, " dice di NON essere in carica").

+charging(yes, D)[source(D)] <-
    -charging(no, D)[source(D)]; //rimuove la credenza che il drone D non sia in carica
    .print(D, " dice di essere in carica").


//----------------------------BROKEN STATE----------------------------
+broken(D, no)[source(D)] <-
    -broken(no, D)[source(D)]; //rimuove la credenza che sia in guasto
    .print(D, " dice di NON essere guasto").

+broken(yes, D)[source(D)] <-
    -broken(no, D)[source(D)]; //rimuove la credenza che il drone D non sia guasto
    .print(D, " dice di essere guasto").


//----------------------------CHECK DRONE BATTERY LEVEL----------------------------

+batteryLevel(BatteryLevel, D) <-
    .print(D, " dice di avere la batteria al ",BatteryLevel).

+!calculateBatteryRequired(DestX, DestY, RequiredBattery) <-
    ?pizzeriaLocation(PizzaX, PizzaY);
    {DX = DestX - PizzaX};
    {DY = DestY - PizzaY};
    {Distance = math.sqrt(DX * DX + DY * DY)};
    ?consumptionRate(ConsumptionRate);
    {RequiredBattery = Distance * 2 * ConsumptionRate}.


//----------------------------CUSTOM FUNCTION-------------------------------

+!generateRandomDestination(X, Y) <-
    utils.random_destination(50,X,Y).
