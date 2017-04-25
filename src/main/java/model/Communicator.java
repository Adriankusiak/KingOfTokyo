package model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Communicator{
    private boolean running;
    private InetAddress IPAddress;
    //private int sendPortTCP = 9879;
    private ServerSocket hostSocket;
    private Socket clientSocket;
    private ActionEngine engineHandle;
    private ArrayList<Socket> clients;
    private HashMap<Socket, ObjectOutputStream> streamMap;
    private Game gameHandle;

    public Communicator(Game game, Boolean client, String IP, int port) throws IOException{
        streamMap = new HashMap<>();
        gameHandle = game;
        engineHandle = ActionEngine.getInstance();
        clients = new ArrayList<>();
        running = true;
        if(client){
            IPAddress = InetAddress.getByName(IP);
            clientSocket = new Socket(IPAddress, port);
            new Thread(() -> {
                receiveEvents(clientSocket, false);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }){{setDaemon(true);}}.start();

        }else{
            hostSocket = new ServerSocket(0);
            System.out.println("Server listening on " + hostSocket.getInetAddress() + ":" + hostSocket.getLocalPort());
            new Thread(()->receiveClients()).start();
        }

    }

    private void receiveHandshake(ObjectInputStream objectInputStream) {

        try {
            GameAction retrievedAction = (GameAction) objectInputStream.readObject();
            gameHandle.initialiseJoin(retrievedAction.getDeciData(), retrievedAction.getStringData());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void receiveClients() {
        try {
            while (running) {
                Socket newClient = hostSocket.accept();
                clients.add(newClient);
                sendAction(gameHandle.newPlayerAction(), newClient);
                new Thread(()->receiveEvents(newClient, true)).start();
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.out.println("Listening Closed");
    }


    public void receiveEvents(Socket comSocket, boolean share){
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(comSocket.getInputStream());
            if(clientSocket != null) receiveHandshake(objectInputStream);
            while(running) {
                System.out.println("Receiving events from client at "+ comSocket.getLocalAddress()+":"+comSocket.getLocalPort());
                GameAction retrievedAction = (GameAction) objectInputStream.readObject();
                if(share) informAllExcept(retrievedAction, comSocket);
                engineHandle.push(retrievedAction);
                //System.out.println("Updateload received");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void sendAction(GameAction payload, Socket target) {

        try {

            //System.out.println("Client connected");
            ObjectOutputStream oos = streamMap.get(target);
            if(oos == null){
                oos =new ObjectOutputStream(target.getOutputStream());
                streamMap.put(target, oos);
            }

            oos.writeObject(payload);

            //System.out.println("Updateload received");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void informAllExcept(GameAction action, Socket exception){
        for(Socket s : clients){
            if(s.equals(exception)) continue;
            sendAction(action, s);
        }
    }

    public void informAllEndTurn() {
        for(Socket s : clients){
            sendAction(new GameAction("END_TURN", null, null), s);
        }
    }

    public void informEndTurn() {
        sendAction(new GameAction("END_TURN", null, null), clientSocket);
    }

    public void informAllSelect(int i) {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(i);
        for(Socket s : clients){
            sendAction(new GameAction("SELECT_DICE", data, null), s);
        }
    }

    public void informSelect(int i) {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(i);
        sendAction(new GameAction("SELECT_DICE", data, null), clientSocket);
    }


    public void informUnselect(int i) {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(i);
        sendAction(new GameAction("UNSELECT_DICE", data, null), clientSocket);
    }

    public void informAllUnselect(int i) {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(i);
        for(Socket s : clients){
            sendAction(new GameAction("UNSELECT_DICE", data, null), s);
        }
    }

    public void informAllRoll(ArrayList<Integer> vals) {
        for(Socket s : clients){
            sendAction(new GameAction("ROLL", vals, null), s);
        }
    }

    public void informRoll(ArrayList<Integer> vals) {
        sendAction(new GameAction("ROLL", vals, null), clientSocket);
    }

    public void informAllResolve(Integer[] vals) {
        for(Socket s : clients){
            sendAction(new GameAction("RESOLVE", new ArrayList<Integer>(Arrays.asList(vals)), null), s);
        }
    }

    public void informResolve(Integer[] vals) {
        sendAction(new GameAction("RESOLVE", new ArrayList<Integer>(Arrays.asList(vals)), null), clientSocket);
    }

    public void informAllLeaveTokyo() {
        for(Socket s : clients){
            sendAction(new GameAction("LEAVE", null, null), s);
        }
    }

    public void informLeaveTokyo() {
        sendAction(new GameAction("LEAVE", null, null), clientSocket);
    }


    public void informAllBuyCard(String name) {
        for(Socket s : clients){
            sendAction(new GameAction("BUY", null, name), s);
        }
    }

    public void informBuyCard(String name) {
        sendAction(new GameAction("BUY", null, name), clientSocket);
    }


    public void informAll(GameAction action) {
        for(Socket s : clients){
            sendAction(action, s);
        }
    }

    public void inform(GameAction action) {
        sendAction(action, clientSocket);
    }

    public void endRunning() {
        try {
            if(hostSocket != null) hostSocket.close();
            for(Socket c : clients){
                try{
                    c.close();
                }catch (IOException e){}

            }
            System.out.println("The server is shut down!");
            running = false;
        } catch (IOException e) { /* failed */ }
    }
}