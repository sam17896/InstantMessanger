package instant.messaging.client;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
    private JTextField message;
    public JTextField ipAddress;
    private JTextArea chatThread;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String msg = "";
    public String serverIP;
    private Socket connection;
    
    public Client(String host){
        super("Client");
        serverIP = host;
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
    
    void runClient(){
        try{
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

    private void sendMessage(String message) {
        try{
          output.writeObject("CLIENT - " + message);
          output.flush();
          showMessage("\nCLIENT - " + message);
       }catch(IOException e){
           chatThread.append("\n Error: can't send message \n");
       }
    }

    private void showMessage(final String m) {
        SwingUtilities.invokeLater(
                 new Runnable(){
                     public void run(){
                         chatThread.append(m);
                     }
                 }
        );
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
}
