#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include "buffer.h"
#include <unistd.h>

//buffer
buffer_item buffer[BUFFER_SIZE];
int num_buffer_items;

//mutex lock
pthread_mutex_t mutex;

//semaphores
sem_t full, empty;

//tid for thread creation
pthread_t tid;

//maximum sleep time for each thread
static int MAX_WAIT_TIME = 30;


int insert_item(buffer_item item)
{
    if(num_buffer_items < BUFFER_SIZE)
    {
    	/* Add item to buffer and increment the counter */
    	buffer[num_buffer_items] = item;
    	num_buffer_items = num_buffer_items + 1;
    	/* return 0 if successful, otherwise */
    	return 0;
    }
    else
    {
    	/* return -1 indicating an error */
    	return -1;
    }
}


int remove_item(buffer_item *item)
{
    if(num_buffer_items > 0)
    {
    	/* Remove item from buffer and decrement the counter */
    	*item = buffer[num_buffer_items - 1];
    	num_buffer_items = num_buffer_items - 1;
    	/* return 0 if succesful, otherwise */
    	return 0;
    }
  		 else
    {
    	/* return -1 indicating an error */
    	return -1;
    }
}


void *producer(void *param)
{
	buffer_item item;
    
	while(1)
	{
    /* sleep for a random period of time */
    int sleep_time = (rand() % MAX_WAIT_TIME) + 1;
    sleep(sleep_time);

    /* generate a random number */
    item = rand();

    /* wait for the buffer to have available space */
    sem_wait(&empty);
    /* aquire mutual exclusion lock when available */
    pthread_mutex_lock(&mutex);
    
    if(insert_item(item))
    {
    	printf("Error, buffer full. Producer could not produce\n");
    }
    else
    {
    	printf("Producer produced %d\n", item);
    }

    /* release mutual exclusion lock, allowing other threads to use */
    pthread_mutex_unlock(&mutex);
    /* increment full semaphore */
    sem_post(&full);
	}
}


void *consumer(void *param)
{
	buffer_item item;
    
	while(1)
	{
    /* sleep for a random period of time */
    int sleep_time = (rand() % MAX_WAIT_TIME) + 1;
    sleep(sleep_time);

    /* wait for the buffer to have items to consume */
    sem_wait(&full);
    /* aquire mutual exclusion lock when available */
    pthread_mutex_lock(&mutex);
    
    if(remove_item(&item))
    {
    	printf("Error, buffer empty. Consumer could not consume\n");
    }
    else
    {
    	printf("Consumer consumed %d\n", item);
    }

    /* release mutual exclusion lock, allowing other threads to use */
    pthread_mutex_unlock(&mutex);
    /* increment empty semaphore */
    sem_post(&empty);
	}
}
    

int main(int argc, char *argv[])
{
	if(argc != 4)
	{
    printf("Incorrect number of arguments (3 arguments, all INTs)\n");
    return -1;
	}

/* 1. Get command line arguments argv[1], argv[2], and argv[3] */
	int sleep_time = atoi(argv[1]);
	int producers = atoi(argv[2]);
	int consumers = atoi(argv[3]);

/* 2. Initialize buffer, mutex, and semaphores */
	num_buffer_items = 0;
	pthread_mutex_init(&mutex, NULL);
	sem_init(&full, 0, 0);
	sem_init(&empty, 0, BUFFER_SIZE);

/* 3. Create producer thread(s) */
	for(int i = 0; i < producers; i++)
	{
    pthread_create(&tid, NULL, producer, NULL);
	}

/* 4. Create consumer thread(s) */
	for(int i = 0; i < consumers; i++)
	{
    pthread_create(&tid, NULL, consumer, NULL);
	}

/* 5. Sleep */
	sleep(sleep_time);

/* 6. Exit */
	return 0;
}
