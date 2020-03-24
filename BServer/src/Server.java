import javax.print.DocFlavor;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.util.ArrayList;

public class Server {
    private static final String FILEPATH = "C:\\Users\\pawel\\IdeaProjects\\BServer\\src\\users.txt";

    private static String withdrawal(String username, float amount) throws IOException {
        BufferedReader reader;
        FileInputStream fIn;
        PrintWriter pw;
        ArrayList<String> linesToSave = new ArrayList<String>();
        fIn = new FileInputStream(new File(FILEPATH));
        reader = new BufferedReader(new InputStreamReader(fIn));

        float newBalance;

        String lineToRead;
        lineToRead = reader.readLine();
        while(lineToRead != null){
            linesToSave.add(lineToRead);
            lineToRead = reader.readLine();
        }
        fIn.getChannel().position(0);
        reader = new BufferedReader(new InputStreamReader(fIn));
        int lineNumber = 0;
        String line = reader.readLine();
        System.out.println(line);
        while(line != null){
            String[] splittedLine = line.split(":");  // user:password:balance
            if(username.equals(splittedLine[0])){
                float balance = Float.parseFloat(splittedLine[2]);
                float res = balance -amount;
                if(res >= 0){
                    String foundedLine = linesToSave.get(lineNumber);
                    String[] splittedFoundedLine = foundedLine.split(":");
                    splittedFoundedLine[2] = Float.toString(res);
                    String updatedLine = String.join(":", splittedFoundedLine);
                    linesToSave.set(lineNumber, updatedLine);
                    reader.close();
                    pw = new PrintWriter(new FileOutputStream(new File(FILEPATH)));
                    for(String fileLine : linesToSave){
                        pw.println(fileLine);
                    }
                    pw.close();
                    System.out.println("Wypłacono " + amount + " użytkownikowi " + splittedLine[0]);
                    return Float.toString(res);
                }else{
                    System.out.println("Uzytkownik " + splittedLine[0] + " ma za mało środków");
                    reader.close();
                    return "TOOMUCH";
                }
            }
            line = reader.readLine();
            lineNumber++;
        }
        reader.close();
        System.out.println("ERROR");
        return "null";
    }

