#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <fcntl.h> // gia file descriptors
#include <sys/types.h>
#include <sys/stat.h>  // gia flag tou mode
#include <sys/wait.h>
#include <unistd.h>
#include <string.h>
#include <readline/readline.h>
#include <signal.h>
#include <dirent.h>
#include <fnmatch.h>
#define PATH_MAX 4096

struct cmd{
    char* argv[6];//pinakas opou apothikeuetai i entoli,ta 4 epitrepomena orismata ths kai mia timh null
    int argc;//counter opou apothikeutai o arithmos twn orismatwn tin entolis
    struct redirection{
        bool on;
        bool append;
        char* file;
    } redin,redout;

};

char** argv_wild; // o neos pinakas orismatwn opou h kanonikh ekfrasi me to wildcard tha antikatastathei apo ta arxeia ta opoia auth perigrafei
int wposition; // thesi tou orismatos pou vrisketai to wildcard ston pinaka orismatwn

struct cmd commands[2];
bool pipeOn;

char* internal[]={"exit","cd",NULL};//pinakas me eswterikes entoles
int fd;

void parsing(int numberOfCommand,char buffer[100]);//sinartisi gia apokodikopoiisi tis entolis
bool contains(char *str1,char *list[]);//epistrefei true otan i simvoloseira str1 periexetai ston pinaka sumvoloseirwn list
void killZombies();
bool wildMatch(char* currentPath);
void runPipe(int pfd[]);

void redirOutAppend(int numberOfCommand);
void redirOut(int numberOfCommand);
void redirIn(int numberOfCommand);

int main()
{
    chdir(getenv("HOME"));//arxikopoiw current working directory se HOME
    const char* hostname = getenv( "HOSTNAME" );

    char *input;//grammi entolwn poy eisagei o xristis
    char* path=NULL; // aksiopoieitai apo getcwd

    rl_bind_key('\t',rl_complete);//ylopoiei pliktro tab gia autosimplirwsi orismatwn

    bool background;
    int pfd[2];

    signal(SIGCHLD,killZombies);

    //vroxos o opoios epanalamvanetai mexri o xristis na eisagei exit
    while(1){

        printf("%s@%s %s",getenv("USER"),(hostname ? hostname : "HOSTNAME not defined!"),getcwd(path,PATH_MAX));
        input=readline("$");
        if (!strcmp(input,"")){ //otan o xristis den eisagei tipota
            free(input);continue;
        }
        parsing(0,input);
        if (!strcmp("&",commands[0].argv[commands[0].argc-1])){  //kathorizei tin timh tis metavlitis background
            background=true; commands[0].argv[commands[0].argc-1]=NULL;
        }
        else background = false;
        if (pipeOn) {pipe(pfd);}
        if(contains(commands[0].argv[0],internal)){// gia eswterikes entoles kaleitai kateutheian klhsh susthmatos
            if(!strcmp("cd",commands[0].argv[0])){
                if(chdir(commands[0].argv[1])!=0)   write(1,"no such file or directory\n",26);
            }
            else if(!strcmp("exit",commands[0].argv[0]))    exit(EXIT_SUCCESS);
            }

        else{
            switch (fork()){//gia ekswterikes entoles dimioyrgei kainoyrgia diergasia protou ginei h klhsh tou front-end ths exec
               case 0://an einai paidi
                  if(commands[0].redout.on==true){
                      if(commands[0].redout.append==true){// '-->'
                          redirOutAppend(0);
                    }
                      else{ // '==>'
                          redirOut(0);
                        }
                  }
                  if(commands[0].redin.on==true){// '<=='
                      redirIn(0);
                  }
                  if (pipeOn){ // '|'
                    runPipe(pfd);
                    perror("exec_pipe");
                    _exit(EXIT_FAILURE);
                  }
                  if ((wposition>0)&&(wposition<5)){// '*'
                    if (wildMatch(getcwd(path,PATH_MAX)))    execvp(argv_wild[0], argv_wild);
                    else break;
                  }

                  execvp(commands[0].argv[0],commands[0].argv);
                  perror("exec"); // den prepei na ftasei edw - i exec den epistrefei tipota ektos an kati paei strava
                  _exit(EXIT_FAILURE);
                  break;

               case -1://error
                  perror("fork");
                  break;
               default://gonios
                    if (pipeOn){
                        if (close(pfd[0]) == -1) perror("close");
                        if (close(pfd[1]) == -1) perror("close");
                    }
                    if (!background){
                        while (wait(NULL)!=-1) continue;
                    }
                }
        }
        free(input); // eswterika i realine ulopoieitai me malloc gi' auto kai xreiazetai na ginei free ston deikti input
        if (argv_wild) free(argv_wild);
    }

}

