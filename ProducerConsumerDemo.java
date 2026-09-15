package ostask;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

//implementing producer-consumer synchronization

class BoundedBuffer {

		 private final Queue<Integer> items;
		 private final int limit;
		 //initializes the buffer with a specified maximum capacity limit
		 public BoundedBuffer(int limit) {
		 this.limit = limit;
		 this.items = new ArrayDeque<>();
		 }

public synchronized void addItem(int value, String producerName)
		 		            throws InterruptedException {

		 while (items.size() >= limit) {
		 		            System.out.println(producerName +
		 		                    " is waiting - buffer is full.");
		 		            wait();
		 		        }

		 		        items.add(value);

System.out.println( producerName + " added " + value + " | Current items: " + items.size()
		 );

		 		                notifyAll();// signal all waiting threads that state has changed
		 		    }

public synchronized int removeItem(String consumerName)
		 		    throws InterruptedException {

		 while (items.isEmpty()) {
System.out.println(consumerName + " is waiting - buffer is empty.");
		 		            wait();
		 		        }

		 		        int value = items.remove();
System.out.println( consumerName + " removed " + value +
		 		                " | Current items: " + items.size());

		 		        notifyAll();

		 return value;
		 		    }
		 		}
		

		  
 class ProducerTask implements Runnable {
		  	// shared buffer used by the producer to store items
		  	    private final BoundedBuffer buffer;
		  	    private final int id;
		  	    private final int numberOfItems;// total number of items this producer should create

		  // constructor initializes the buffer, producer ID, and item count
public ProducerTask(BoundedBuffer buffer, int id, int numberOfItems) {
		  	        this.buffer = buffer;
		  	        this.id = id;
		  	        this.numberOfItems = numberOfItems;
		  	    }

		  	    @Override
public void run() {

		  for (int i = 1; i <= numberOfItems; i++) {
		   int product = (id * 100) + i;

		  	            try {

		  	                buffer.addItem(product, "Producer-" + id);

		  	                Thread.sleep(250);

		  	            } catch (InterruptedException e) {
		  // restore the interrupted status of the thread
		  	                Thread.currentThread().interrupt();
		  // stop the producer if the thread is interrupted
		  	                return;
		   }
		   }
		  }}
 

 class ConsumerTask implements Runnable{

 	    private final BoundedBuffer buffer;// shared buffer from which the consumer takes 
 	    private final int id;
 	    private final int itemsToConsume;
 	    
 // Constructor initializes the consumer details
 public ConsumerTask(BoundedBuffer buffer, int id, int itemsToConsume) {
 	        this.buffer = buffer;
 	        this.id = id;
 	        this.itemsToConsume = itemsToConsume;
 	    }

 	    @Override
 public void run() {

 for (int i = 0; i < itemsToConsume; i++) {

 	            try {

 	                buffer.removeItem("Consumer-" + id);

 	                Thread.sleep(450);

 	     } catch (InterruptedException e) {

 	                Thread.currentThread().interrupt();
 	             
 	                // Stop the consumer if the thread is interrupted
 	                
 	                return;
  }
  }
  }}
 
 

 public class ProducerConsumerDemo {
 public static void main(String[] args) {
          
	 Scanner input = new Scanner(System.in);
 
	 System.out.print("enter buffer capacity: ");
 		   int capacity = input.nextInt();
 System.out.print("enter number of producers: ");
            int producerCount = input.nextInt();
 System.out.print("enter number of consumers: ");
 		   int consumerCount = input.nextInt();

 System.out.print("enter items produced by each producer: ");
 		    int itemsPerProducer = input.nextInt();

 		    int totalItems = producerCount * itemsPerProducer;

 		     if (totalItems % consumerCount != 0) {

 System.out.println("total items must be divisible by number of consumers.");

 		            input.close();
 		            return;
 		        }

 		        int itemsPerConsumer = totalItems / consumerCount;
 BoundedBuffer sharedBuffer = new BoundedBuffer(capacity);

 		        Thread[] producers =
 		                new Thread[producerCount];

 		        Thread[] consumers =
 		                new Thread[consumerCount];

 		        // create producer threads
 for (int i = 0; i < producerCount; i++) {

 		            producers[i] = new Thread(
 		                    new ProducerTask(
 		                            sharedBuffer,
 		                            i + 1,
 		                            itemsPerProducer
 		                    ),
 		                    "ProducerThread-" + (i + 1)
 		            );
 		        }

 		        // create consumer threads
 		        for (int i = 0; i < consumerCount; i++) {

 		            consumers[i] = new Thread(
 		                    new ConsumerTask(
 		                            sharedBuffer,
 		                            i + 1,
 		                            itemsPerConsumer
 		                    ),
 		                    "ConsumerThread-" + (i + 1)
 		            );
 		        }

 		        // start producers
 		        for (Thread producer : producers) {
 		            producer.start();
 		        }

 		        // start consumers
 		        for (Thread consumer : consumers) {
 		            consumer.start();
 		        }

 		        try {

 		                    // wait for producers
 		            for (Thread producer : producers) {
 		                producer.join();
 		            }

 		                // wait for consumers
 		            for (Thread consumer : consumers) {
 		                consumer.join();
 		            }

 		        } catch (InterruptedException e) {

 		            Thread.currentThread().interrupt();
 		        }

 		        input.close();
 System.out.println("\nProducer-Consumer execution completed.");
 		    }}
 	