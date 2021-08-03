package com.aws.examples;

import java.io.IOException;

public class ExternCommand {
   
    public static void main(String[] args) {
         
        Process p;
        try {
            //executar rotina de backup
            p = Runtime.getRuntime().exec("/opt/gera-backup.sh"); 
            p.waitFor(); // espera pelo processo terminar
             
            // ....
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}