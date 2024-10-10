

!start.

/*Initial Beliefs*/

pizzeriaLocation(26,26). //posizione della pizzeria
consumptionRate(1).
generatedOrders(0).
orderQueue([]).


//LA PIZZERIA PARTE, ASPETTA CHE PASSANO 5 SECONDI PRIMA DI GENERARE ORDINI

+!start <-
    .print("Pizzeria avviata.");
    !waitForOrders;
    !generateOrders.

+!waitForOrders <-
    .print("Sono in attesa di ricevere degli ordini....");
    .wait(5000). //gli ordini partono dopo 5 secondi dall'apertura della pizzeria


//GENERA ORDINI FINO A NUMERO MAX DI PIZZE, ALTRIMENTI SVUOTA LA CODA RIMANENTE
+!generateOrders <-
    ?maxPizzas(NumberOfMaxPizzas);
    ?generatedOrders(N);
    if (N < NumberOfMaxPizzas) {
        !generateRandomDestination(X, Y);
        .print("NUOVO ORDINE: ", X, " ", Y);
        -generatedOrders(N);
        +generatedOrders(N + 1);
        // Mette l'ordine generato direttamente in coda
        !enqueueOrder(order(X, Y));
        !processOrderQueue;

        // Genera un nuovo ordine dopo un intervallo di tempo casuale
        Min = 1000;
        Max = 3000;
        WaitTime = math.random(Max - Min + 1) + Min;
        .wait(WaitTime);
        !generateOrders; // Genera il prossimo ordine
    } else {
        .print("RAGGIUNTO NUMERO MASSIMO DI ORDINI, SVUOTO LA CODA RIMANENTE");
        !processRemainingOrders;
    }.




+!checkAvailableDrone(X, Y) <-
    !calculateBatteryRequired(X, Y, RequiredBattery);
    .findall(Drone, (at(pizzeria, Drone) &
                    charging(no, Drone) &
                    batteryLevel(Level,Drone)[source(Drone)] &
                    broken(Drone,no) & Level >= RequiredBattery),
             DronesAvailable);
    .print("Droni disponibili in pizzeria PER LA CONSEGNA VERSO ",X," ",Y, " :", DronesAvailable);
    if (DronesAvailable == []) {
        !enqueueOrderAtFront(order(X, Y));
    } else {
            [FirstDrone | _] = DronesAvailable;
            !assignOrderTo(FirstDrone, X, Y);
    }.


//--------------------------GESTIONE DEGLI ORDINI-------------------------------------------


+!processOrderQueue <-
    if (not orderQueue([])) {
        .print("Processo la coda degli ordini");
        !dequeueOrder(order(X, Y));
        !checkAvailableDrone(X, Y);

    } else {
        .print("Nessun ordine in coda, ORDINI FINITI!");
    }.

+!processRemainingOrders <-
    if (not orderQueue([])) {
        .print("Elaborazione ordine rimanente in coda...");
        !dequeueOrder(order(X, Y));
        !checkAvailableDrone(X, Y);
        .wait(1000);
        !processRemainingOrders;
    } else {
        .print("Nessun ordine in coda");
    }.

+!enqueueOrder(order(X, Y)) <-
    -orderQueue(CurrentQueue);
    !appendOrder(order(X, Y), CurrentQueue, NewQueue);
    +orderQueue(NewQueue);
    .print("Ordine aggiunto alla coda: ", NewQueue).

+!enqueueOrderAtFront(order(X, Y)) <-
    -orderQueue(CurrentQueue);
    NewQueue = [order(X, Y) | CurrentQueue];
    +orderQueue(NewQueue);
    .print("Ordine reinserito in testa alla coda: ", NewQueue).

+!appendOrder(Order, [], [Order]).
+!appendOrder(Order, [Head | Tail], [Head | NewTail]) <-
    !appendOrder(Order, Tail, NewTail).

+!dequeueOrder(Order) <-
    -orderQueue([Order | RestQueue]);
    +orderQueue(RestQueue).

+!assignOrderTo(Drone, X,Y) <-
    .print("ORDINE ",X, " ", Y," ASSEGNATO A ", Drone);
    .send(Drone, achieve, receiveOrder(Drone, X,Y)).

//-------------------------------------------------------------------------------------------

+!waitForDrone <-
    .print("Attendo l'arrivo di un drone disponibile").

//----------------------------------------------------------BELIEFS UPDATE------------------------------------

//----------------------------POSITION----------------------------
+at(pizzeria, D)[source(D)] : orderQueue([]) <- // caso in cui la coda è vuota
    .print(D, " è in pizzeria, ma la coda di ordini e vuota non ci sono ordini").



+!left(pizzeria, D) <-
    -at(pizzeria, D)[source(D)]. //rimuovo la credenza che il drone sia alla pizzeria


//----------------------------CHARGING STATE----------------------------
+charging(no, D)[source(D)] <-
    -charging(yes, D)[source(D)]. //rimuove la credenza che sia in carica
    //.print(D, " dice di NON essere in carica").

+charging(yes, D)[source(D)] <-
    -charging(no, D)[source(D)]. //rimuove la credenza che il drone D non sia in carica
    //.print(D, " dice di essere in carica").


//----------------------------BROKEN STATE----------------------------
+broken(D,no)[source(D)] <-
    -broken(D, yes)[source(D)]. //rimuove la credenza che sia in guasto
    //.print(D, " dice di NON essere guasto").

+broken(D,yes)[source(D)] <-
    -broken(D, no)[source(D)]; //rimuove la credenza che il drone D non sia guasto
    .print(D," dice di essere guasto").


//----------------------------CHECK DRONE BATTERY LEVEL----------------------------

+!updateBatteryLevel(BatteryLevel, D) <-
    -batteryLevel(_, D)[source(_)];
    +batteryLevel(BatteryLevel, D)[source(D)].
    //.print(D, " dice di avere la batteria al ", BatteryLevel).



+!calculateBatteryRequired(DestX, DestY, RequiredBattery) <-
    ?pizzeriaLocation(PizzaX, PizzaY);
    {DX = DestX - PizzaX};
    {DY = DestY - PizzaY};
    {Distance = math.sqrt(DX * DX + DY * DY)};
    ?consumptionRate(ConsumptionRate);
    ?consumptionRate(ConsumptionRate);
    {RequiredBattery = Distance * 2 * ConsumptionRate}. //considera la batteria necessaria per A/R


//----------------------------CUSTOM FUNCTION-------------------------------

+!generateRandomDestination(X, Y) <-
    utils.random_destination(50,X,Y).
