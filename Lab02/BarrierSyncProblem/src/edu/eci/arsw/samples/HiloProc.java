package edu.eci.arsw.samples;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class HiloProc extends Thread{

	private CountDownLatch latch;

	int waitPeriod=0;
	int idHilo=0;
	long resultado=0;
	
	public HiloProc(int id, CountDownLatch latch){
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waitPeriod=Math.abs(new Random(System.currentTimeMillis()).nextInt()%5000);
		idHilo=id;
		this.latch = latch;
	}
	
	public void run(){
		int numit=10;
		long startTime=System.currentTimeMillis();
		for (int i=0;i<numit;i++){
			System.out.println("Soy el hilo "+idHilo+" y voy en el "+((float)((float)(i+1)/(float)numit)*100)+"% de mi tarea. P:"+waitPeriod);
			try {
				Thread.sleep(waitPeriod);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		resultado=System.currentTimeMillis()-startTime;
		latch.countDown();
	}
	
	

	public long getResultado() {
		return resultado;
	}
}
