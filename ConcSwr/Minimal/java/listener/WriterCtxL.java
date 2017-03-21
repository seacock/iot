/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listener;

import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import write.FileLockExclusiveWrite;

/**
 * Web application lifecycle listener.
 *
 * @author andre
 */
class ThreadWrite extends Thread {    
    public String thrMsg = "";
    public String thrBook = "";
    int NumberOfRun;
    
    public ThreadWrite(int NumberOfRun)
    {
        this.NumberOfRun = NumberOfRun;
    }    
    
    @Override
    public void run(){  
        try {
            FileLockExclusiveWrite flew = new FileLockExclusiveWrite(NumberOfRun);
            flew.FlexWrite();                
            Thread.sleep(50);
            thrMsg += flew.message;
            thrBook = flew.book;
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }    
} 

public class WriterCtxL implements ServletContextListener {
    int NumberOfRun = 0;
    int missedWrite = 0;
    boolean nextThread = true;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        super.contextInitialized(sce); //To change body of generated methods, choose Tools | Templates.
        sce.getServletContext().setAttribute("Writer", "");
        sce.getServletContext().setAttribute("WriterFinished", "No");
        TimerTask writeTT = new WriteTT(sce);

        Timer timer = new Timer();
        timer.schedule(writeTT, 10, 100);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        super.contextDestroyed(sce); //To change body of generated methods, choose Tools | Templates.
    }
    
    class WriteTT extends TimerTask {        
        ServletContextEvent sce;
        
        public WriteTT(ServletContextEvent sce)
        {
            this.sce = sce;
        }
        
        @Override
        public void run() {
            String writerFinished = (String)sce.getServletContext().getAttribute("WriterFinished");
            if (writerFinished.matches("No") && nextThread == true) {
                nextThread = false;
                NumberOfRun++;
                
                ThreadWrite thW = new ThreadWrite(NumberOfRun);
                thW.start();   

                try {
                    thW.join();
                    String aggregate = (String)sce.getServletContext().getAttribute("Writer");                
                    aggregate += thW.thrMsg;
                    sce.getServletContext().setAttribute("Writer", aggregate);
                    sce.getServletContext().setAttribute("Book", thW.thrBook);
                }
                catch (Exception e) {
                    System.out.println(e);
                }

                System.out.println("Write    " + NumberOfRun);

                if (NumberOfRun == 400)
                {
                    NumberOfRun = 0;
                    sce.getServletContext().setAttribute("WriterFinished", "Yes");
                }
                
                nextThread = true;
            }
            else
                System.out.println("missedWrite    " + ++missedWrite);
            
            if (missedWrite == 10000)
                missedWrite = 0;
        }
    }    
}