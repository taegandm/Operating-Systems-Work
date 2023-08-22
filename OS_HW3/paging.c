#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[])
{
//check for two command line arguments
if(argc != 2)
{
	printf("Please enter a virtual memory address on the command line\n");
	return 0;
}

//store address and create variables
unsigned long address = atoi(argv[1]);
unsigned long page;
unsigned long offset;
static int PAGE_SIZE = 4096;

//calculate page and offset
page = address / PAGE_SIZE;
offset = address % PAGE_SIZE;

//display results
printf("The address %lu contains:\npage number = %lu\noffset = %lu\n", address, page, offset);

return 0;
}
