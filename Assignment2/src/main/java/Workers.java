import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class for managing worker threads using ReentrantLock and Condition for thread synchronization.
 */
public class Workers {
  private final Queue<Runnable> taskQueue = new LinkedList<>();
  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private final Thread[] threads;
  private volatile boolean stopFlag = false;

  /**
   * Constructor to create a worker thread pool with the given number of threads.
   * Each thread runs the workerThread method.
   *
   * @param numThreads Number of threads to be created.
   */
  public Workers(int numThreads) {
    threads = new Thread[numThreads];

    for (int i = 0; i < numThreads; i++) {
      threads[i] = new Thread(this::workerThread);
      threads[i].start();
    }
  }

  /**
   * Add a task to the work queue and signals waiting threads.
   *
   * @param task Task to be added to the queue.
   */
  public void post(Runnable task) {
    lock.lock();   // lock to ensure that only one thread can modify the queue at a time
    try {
      taskQueue.offer(task);     // Adds the task to the queue, offer method is used since it is a non-blocking method
      condition.signal();    // Signals a waiting thread to wake up, important to wake up a worker thread that might be waiting for a task to be added to the queue
    } finally {
      lock.unlock();
    }
  }

  /**
   * Adds a task to the queue after the given number of milliseconds. Simulates a delayed post.
   * Uses Thread.sleep() to simulate the delay.
   * Uses post() to add the task to the queue.
   *
   * @param task       Task to be added to the queue.
   * @param milliseconds Number of milliseconds to wait before adding the task to the queue.
   */
  public void postTimeout(Runnable task, int milliseconds) {
    try {
      Thread.sleep(milliseconds);
      post(task);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Stops all worker threads. Uses join() on each thread to wait for them to finish their execution.
   */
  public void stop() {
    lock.lock();
    try {
      stopFlag = true;
      condition.signalAll();    // Signals all threads to terminate
    } finally {
      lock.unlock();
    }

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Method that runs in each worker thread and executes tasks from the queue.
   */
  private void workerThread() {
    while (true) {    // run in an infinite loop, the worker thread will run until explicitly terminated
      lock.lock();    // lock to ensure that only one thread can access critical section at a time
      try {
        while (taskQueue.isEmpty() && !stopFlag) {
          // Wait until a task is added to the queue or stopFlag is set
          condition.await();
        }

        if (stopFlag && taskQueue.isEmpty()) {
          // If stopFlag is set and the queue is empty, terminate the thread
          return;
        }

        Runnable task = taskQueue.poll();
        if (task != null) {
          task.run();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        lock.unlock();
      }
    }
  }

  /**
   * Main method that demonstrates the usage of the Workers class. Creates a worker thread pool with 4 threads.
   * Adds tasks to the queue by calling post() and postTimeout() methods. Waits 500 milliseconds.
   * Stops the worker threads by calling stop().
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    Workers workerThreads = new Workers(4);

    workerThreads.post(() -> {
      // Task A
      System.out.println("Task A started on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
      // Perform task logic
      System.out.println("Task A completed on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
    });

    workerThreads.post(() -> {
      // Task B
      System.out.println("Task B started on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
      // Perform task logic
      System.out.println("Task B completed on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
    });

    workerThreads.postTimeout(() -> {
      // Task C (postTimeout example)
      System.out.println("Task C (postTimeout) started on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
      // Perform task logic
      System.out.println("Task C (postTimeout) completed on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
    }, 1000); // Post after 1000 milliseconds

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    workerThreads.post(() -> {
      // Task D
      System.out.println("Task D started on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
      // Perform task logic
      System.out.println("Task D completed on Thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
    });

    workerThreads.stop(); // Calls stop() on the worker threads
  }
}
