all : client

client : client.o
	gcc -pthread -o client client.o

client.o : client.c
	gcc -c -Wall client.c

clean :
	rm *.o $(all) *~ client
