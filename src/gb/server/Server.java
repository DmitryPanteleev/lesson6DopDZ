package gb.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.logging.*;

public class Server {
    private static final Logger chatingHistoryLogger = Logger.getLogger("");
    private static String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH-mm"));
    private Vector<ClientHandler> clients;


    public Server() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;
        try {
            createChatLog();
            AuthService.connect();
            server = new ServerSocket(8190);
            System.out.println("Сервер запущен. Ожидаем клиентов...");
            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
//                clients.add(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);

        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public static void chateChatingHistoryLog(String event, String message) {
        chatingHistoryLogger.log(Level.INFO, " " + event + " " + message + "\n");
    }

    public static void createChatLog() throws IOException {
        System.out.println("create chat");
        Handler chatingHistoryHandler = new FileHandler(localDateTime + " chatingHistory.log", true);
        chatingHistoryHandler.setLevel(Level.ALL);
        chatingHistoryHandler.setFormatter(new SimpleFormatter());
        chatingHistoryLogger.addHandler(chatingHistoryHandler);
        chatingHistoryLogger.setLevel(Level.INFO);
        chatingHistoryLogger.getHandlers()[1].setLevel(Level.INFO);
        chatingHistoryLogger.getHandlers()[1].setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String msg = record.getLevel() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.YY HH:mm"))
                        + "\t" + record.getMessage();
                return msg;
            }
        });
        System.out.println("create chat successful");
    }
}
