package instant.messaging;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
    private JTextField message;
    private JTextArea chatThread;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    
    public Server(){
        super("Server");
        message = new JTextField();
        message.setEditable(false);
        message.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        sendMessage(e.getActionCommand());
                        message.setText("");
                    }
                }
        );
        chatThread = new JTextArea();
        Dimension dc = new Dimension(500,450);
        Dimension m = new Dimension(400,50);
        chatThread.setMinimumSize(dc);
        chatThread.setPreferredSize(dc);
        chatThread.setMaximumSize(dc);
        message.setPreferredSize(m);
        add(message,BorderLayout.SOUTH);
        add(chatThread);
        add(new JScrollPane(chatThread), BorderLayout.CENTER);
        setSize(500,500);
        setVisible(true);

    }
    
    public void runServer(){
        try{
            server = new ServerSocket(6789, 100);
            while(true){
                try{
                    System.out.println("waiting");
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
        }catch(IOException io){
            io.printStackTrace(); 
        }
    }
    private void waitForConnection() throws IOException{
        System.out.println("waiting...");
        showMessage(" Waiting for someone to connect...\n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress().getHostName());
    }
 
    private void showMessage(final String text) {
        SwingUtilities.invokeLater(
                 new Runnable(){
                     public void run(){
                         chatThread.append(text);
                     }
                 }
        );
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

    private void ableToType(final boolean b) {
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        message.setEditable(b);
                    }
                }
        );
    }

    private void sendMessage(String message) {
       try{
          output.writeObject("SERVER - " + message);
          output.flush();
          showMessage("\nSERVER - " + message);
       }catch(IOException e){
           chatThread.append("\n Error: can't send message \n");
       }
    }
}  
