package clubSimulation;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AndreBarman extends Thread {

    static ClubGrid club;

    GridBlock currentBlock;
    private int movingSpeed;
    private PeopleLocation barmanLoc;
    private int direction = 1;//Add 1 x-value or subtract 1 x-value
    private GridBlock above;
    public static AtomicBoolean served = new AtomicBoolean(false);
    private PeopleCounter counter = new PeopleCounter(direction);

public AndreBarman(int movingSpeed, PeopleLocation andreLoc, PeopleCounter c) {
    this.movingSpeed = movingSpeed;
    this.barmanLoc = andreLoc;
    this.counter = c;
    this.currentBlock = andreLoc.getLocation();
}

//setter for Andre's direction used in clubGrid to prevent index out of range
public void setDirection(int d) {
    this.direction *= d;
}

	//check to see if user pressed pause button
private void checkPause() {
    // THIS DOES NOTHING - MUST BE FIXED
    synchronized (ClubSimulation.paused) {
        try {
            while (ClubSimulation.paused.get()) {
                ClubSimulation.paused.wait();
            }
        } catch (InterruptedException e) {}
    }


    }

private void startSim() { 
    try { ClubSimulation.latch.await(); //wait for latch to open
        synchronized (ClubSimulation.started) {
        if (ClubSimulation.started.get()) {
            ClubSimulation.started.set(true);
        
        }
        }
        } catch (InterruptedException e) {} 
    
}

private void patrol() throws InterruptedException {	
        int x_mv= direction;
        int y_mv= 0;
        serve();
        //currentBlock=Clubgoer.club.moveAndre(currentBlock,x_mv,y_mv, barmanLoc);	
        currentBlock = club.moveAndre(currentBlock,x_mv,y_mv, barmanLoc);
        sleep(movingSpeed);
        
        if (club.getMaxX() == currentBlock.getX()) {
            direction *= -1;
        }
        else if (currentBlock.getX() == 1)
            direction *= -1;
        
        
    }


private void serve() {
    try {
    //Andre's co-ord system is ahead by 1 for x-values
    //whichBlock checks the x-value of him -1
    GridBlock above = club.whichBlock(currentBlock.getX() - 1, currentBlock.getY() - 1);
    System.out.println("Andre checked: " + (above.getX()) + ":" + above.getY());
    if (above.occupied()){
        sleep(movingSpeed*6);
        //locked placed to notify the gridblock on bar
        //and allow clubgoer to leave
        synchronized (above) {

        above.notify();
        System.out.println("Andre served guy at: " + above.getX() + ":" + above.getY());
        }

    }
    } catch (InterruptedException e3) {};

}

public void run() {
    try {
    startSim();
    checkPause();
    while (true) {
        checkPause();
        patrol();    
      
    }
    } catch (InterruptedException e) {};

}
    
}
