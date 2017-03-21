/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author andre
 */
public class FileLockExclusiveRead {
    public String book = "", message = "";
    int NumberOfRun;
    public FileLockExclusiveRead(int NumberOfRun)
    {
        this.NumberOfRun = NumberOfRun;
    }
    public void FlexRead() throws IOException, InterruptedException {
//        Path path = Paths.get("Z:\\examplefile.txt");
        Path path = Paths.get("/MyRAM/examplefile.txt");
        FileChannel fileChannel;
        fileChannel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);

        int NumFailedattempts = 0;
        boolean validLock = false;
        while (validLock == false){
            try {  
                FileLock lock = fileChannel.lock();  // gets an exclusive lock
                validLock = lock.isValid();
            } 
            catch (OverlappingFileLockException e) {
                NumFailedattempts++;
                Thread.sleep(10);
            }          
        }

        message += "NumberOfRun: " + NumberOfRun + "   ThreadId for reading: " + Thread.currentThread().getId() + "      NumFailedattempts: " + NumFailedattempts;

        ByteBuffer buffer = ByteBuffer.allocate(100);
        int noOfBytesRead = fileChannel.read(buffer);

        while (noOfBytesRead != -1) {
            buffer.flip();
            
            while (buffer.hasRemaining())
                book += (char)buffer.get();   
            
            buffer.clear();            
            noOfBytesRead = fileChannel.read(buffer);
        }

        fileChannel.close();
    }
}