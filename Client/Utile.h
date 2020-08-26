void breakMess (char *s, char ** tab) {
  int i = 0, j = 0;
  char * buff = malloc(sizeof(char)*100);
  while(*s!='\0') {
    while(*s!=' ' && *s!='\0')
      buff[j++]=*(s++);
    if(*s!='\0')
      s++;
    buff[j]='\0';
    tab[i] = malloc(sizeof(char)*j+1);
    tab[i][0]='\0';
    j=0;
    strcpy(tab[i++], buff);
  }
  for (int n=i; n<7; n++){
    tab[n]=malloc(sizeof(char));
    *(tab[n])='\0';
  }
  free(buff);
}

void afficherMess (char ** tab) {
  for (int i=0; i<7; i++)
    printf("%s ", tab[i]);
  printf("\n");
}


int stoi (char *s) {
  char ** endptr = malloc(sizeof(char**));
  return (int)strtoul(s, endptr, 10);
  free(endptr);
}

int coToMess (char * s) {
  char ** commande = malloc(sizeof(char*)*7);
  breakMess (s, commande);
  
  if(!strcmp(commande[0], "games"))
    strcpy (s, "GAMES?***");
  
  else if(!strcmp(commande[0], "list")) {
    *s = '\0';
    strcat (strcat (strcat (s, "LIST? "), commande[1]), "***");
  }
    
  else if(!strcmp(commande[0], "size")){
    *s ='\0';
    strcat( strcat (strcat (s, "SIZE? "), commande[1]), "***");
  }
  
  else if(!strcmp(commande[0], "reg")){
    char port[12];
    sprintf(port, "%d", cl->portUDP);
    *s = '\0';
    strcat ( strcat (strcat ( strcat (strcat (strcat (strcat (s, "REG "), cl->id), " "), port), " "), commande[1]), "***");
  }
  
  else if(!strcmp(commande[0], "new")){
    char port[12]; 
    sprintf(port, "%d", cl->portUDP);
    *s = '\0';
    strcat ( strcat (strcat (strcat (strcat (s, "NEW "), cl->id), " "), port), "***");
  }

  else if(!strcmp(commande[0], "unreg")){
    *s = '\0';
    strcat (s, "UNREG***");
  }

  else if(!strcmp(commande[0], "start")){
    *s = '\0';
    strcat (s, "START***");
  }

  else if(!strcmp(commande[0], "all")){
    *s = '\0';
    strcat (s, "ALL? ");
    for (int i=1; i<7; i++)
      strcat(strcat(s, commande[i]), " ");
    strcat(s, "***");
  }

  else if(!strcmp(commande[0], "u")){
    *s = '\0';
    strcat ( strcat (strcat (s, "UP "), commande[1]), "***");
  }

  else if(!strcmp(commande[0], "d")){
   *s = '\0';
   strcat ( strcat (strcat (s, "DOWN "), commande[1]), "***");
  }

  else if(!strcmp(commande[0], "r")){
    *s = '\0';
    strcat (strcat (strcat (s, "RIGHT "), commande[1]), "***");
  }

  else if(!strcmp(commande[0], "l")){
    *s = '\0';
    strcat ( strcat (strcat (s, "LEFT "), commande[1]), "***");
  }

  else if(!strcmp(commande[0], "send")){
    *s = '\0';
    strcat (s, "SEND? ");
    for (int i=1; i<7; i++)
      strcat(strcat(s, commande[i]), " ");
    strcat(s, "***");
  }

  else if(!strcmp(commande[0], "quit")){
    *s = '\0';
    strcat ( strcat (s, "QUIT"), "***");
}

  else if(!strcmp(commande[0], "glist")){
    *s = '\0';
    strcat (s, "GLIST?***");
  }

  else {
    //free(commande);
    return 0;
  }

  //free(commande);
  return 1;
}
