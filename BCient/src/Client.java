import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


// DANE do logowanie Login:user1 Pass:user1 Login:user2 Pass:user2
public class Client {
    private static JFrame login;
    public static boolean isLogged = false;
    public static String username;
    public static String userBalance;
    public static void main(String[] args){
        Socket s = null;
        try {
            s = new Socket("localhost", 1234);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            login = new LoginFrame(in, out);
            while(!isLogged){
                login.setVisible(true);
            }
            login.setVisible(false);
            JFrame accountFame = new AccountFrame(in, out);
            accountFame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Nie można połączyć się z serwerem", "Błąd", JOptionPane.ERROR_MESSAGE);
        }


//        String inmsg = "", outmsg = "";
//
//        outmsg = "1:user1:user1";
//        System.out.println("Sending");
//        out.writeUTF(outmsg);
//        out.flush();
//        inmsg = in.readUTF();
//        System.out.println(inmsg);
//        out.writeUTF("9:user1:user1");
//        out.flush();
//
//        s.close();
    }
}
