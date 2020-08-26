#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>


int envoyerMess (char * s) {
  if(send(cl->socketTCP, s, strlen(s), 0) == -1)
    return -1;
  else
    return 1;
}

int receptionMess (char ** tab) {
  char * buff = malloc(sizeof(char)*100);
  int size_mess = recv (cl->socketTCP, buff, 99*sizeof(char), 0);
  if(size_mess==-1)
    return -1;
  else if(size_mess==0)
    return 0;
  else {
    buff[size_mess-3] = '\0';
    breakMess (buff, tab);
    free(buff);
    return 1;
  }
}

    

int setTCP () {
  char ** recue = malloc(sizeof(char *)*7);
  int compteur = 0;
  cl->infoTCP.sin_family = AF_INET;
  cl->infoTCP.sin_port = htons (4242);
  inet_aton(ip, &cl->infoTCP.sin_addr);
  int r = connect (cl->socketTCP, (struct sockaddr *) &cl->infoTCP, sizeof (struct sockaddr_in));
  if(r==0){
    receptionMess (recue);
    afficherMess (recue);
    compteur = stoi(recue[1]);
    for (int i=0; i<compteur; i++){
      receptionMess (recue);
      afficherMess (recue);
    }
    return 1;
  }else
    return 0;
}

void fermerTCP (){
}


int interfaceLobby() {
  char * s = malloc(sizeof(char)*100);
  char ** recue = malloc(sizeof(char*)*7);
  int compteur = 0;
  
  do{
    fgets(s, 99, stdin);
    s[strlen(s)-1]='\0';
  }while(!coToMess(s));
  
  while(strcmp(s, "QUIT") && strcmp(s, "START***"))
    {
      envoyerMess (s);
      receptionMess (recue);
      afficherMess (recue);

      if(!strcmp(recue[0], "GAMES")){
	compteur = stoi(recue[1]);
	for (int i=0; i<compteur; i++){
	  receptionMess (recue);
	  afficherMess (recue);
	}
      }

      else if(!strcmp(recue[0], "LIST!")){
	compteur = stoi(recue[2]);
	for (int i=0; i<compteur; i++) {
	  receptionMess (recue);
	  afficherMess (recue);
	}
      }

      do{
	fgets (s, 99, stdin);
	s[strlen(s)-1]='\0';
      }while(!coToMess(s));
    }
  
  if(strcmp(s, "QUIT")){
    envoyerMess (s);
    receptionMess (recue);
    afficherMess (recue);
    cl->IPMulti.imr_multiaddr.s_addr = inet_addr(recue[5]);
    cl->IPMulti.imr_interface.s_addr = htonl (INADDR_ANY);
    if(!set_Multi(stoi(recue[6]))){
      printf("Une erreur à eu lieu.\n");
      free(s);
      free(recue);
      return 0;
    }
    free(s);
    free(recue);
    return 1;
  }

  envoyerMess (s);
  receptionMess (recue);
  afficherMess (recue);
  free(s);
  free(recue);
  return 0;
}	 

void *boucleTCP(void *ptr){
  char * s = malloc(sizeof(char)*100);
  char ** recue = malloc(sizeof(char*)*7);
  int compteur = 0;

  receptionMess (recue);
  afficherMess (recue);
  
  do{
    fgets (s, 99, stdin);
    s[strlen(s)-1]='\0';
  }while(!coToMess(s));
  
  while(strcmp(s, "quit") && strcmp(s, "QUIT***"))
    {
      envoyerMess (s);
      receptionMess (recue);
      afficherMess (recue);
      
      if(!strcmp(recue[0], "GLIST!")) {
	compteur = stoi(recue[1]);
	for (int i=0; i<compteur; i++){
	  receptionMess (recue);
	  afficherMess (recue);
	}
      }

      do{
	fgets (s, 99, stdin);
	s[strlen(s)-1]='\0';
      }while(!coToMess(s));
    }
  
  receptionMess (recue);
  afficherMess (recue);
  fermerClient();

  free(s);
  free(recue);
  return NULL;
}
