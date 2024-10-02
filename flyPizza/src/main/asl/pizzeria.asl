

!start.

/*Initial Beliefs*/

pizzeriaLocation(26,26). //posizione della pizzeria
consumptionRate(1).
generatedOrders(0).


+!start <-
    .print("Pizzeria avviata.");
    +orderQueue([]);
    !waitForOrders.

+!waitForOrders <-
    .print("Sono in attesa di ricevere degli ordini....");
    .wait(5000); //gli ordini partono dopo 5 secondi dall'apertura della pizzeria
    !generateOrders.


+!generateOrders <-
    ?maxPizzas(NumberOfMaxPizzas); //prendo il numero di pizze massime generabili
    ?generatedOrders(N); //prendo il numero di ordini generati

    if (N < NumberOfMaxPizzas) { //se il numero di pizze gia ordine è minore del numero massimo di pizze allora
        !generateRandomDestination(X, Y); // Creo una nuova destinazione casuale
        .print("NUOVO ORDINE: ", X, " ", Y);
        -generatedOrders(N);+generatedOrders(N+1);
        !checkAvailableDrone(X, Y, mode2);
        Min = 1000;
        Max = 3000;
        WaitTime =  math.random(Max - Min + 1) + Min;
        .wait(WaitTime);
        !generateOrders;
    } else {
        .print("RAGGIUNTO MASSIMO DEGLI ORDINI PRENDIBILI");
    }.


+!checkAvailableDrone(X, Y, Mode) <-
    //1) guardo quanta batteria serve per arrivare alla destinazione
    !calculateBatteryRequired(X, Y, RequiredBattery);
    //2) assegno la consegna a chi si trova alla pizzeria, non sta ricaricando e ha un livello di batteria utile per la consegna
    .findall(Drone, (at(pizzeria, Drone) &
                    charging(no, Drone) &
                     batteryLevel(Level,Drone)[source(Drone)] &
                      broken(Drone,no)[source(Drone)] & Level >= RequiredBattery), DronesAvailable);
    .print("Droni disponibili in pizzeria PER LA CONSEGNA: ", DronesAvailable);

    //3) Verifico se ci sono droni disponibili
    if (DronesAvailable == []) {
        // 4) Aggiungi l'ordine alla coda
        if (Mode == mode2) { //mode2 è quando viene aggiunto dalla generazione in coda
            .print("Metto l'ordine in coda...");
            !enqueueOrder(order(X, Y));
        } else {
            !waitForDrone;
        }

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
        !checkAvailableDrone(X, Y, mode1);
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
    .print("ORDINE ",X, " ", Y," ASSEGNATO A ", Drone);
    .send(Drone, achieve, receiveOrder(Drone, X,Y)).

//-------------------------------------------------------------------------------------------

+!waitForDrone <-
    .print("Attendo l'arrivo di un drone disponibile").

//----------------------------------------------------------BELIEFS UPDATE------------------------------------

//----------------------------POSITION----------------------------
+at(pizzeria, D)[source(D)] : orderQueue([]) <- // caso in cui la coda è vuota
    .print(D, " è in pizzeria, ma la coda di ordini e vuota non ci sono ordini").

+at(pizzeria, D)[source(D)] : orderQueue([_|_]) <- // caso in cui la coda non è vuota
    //prima di processare la coda degli ordini
    //aspetto 2 secondi
    .wait(2000);
    !processOrderQueue(D).


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
