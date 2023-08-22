#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define PAGE_TABLE_SIZE 256
#define PAGE_SIZE 256
#define TLB_SIZE 16
#define FRAME_SIZE 256
#define MEMORY_SIZE 256

unsigned char mask = 255;

int main(int argc, char* argv[])
{
    //variables to store file and current address
    FILE *addresses;
    int currentAddress;

    //open BACKING_STORE.bin for reading
    FILE *disk;
    disk = fopen("BACKING_STORE.bin", "rb");
    if(disk == NULL) {
        printf("Error opening BACKING_STORE.bin\n");
        exit(0);
    }

    //variables to store statistics
    int totalAddresses;
    int faults;
    int hits;

    // store current frame to write to in physical memory
    int frameToWrite = 0;
 
    //create TLB, initialize entries to -1 and create a counter for replacing table entries
    int tlb[2][TLB_SIZE];
    memset(tlb, -1, sizeof(tlb));
    int tlbCounter = 0;

    //create page table, initialize entries to -1
    int pageTable[PAGE_TABLE_SIZE];
    memset(pageTable, -1, sizeof(pageTable));

    //create physical memory of size 256*256
    char physicalMemory[MEMORY_SIZE][MEMORY_SIZE];

    //check if user inputs file on command line
    if(argc != 2) {
        printf("Please enter one file to read from on the command line\n");
        exit(0);
    }

    //attempt to open file
    addresses = fopen(argv[1], "r");
    if(addresses == NULL) {
        printf("Error opening file");
        exit(0);
    }

    //process each entry
    while(fscanf(addresses, "%d", &currentAddress) == 1) 
    {
        //print logical address
        printf("Logical Address: %d\t", currentAddress);

        //get page number (bits 9-16 of the address)
        int page = (currentAddress >> 8) & mask;

        //get offset (bits 1-8 of the address)
        int offset = currentAddress & mask;

        //variable to store the frame in physical memory
        int frame;

        //check the TLB for matches
        int found = 0;
        for(int i=0; i < TLB_SIZE; i++)
        {
            if(tlb[0][i] == page)
            {
                frame = tlb[1][i];
                found = 1;
                hits = hits + 1;
                break;
            }
        }

        //if not found, check pageTable
        if(found == 0)
        {
            //if entry is not yet loaded into memory, load it from the disk
            if(pageTable[page] == -1)
            {
                //note that a page fault occurs
                faults = faults + 1;

                //seek page
                if(fseek(disk, page * PAGE_SIZE, SEEK_SET) != 0)
                {
                    printf("Error in fseek\n");
                    exit(0);
                }

                //read page from disk into physical memory
                if(fread(physicalMemory[frameToWrite], sizeof(char), FRAME_SIZE, disk) == 0)
                {
                    printf("Error in fread\n");
                    exit(0);
                }

                //save frame in page table
                pageTable[page] = frameToWrite;

                //increment next frame to write to in physical memory
                frameToWrite = frameToWrite + 1;
            }

            //get frame from page table
            frame = pageTable[page];

            //add recently accessed page to TLB
            tlb[0][tlbCounter] = page;
                tlb[1][tlbCounter] = frame;
            tlbCounter = (tlbCounter + 1) % TLB_SIZE;
        }

        //print logical address
        int logicalAddress = (frame << 8) + offset;
        printf("Logical address: %d\t", logicalAddress);

        //get value from physical memory
        char value = physicalMemory[frame][offset];
        printf("Value: %d\n", value);

        //increment addresses processed
        totalAddresses++;
    }

    //calculate page fault and TLB hit statistics
    float pageFaults = ((float)faults / (float)totalAddresses) * 100.0;
    float TLBHits = ((float)hits / (float)totalAddresses) *100.0;
    printf("Page Fault Rate: %.2f%%\tTLB Hit Rate: %.2f%%\n", pageFaults, TLBHits);
}
