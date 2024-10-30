

!start.

/*Initial Beliefs*/

pizzeriaLocation(26,26).
consumptionRate(1).
generatedOrders(0).
orderQueue([]).


//PIZZERIA STARTS AND WAIT 5 SECONDS

+!start <-
    .print("Pizzeria started");
    !waitForOrders;
    !generateOrders.

+!waitForOrders <-
    .print("Waiting orders...");
    .wait(5000).



+!generateOrders <-
    ?maxPizzas(NumberOfMaxPizzas);
    ?generatedOrders(N);
    if (N < NumberOfMaxPizzas) {
        !generateRandomDestination(X, Y);
        .print("New order: ", X, " ", Y);
        -generatedOrders(N);
        +generatedOrders(N + 1);
        !enqueueOrder(order(X, Y));
        !processOrderQueue;

        //generate new order at random time
        Min = 1000;
        Max = 3000;
        WaitTime = math.random(Max - Min + 1) + Min;
        .wait(WaitTime);
        !generateOrders; //generate next order
    } else {
        .print("MAXIMUM NUMBER OF ORDERS REACHED, CLEARING REMAINING QUEUE");
        !processRemainingOrders;
    }.


//CHECK AVAILABLE DRONE AND ASSIGN DELIVERY

+!checkAvailableDrone(X, Y) <-
    !calculateBatteryRequired(X, Y, RequiredBattery);
    .findall(Drone, (at(pizzeria, Drone) &
                    charging(no, Drone) &
                    batteryLevel(Level,Drone)[source(Drone)] &
                    broken(Drone,no) & Level >= RequiredBattery),
             DronesAvailable);
    .print("Drones available for the delivery at ",X," ",Y, " :", DronesAvailable);
    if (DronesAvailable == []) {
        !enqueueOrderAtFront(order(X, Y)); //enqueue order at front
    } else {
            [FirstDrone | _] = DronesAvailable;
            !assignOrderTo(FirstDrone, X, Y);
    }.


//--------------------------GESTIONE DEGLI ORDINI-------------------------------------------


+!processOrderQueue <-
    if (not orderQueue([])) {
        .print("Process Order Queue");
        !dequeueOrder(order(X, Y));
        !checkAvailableDrone(X, Y);

    } else {
        .print("No order in queue, FINISH!");
    }.

+!processRemainingOrders <-
    if (not orderQueue([])) {
        .print("process remaining orders...");
        !dequeueOrder(order(X, Y));
        !checkAvailableDrone(X, Y);
        .wait(1000);
        !processRemainingOrders;
    } else {
        .print("Queue is empty");
    }.

+!enqueueOrder(order(X, Y)) <-
    -orderQueue(CurrentQueue);
    !appendOrder(order(X, Y), CurrentQueue, NewQueue);
    +orderQueue(NewQueue);
    .print("Order added to queue: ", NewQueue).

+!enqueueOrderAtFront(order(X, Y)) <-
    -orderQueue(CurrentQueue);
    NewQueue = [order(X, Y) | CurrentQueue];
    +orderQueue(NewQueue);
    .print("Order reinsertion at the front: ", NewQueue).

+!appendOrder(Order, [], [Order]).

+!appendOrder(Order, [Head | Tail], [Head | NewTail]) <-
    !appendOrder(Order, Tail, NewTail).

+!dequeueOrder(Order) <-
    -orderQueue([Order | RestQueue]);
    +orderQueue(RestQueue).

+!assignOrderTo(Drone, X,Y) <-
    .print("ORDER ",X, " ", Y," ASSIGNED TO ", Drone);
    .send(Drone, achieve, receiveOrder(Drone, X,Y)).


+!waitForDrone <-
    .print("Waiting for available drone...").

//----------------------------------------------------------BELIEFS UPDATE------------------------------------

//----------------------------POSITION----------------------------
+at(pizzeria, D)[source(D)] : orderQueue([]) <-
    .print(D, " is at pizzeria but queue order is empty").


+!left(pizzeria, D) <-
    -at(pizzeria, D)[source(D)].


//----------------------------CHARGING STATE----------------------------
+charging(no, D)[source(D)] <-
    -charging(yes, D)[source(D)].

+charging(yes, D)[source(D)] <-
    -charging(no, D)[source(D)].
    //.print(D, " dice di essere in carica").


//----------------------------BROKEN STATE----------------------------
+broken(D,no)[source(D)] <-
    -broken(D, yes)[source(D)].

+broken(D,yes)[source(D)] <-
    -broken(D, no)[source(D)].




//----------------------------CHECK DRONE BATTERY LEVEL----------------------------

+!updateBatteryLevel(BatteryLevel, D) <-
    -batteryLevel(_, D)[source(_)];
    +batteryLevel(BatteryLevel, D)[source(D)].


+!updateBrokenStatus(D, Value) <-
    -broken(_, D)[source(_)];
    +broken(D, Value)[source(D)].



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