void parsing(int numberOfCommand,char buffer[100]){
char* result;
char* delims=" \t\n";
char* rem;

commands[numberOfCommand].redin.on=false;
commands[numberOfCommand].redout.on=false;
commands[numberOfCommand].redout.append=false;
wposition=0;
argv_wild=NULL;

if (numberOfCommand==0) pipeOn=false;

int i;
commands[numberOfCommand].argc=0;
// arxikopoiw deiktes gia apofigi provlimatos
for(i=0;i<6;i++) commands[numberOfCommand].argv[i]=NULL;
for(result=strtok_r(buffer,delims,&rem);result;result=strtok_r(NULL,delims,&rem))
{

    if(!strcmp(result,"==>")){
        commands[numberOfCommand].redout.on=true;
        commands[numberOfCommand].redout.append=false;
        result=strtok_r(NULL,delims,&rem);
        commands[numberOfCommand].redout.file=result;
        continue;
    }
    if(!strcmp(result,"<==")){
        commands[numberOfCommand].redin.on=true;
        commands[numberOfCommand].redin.append=false;
        result=strtok_r(NULL,delims,&rem);
        commands[numberOfCommand].redin.file=result;
        continue;
    }
    if(!strcmp(result,"-->")){
        commands[numberOfCommand].redout.on=true;
        commands[numberOfCommand].redout.append=true;
        result=strtok_r(NULL,delims,&rem);
        commands[numberOfCommand].redout.file=result;
        continue;
    }
    if (strchr(result,'|')){

        pipeOn=true;
        parsing(1,rem);
        break;
    }
    commands[numberOfCommand].argv[commands[numberOfCommand].argc]=result;
    if (strchr(result,'*')) wposition=commands[numberOfCommand].argc; // apothikeuei ti thesi tou orismatos pou vrethike to wildcard
    commands[numberOfCommand].argc++;
    if (commands[numberOfCommand].argc==5) break;//diasfalizei oti akoma ki an o xristis valei panw apo 4 orismata to shell tha aksiopoihsei mono ta 4 prwta xwris na kolisei
}

}

bool contains(char *str,char *list[]){//elegxei an i simvoloseira toy prwtoy orismatos periexetai ston pinaka twn simvoloseirwn toy deyteroy

    while(*list)
    {
        if(!strcmp(str,*list)) return true;
        list++;
    }
    return false;

}

void killZombies(){
    pid_t child;
    while ((child=waitpid(-1,NULL,WNOHANG))>0){ // perimene gia sima SIGCHLD apo opoiodipote paidi alla ama den erthei kanena mhn mplokareis
        printf("Process %d dead",child); // kati allo me sig
        continue;
    }
    signal(SIGCHLD,killZombies); // o handler orizetai ksana gia na isxuei kai gia epomeno sima
}

