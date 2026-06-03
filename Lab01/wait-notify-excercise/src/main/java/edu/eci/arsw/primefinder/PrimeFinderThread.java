package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread{

	
	int a,b;
	private Control control;
    
	private List<Integer> primes;
	
	public PrimeFinderThread(int a, int b, Control control) {
		super();
        this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
		this.control = control;
	}

        @Override
	public void run(){
            for (int i= a;i < b;i++){
				
				synchronized(control.getMonitor()) {
					while(control.isPaused()) {
						try {
							control.getMonitor().wait();
						} catch (InterruptedException ex) {
							System.out.println("Thread interrumpida");
						}
					}
				}

                if (isPrime(i)){
                    primes.add(i);
                    System.out.println(i);
                }
            }
	}
	
	boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) { 
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}

	public int getPrimesFound() {
		return primes.size();
	}
	
}
