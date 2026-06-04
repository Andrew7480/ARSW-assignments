/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.util.Scanner;

/**
 *
 */
public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];

    private final Object monitor = new Object();
    private boolean paused = false;

    
    private Control() {
        super();
        this.pft = new  PrimeFinderThread[NTHREADS];

        int i;
        for(i = 0;i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA, this);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1, this);
    }
    
    public static Control newControl() {
        return new Control();
    }

    public Object getMonitor(){
        return monitor;
    }

    public boolean isPaused() {
        synchronized(monitor){
            return paused;
        }
    }

    public int getPrimesFound() {
        int ans = 0;
        //ans = pft.stream().mapToInt(t -> t.getPrimesFound()).sum();
        for(PrimeFinderThread t : pft) {
            ans += t.getPrimesFound();
        }
        return ans;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        

        for(int i = 0;i < NTHREADS;i++ ) {
            pft[i].start();
        }
        
        while(true) {
            try {
                Thread.sleep(TMILISECONDS);
            } catch (InterruptedException ex) {
                System.out.println("Thread interrupted");
            }

            synchronized(monitor) {
                paused = true;
            }

            System.out.println("Primos encontrados: "+ getPrimesFound());


            System.out.println("Press ENTER to continue...");
            sc.nextLine();

            synchronized(monitor) {
                paused = false;
                monitor.notifyAll();
            }
        }
        
        
        
    }
    
}
