package instant.messaging.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
public class InstantMessagingClient {
    public static void main(String[] args) {
        Client c = new Client("127.0.0.1");
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.runClient();
        
    }
    
}
