/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package write;

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
public class FileLockExclusiveWrite {
    public String book = "", message = "";
    int NumberOfRun;
    public FileLockExclusiveWrite(int NumberOfRun)
    {
        this.NumberOfRun = NumberOfRun;
    }
    public void FlexWrite() throws IOException, InterruptedException {
//        String filePath = "Z:\\examplefile.txt";    
        String filePath = "/MyRAM/examplefile.txt";
        
        Path path = Paths.get(filePath);
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
        
        message += "NumberOfRun: " + NumberOfRun + "   ThreadId of writer for reading1: " + Thread.currentThread().getId() + "      NumFailedattempts: " + NumFailedattempts + "<br>";
 
        ByteBuffer bufferR = ByteBuffer.allocate(100);
        int noOfBytesRead = fileChannel.read(bufferR);

        while (noOfBytesRead != -1) {
            bufferR.flip();
            
            while (bufferR.hasRemaining())
                book += (char)bufferR.get();   
            
            bufferR.clear();            
            noOfBytesRead = fileChannel.read(bufferR);
        }
        
        int position = book.indexOf("Mrs. Diver.");
        position += "Mrs. Diver.".length();
        fileChannel.truncate(position);
        fileChannel.close(); // also releases lock
        
        fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                
        NumFailedattempts = 0;
        validLock = false;
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
        
        message += "NumberOfRun: " + NumberOfRun + "   ThreadId of writer for writing: " + Thread.currentThread().getId() + "      NumFailedattempts: " + NumFailedattempts + "<br>";
                
        String input = "\r\n" + NumberOfRun;
        ByteBuffer bufferW = ByteBuffer.wrap(input.getBytes());
        fileChannel.write(bufferW);
        fileChannel.close(); // also releases lock
        
        fileChannel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
                
        NumFailedattempts = 0;
        validLock = false;
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
        
        message += "NumberOfRun: " + NumberOfRun + "   ThreadId of writer for reading2: " + Thread.currentThread().getId() + "      NumFailedattempts: " + NumFailedattempts + "<br>";
               
        book = "";
        bufferR.clear();            
        noOfBytesRead = fileChannel.read(bufferR);

        while (noOfBytesRead != -1) {
            bufferR.flip();
            
            while (bufferR.hasRemaining())
                book += (char)bufferR.get();   
            
            bufferR.clear();            
            noOfBytesRead = fileChannel.read(bufferR);
        }
        fileChannel.close(); // also releases lock
    }
}