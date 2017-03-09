package instant.messaging.client;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client{
    
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String msg = "";
    public String serverIP;
    private Socket connection;
    boolean connect = false;
    
    void runClient(){
    while(!connect){    
         System.out.print("");  
    }
    try{
            ClientForm.jTextField1.setText("");
            ClientForm.jTextField1.setEditable(false);
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException e){
            showMessage("\n Client Terminated connection \n");
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            System.out.println("close");
            close();
        }
    }

    private void connectToServer() throws IOException{
        showMessage("Attempting connection...\n");
        connection = new Socket(InetAddress.getByName(serverIP),6789);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }
    
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nStreams are now setup! \n");
    }
    
    private void whileChatting() throws IOException {
        ableToType(true);
        do{
            try{
                msg = (String) input.readObject();
                showMessage("\n" + msg);
            }catch(ClassNotFoundException e){
           showMessage("\n error"); 
        }
        }while(!msg.equals("SERVER - END"));
    }
    
    private void close() {
        showMessage("\n Closing Connections....\n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try{
          output.writeObject("CLIENT - " + message);
          output.flush();
          showMessage("\nCLIENT - " + message);
       }catch(IOException e){
           ClientForm.jTextArea1.append("\n Error: can't send message \n");
       }
    }

    private void showMessage(final String m) {
        SwingUtilities.invokeLater(
                 new Runnable(){
                     public void run(){
                         ClientForm.jTextArea1.append(m);
                     }
                 }
        );
    }

    private void ableToType(final boolean b) {
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        ClientForm.jTextField2.setEditable(b);
                    }
                }
        );
    }
}
