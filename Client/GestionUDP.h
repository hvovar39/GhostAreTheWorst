#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>


void *boucleUDP(void *ptr){
  char * buff = malloc(sizeof(char)*100);
  char ** recue = malloc(sizeof(char*)*7);
  int size_mess;
  while(1)
    {
      if(*(int *)ptr)
	size_mess = recv (cl->socketUDP, buff, 99*sizeof(char), 0);
      else
	size_mess = recv (cl->socketMulti, buff, 99*sizeof(char), 0);
      if(size_mess>0){
	buff[size_mess-3] = '\0';
	breakMess (buff, recue);
	afficherMess(recue);
      }
    }
  free(buff);
  free(recue);
  return NULL;
}

int setUDP(){
  cl->infoUDP.sin_family = AF_INET;
  cl->infoUDP.sin_port = htons(cl->portUDP);
  cl->infoUDP.sin_addr.s_addr = htonl (INADDR_ANY);

  int r = bind(cl->socketUDP, (struct sockaddr *)&cl->infoUDP, sizeof(struct sockaddr_in));
  if(r==-1)
    return 0;
  return 1;
}

void fermerUDP(){
}

int set_Multi(int port){
  int ok=1;
  int r = setsockopt(cl->socketMulti, SOL_SOCKET, SO_REUSEPORT, &ok, sizeof(ok));
  if(r==-1){
    return 0;
  }
  cl->infoMulti.sin_family=AF_INET;
  cl->infoMulti.sin_port=htons(port);
  cl->infoMulti.sin_addr.s_addr=htonl(INADDR_ANY);
  
  r = bind(cl->socketMulti, (struct sockaddr *)&cl->infoMulti, sizeof(struct sockaddr_in));
  if(r==-1){
    return 0;
  }
  r = setsockopt(cl->socketMulti, IPPROTO_IP, IP_ADD_MEMBERSHIP, &(cl->IPMulti), sizeof(cl->IPMulti));
  if(r==-1){
    return 0;
  }
  return 1;
}
