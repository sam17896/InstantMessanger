package instant.messaging;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    boolean start = false;
    int count = 0;
    public void runServer(){
        try{
            server = new ServerSocket(6789, 100);
            while(true){
                System.out.print("");
                if(start){
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                    //
                }catch(EOFException eofException){
                    showMessage("\n Server ended the connection!");
                }
                finally{
                    close();
                }
            }
            }
        }catch(IOException io){
            io.printStackTrace(); 
        }
    }
    private void waitForConnection() throws IOException{
        showMessage(" Waiting for someone to connect...\n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress().getHostName());
    }
 
    private void showMessage(final String text) {
        ServerForm.jTextArea1.append(text);
        
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now setup! \n");
    }

    private void whileChatting() throws IOException {
        String message = " Your are now Connected! ";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException e){
           showMessage("\n error"); 
        }
        }while(!message.equals("CLIENT - END"));
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

    public void ableToType(final boolean b) {
        ServerForm.jTextField1.setEditable(b);
       
    }

    public void sendMessage(String message) {
       try{
          output.writeObject("SERVER - " + message);
          output.flush();
          showMessage("\nSERVER - " + message);
       }catch(IOException e){
           ServerForm.jTextArea1.append("\n Error: can't send message \n");
       }
    }
        
}
