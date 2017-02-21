package model;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Communicator{
    private String signature;
    private byte[] received = new byte[1024];
    private boolean receivedFlag = false;
    private DatagramPacket receivePacket = new DatagramPacket(received, received.length);
    private InetAddress IPAddress;
    private int sendPort = 9878;
    private int sendPortTCP = 9879;
    private int listeningPort = 9876;
    private int listeningPortTCP = 9879;
    private int outgoingPort = 9877;
    private ServerSocket hostSocket;

    public Communicator(String IP, String signature) throws IOException{
        this.signature = signature;


        if(!IP.equals("fc")){
            IPAddress = InetAddress.getByName(IP);
            sendPort = 9876;
            outgoingPort = 9875;
            listeningPort = 9878;
        }else{
            hostSocket = new ServerSocket(listeningPortTCP);
            Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
                try {
                    hostSocket.close();
                    System.out.println("The server is shut down!");
                } catch (IOException e) { /* failed */ }
            }});
        }

    }


    public void setIP(String IP) throws UnknownHostException{
        IPAddress = InetAddress.getByName(IP);
    }


    public byte[] communicateWithClient(byte[] payload) {

        byte[] returnLoad = null;
        try {

            //System.out.println("Server socket started");
            Socket comSock = hostSocket.accept();
            //System.out.println("Client connected");
            DataOutputStream dos = new DataOutputStream(comSock.getOutputStream());
            dos.write(payload);
            //System.out.println("Payload sent");
            DataInputStream dis = new DataInputStream(comSock.getInputStream());
            returnLoad = new byte[payload.length];
            dis.readFully(returnLoad);
            //System.out.println("Updateload received");

            comSock.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return returnLoad;
    }

    public byte[] communicateWithHost(byte[] payload) {


        byte[] returnLoad = null;
        try {
            Socket comSock = null;

            comSock = new Socket(IPAddress, sendPortTCP);


            DataInputStream dis = new DataInputStream(comSock.getInputStream());
            //System.out.println("Connected to host");
            returnLoad = new byte[payload.length];
            dis.readFully(returnLoad);
            //System.out.println("Updateload received");
            DataOutputStream dos = new DataOutputStream(comSock.getOutputStream());
            dos.write(payload);
            //System.out.println("Payload sent");
            comSock.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return returnLoad;
    }

    public void received(){
        receivedFlag = true;
    }

    public ArrayList<GameAction> getActions() {
        ArrayList<GameAction> actionStack= new ArrayList<>();
        return actionStack;
    }
}