bool wildMatch(char* cp){ // dimourgei ton argv_wild

    DIR* dirp = opendir(cp);
    struct dirent* dp;
    unsigned int size=0;
    int i;
    int j=0;
    char** temp;
    // 1. mexri wposition pernaw auta pou idi uparxoun
    argv_wild=(char**)malloc(wposition*sizeof(char*));
    for (i=0;i<wposition;i++){
        argv_wild[j]=commands[0].argv[i];j++;
    }
    size=wposition*sizeof(char*);
    // 2. vazei sti thesi tou orismatos me wildcard ta arxeia pou vriskei na tairiazoun me regex
    if (dirp){
    while ((dp = readdir(dirp))) {
        if (fnmatch(commands[0].argv[wposition],dp->d_name,FNM_NOESCAPE)==0) {
            temp=realloc(argv_wild,size+sizeof(char*));
            if (temp!= NULL) argv_wild=temp;
            size=size+sizeof(char*);
            argv_wild[j]=dp->d_name;
            j++;
        }
    }
    }
    if (size==wposition*sizeof(char*)) {
        write(1,"no such files\n",14);
        return 0;
    }
    // 3. vazw orismata argv pou ipoleipontai
    temp=realloc(argv_wild,size+(commands[0].argc-wposition)*sizeof(char*));
    if (temp!=NULL) argv_wild=temp;
    size=size+(commands[0].argc-wposition)*sizeof(char*);
    for(i=wposition+1;i<commands[0].argc;i++){
        argv_wild[j]=commands[0].argv[i];j++;
    }
    // 4. vazw NULL  (pou xreiazetai apo exec) gia teleutaio orisma
    argv_wild[j]=NULL;
    return 1;


}

void redirOutAppend(int numberOfCommand){
    fd=open(commands[numberOfCommand].redout.file,O_CREAT|O_RDWR|O_APPEND,S_IRWXU);  // file mode bits: read, write, execute/search by owner
    if(fd!=-1){
        dup2(fd,1); // tha kleisei ton 1-stdout kai tha antigrapsei sti thesi tou ton fd
        if(close(fd)==-1) perror("close");
        fflush(stdout); // flush ta ekremh dedomena gia na min tupwthoun kai auta sto arxeio pou upodeiknuetai apo perigrafea arxeiwn
    }
}

void redirOut(int numberOfCommand){
    fd=open(commands[numberOfCommand].redout.file,O_CREAT|O_RDWR|O_TRUNC,S_IRWXU);
    if(fd!=-1){
        dup2(fd,1);
        if(close(fd)==-1) perror("close");
        fflush(stdout);
    }
}

void redirIn(int numberOfCommand){
    fd=open(commands[numberOfCommand].redin.file,O_RDONLY);
    if(fd!=-1){
        dup2(fd,0);
        if(close(fd)==-1) perror("close");
    }
}

void runPipe(int pfd[]){//ylopoiisi diplis diaswlinwsis

    switch(fork()){
        case -1:
            perror("fork_pipe");
            exit(EXIT_FAILURE);
        case 0: // paidi - paidi tou proigoumenou paidiou pou tha grapsei kai kleinei tous perigrafeis arxeiwn anagnwsis
            close(pfd[0]);
            dup2(pfd[1],1);
            close(pfd[1]);
            if(commands[1].redout.on==true){
                if(commands[1].redout.append==true){ // '-->'
                    redirOutAppend(1);
                }
                else{// '==>'
                    redirOut(1);
                    }
            }
            if(commands[1].redin.on==true){ // '<=='
                redirIn(1);
            }
            execvp(commands[0].argv[0],commands[0].argv);
            perror("exec");
            _exit(EXIT_FAILURE);
        default: // gonios - paidi proigoumenis fork pou tha diavasei kai kleinei tous pergirafis arxeiwn gia eggrafi
            close(pfd[1]);
            dup2(pfd[0],0);
            close(pfd[0]);
            if(commands[1].redout.on==true){
                if(commands[1].redout.append==true){ // '-->'
                    redirOutAppend(1);
                }
                else{// '==>'
                    redirOut(1);
                    }
            }
            if(commands[1].redin.on==true){ // '<=='
                redirIn(1);
            }
            execvp(commands[1].argv[0], commands[1].argv);
            perror("exec");
            exit(EXIT_FAILURE);

    }
}










