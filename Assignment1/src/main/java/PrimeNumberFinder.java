import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This program finds all the prime numbers between a given range using multiple threads.
 */
public class PrimeNumberFinder {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    // Get the range and number of threads from the user
    System.out.print("Enter the starting number: ");
    int startRange = scanner.nextInt();

    System.out.print("Enter the ending number: ");
    int endRange = scanner.nextInt();

    System.out.print("Enter the number of threads: ");
    int numThreads = scanner.nextInt();

    // Create an array to hold the thread objects
    PrimeNumberThread[] threads = new PrimeNumberThread[numThreads];

    // Calculating the range for each thread
    int rangePerThread = (endRange - startRange + 1) / numThreads;


    // Create and start threads
    for (int i = 0; i < numThreads; i++) {
      int threadStart = startRange + i * rangePerThread;
      int threadEnd = (i == numThreads - 1) ? endRange : threadStart + rangePerThread - 1;

      threads[i] = new PrimeNumberThread(threadStart, threadEnd);
      threads[i].start();
    }

    // Wait for all threads to finish by using join()
    for (PrimeNumberThread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    List<Integer> primeNumbers = new ArrayList<>();
    for (PrimeNumberThread thread : threads) {
      primeNumbers.addAll(thread.getPrimeNumbers());
    }

    // Sort and print the prime numbers
    Collections.sort(primeNumbers);
    System.out.println("Sorted Prime numbers between " + startRange + " and " + endRange + ": " + primeNumbers);
  }
}

/**
 * This class represents a thread that finds all the prime numbers between a given range.
 */
class PrimeNumberThread extends Thread {
  private final int startRange;
  private final int endRange;
  private static final Lock lock = new ReentrantLock();
  private List<Integer> primeNumbers;

  public PrimeNumberThread(int startRange, int endRange) {
    this.startRange = startRange;
    this.endRange = endRange;
    this.primeNumbers = new ArrayList<>();
  }


  // method that finds all the prime numbers between a given range
  public void run() {

    for (int i = startRange; i <= endRange; i++) {
      if (isPrime(i)) {
        lock.lock();
        try {
          primeNumbers.add(i);
        } finally {
          lock.unlock();
        }
      }
    }
  }

  // method that checks if a number is prime, returns true if it is, false if it is not
  private boolean isPrime(int number) {
    if (number <= 1) {
      return false;
    }
    for (int i = 2; i <= Math.sqrt(number); i++) {
      if (number % i == 0) {
        return false;
      }
    }
    return true;
  }

  public List<Integer> getPrimeNumbers() {
    return primeNumbers;
  }
}