    private static String login(String username, String password) throws IOException {
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(new File(FILEPATH)));
        String line = reader.readLine();
        while(line != null){
            String[] splittedLine = line.split(":");
            System.out.println(splittedLine[0] + " " + splittedLine[1]);
            System.out.println(username + " " + password);
            if(username.equals(splittedLine[0]) && password.equals(splittedLine[1])){
                System.out.println("Zalogowano " + username);
                return splittedLine[2];
            }
            line = reader.readLine();
        }
        reader.close();
        return "null";
    }

    private static String transfer(String username, String recipient, float amount) throws IOException {
        BufferedReader reader;
        FileInputStream fIn;
        PrintWriter pw;
        ArrayList<String> linesToSave = new ArrayList<String>();
        fIn = new FileInputStream(new File(FILEPATH));
        reader = new BufferedReader(new InputStreamReader(fIn));

        float recipientBalance;

        String lineToRead;
        lineToRead = reader.readLine();
        int recipientLineNumber = -1;
        int i=0;
        while(lineToRead != null){
            String[] splitted = lineToRead.split(":");
            if(splitted[0].equals(recipient)){
                recipientLineNumber=i;
                recipientBalance = Float.parseFloat(splitted[2]);
            }
            linesToSave.add(lineToRead);
            lineToRead = reader.readLine();
            i++;
        }
        fIn.getChannel().position(0);
        reader = new BufferedReader(new InputStreamReader(fIn));
        int lineNumber = 0;
        String line = reader.readLine();
        System.out.println(line);
        while(line != null){
            String[] splittedLine = line.split(":");  // user:password:balance
            if(recipientLineNumber==-1){
                fIn.close();
                reader.close();
                return "NOTFOUND";
            }
            if(username.equals(splittedLine[0])){
                float balance = Float.parseFloat(splittedLine[2]);
                float res = balance - amount;
                if(res >= 0){
                    String foundedLine = linesToSave.get(lineNumber);
                    String[] splittedFoundedLine = foundedLine.split(":");
                    splittedFoundedLine[2] = Float.toString(res);
                    String updatedLine = String.join(":", splittedFoundedLine);
                    linesToSave.set(lineNumber, updatedLine);
                    String recipientLine = linesToSave.get(recipientLineNumber);
                    String[] recipientLineSplitted = recipientLine.split(":");
                    float resRec = amount + Float.parseFloat(recipientLineSplitted[2]);
                    recipientLineSplitted[2] = Float.toString(resRec);
                    String updatedRecipient = String.join(":", recipientLineSplitted);
                    linesToSave.set(recipientLineNumber, updatedRecipient);
                    reader.close();
                    pw = new PrintWriter(new FileOutputStream(new File(FILEPATH)));
                    for(String fileLine : linesToSave){
                        pw.println(fileLine);
                    }
                    pw.close();
                    fIn.close();
                    System.out.println("Przalano " + amount + "  z konta  " + username + " na konto " + recipient);
                    return Float.toString(res);
                }else{
                    System.out.println("Uzytkownik " + splittedLine[0] + " ma za mało środków");
                    reader.close();
                    fIn.close();
                    return "TOOMUCH";
                }
            }
            line = reader.readLine();
            lineNumber++;
        }
        reader.close();
        fIn.close();
        System.out.println("ERROR");
        return "null";
    }


    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1234);
        System.out.println("Server starts at port: 1234");

        Socket s;

        while(true){
            s = ss.accept();
            System.out.println("Connected with: " + s);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream()) ;

            String inmsg = "", outmsg = "";

            boolean done = true;
            while(done){
                inmsg = in.readUTF();
                String[] parsedMsg = inmsg.split(":");
                int option = Integer.parseInt(parsedMsg[0]);
                // 1 logowanie 2 przelew 3 wyplata
                switch (option){
                    case 1:
                        System.out.println("Próba logowanie użytkownika " + parsedMsg[1]);
                        String loginStatus = login(parsedMsg[1], parsedMsg[2]);
                        if(!loginStatus.equals("null")){
                            out.writeUTF(loginStatus);
                            out.flush();
                            System.out.println("Logowanie użytkownika " + parsedMsg[1] + " OK" + " || Wysłano stan konta " + loginStatus);
                        }else{
                            outmsg = "NOK";
                            out.writeUTF(outmsg);
                            out.flush();
                            System.out.println("Logowanie użytkownika " + parsedMsg[1] + " NOK");
                        }
                        break;
                    case 2:
                        System.out.println("Próba przelewu użytkownika " + parsedMsg[1]);
                        String transferStatus = transfer(parsedMsg[1], parsedMsg[2], Float.parseFloat(parsedMsg[3]));
                        if(transferStatus.equals("NOTFOUND")){
                            outmsg = "NOTFOUND";
                            out.writeUTF(outmsg);
                            out.flush();
                        }
                        if(transferStatus.equals("null")){
                            outmsg = "ERR";
                            out.writeUTF(outmsg);
                            out.flush();
                        }
                        if(transferStatus.equals("TOOMUCH")){
                            outmsg = "TOOMUCH";
                            out.writeUTF(outmsg);
                            out.flush();
                        }else{
                            outmsg = transferStatus;
                            System.out.println(transferStatus);
                            out.writeUTF(outmsg);
                            out.flush();
                        }
                        break;
                    case 3:
                        System.out.println("Próba wypłaty użytkownika " + parsedMsg[1]);
                        float amount = Float.parseFloat(parsedMsg[2]);
                        String username = parsedMsg[1];
                        String withdrawalStatus = withdrawal(username, amount);
                        if(withdrawalStatus.equals("null")){
                            outmsg = "ERR";
                            out.writeUTF(outmsg);
                            out.flush();
                        }
                        if(withdrawalStatus.equals("TOOMUCH")){
                            outmsg = "TOOMUCH";
                            out.writeUTF(outmsg);
                            out.flush();
                        }else{
                            outmsg = withdrawalStatus;
                            out.writeUTF(outmsg);
                            out.writeUTF(outmsg);
                            out.flush();
                        }

                        break;
                    case 9:
                        System.out.println("Rozłączenie użytkownika " + parsedMsg[1]);
                        done = false;
                        break;
                    default:
                        out.writeUTF("Błedne żadanie.");
                        done = false;
                        break;
                }
            }
            s.close();
        }
}
}
