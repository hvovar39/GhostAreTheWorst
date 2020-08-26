#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <pthread.h>

typedef struct client{
  char * id;
  struct sockaddr_in infoTCP;
  int socketTCP;
  struct sockaddr_in infoUDP;
  int socketUDP;
  int portUDP;
  struct ip_mreq IPMulti;
  struct sockaddr_in infoMulti;
  int socketMulti;
}client;

client* cl;
char *ip;

void fermerClient();

#include "Utile.h"
#include "GestionUDP.h"
#include "GestionTCP.h"

int main(int argc, char *argv[]) {
  ip = malloc(sizeof(char)*15);
  
  ip = "127.0.0.1";      //ENTREZ L'ADRESSE IP ICI

  cl = malloc(sizeof(client));
  cl->id = malloc(sizeof(char)*20);
  cl->socketTCP = socket(PF_INET, SOCK_STREAM, 0);
  cl->socketUDP = socket(PF_INET, SOCK_DGRAM, 0);
  cl->socketMulti = socket(PF_INET, SOCK_DGRAM, 0);
  cl->portUDP = stoi(argv[1]);
  char * reconnection = malloc(sizeof(char)*2);
  pthread_t thTCP, thUDP, th_Multi_Diff;
  int *multi = malloc(sizeof(int));
  int *UDP = malloc(sizeof(int));
  *UDP = 1;
  
  printf("Veuillez entrez un identifiant : \n");
  fgets(cl->id, 20, stdin);
  cl->id[strlen(cl->id)-1]='\0';
  while(!setTCP() || !setUDP()){
    printf("Erreur de connection, veuillez réessayer dans quelques instants!\nPour réessayer entrer y, pour quitter l'application entrez n:\n");
    fgets(reconnection, 2, stdin);
    if(*reconnection == 'n')
      return -1;
  }
  if(interfaceLobby()){
    
    pthread_create (&thTCP, NULL, boucleTCP, NULL);
    pthread_create (&thUDP, NULL, boucleUDP, UDP);
    pthread_create (&th_Multi_Diff, NULL, boucleUDP, multi);
    pthread_join(thTCP, NULL);
  }
  fermerClient();
  return 0;
}

void fermerClient (){
  fermerTCP();
  fermerUDP();
  free(cl->id);
  free(cl);
}
