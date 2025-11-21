package com.visu.java;

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExecutorServiceCase executorService = new ExecutorServiceCase();
		long result = executorService.getGlobalMaxvalue(100000000);
		System.out.println("max value is "+result);
	}

}
