import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static public Map<String, String> queryParamsToMap(String query) {

        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    static DataStore accountDataStore = new DataStore();

    static class FundTransfer implements HttpHandler {

        Map<String, String> queryParams;

        @Override
        public void handle(HttpExchange exchange) {

            try {
                queryParams = queryParamsToMap(exchange.getRequestURI().getQuery());
                System.out.println(exchange.getRequestURI().getQuery());
                String response;
                Response transferResponse = accountDataStore.transferMoney(
                        queryParams.get("accountId1"), queryParams.get("accountId2"),
                        Float.parseFloat(queryParams.get("amount")));

                System.out.println(transferResponse.status);
                System.out.println(transferResponse.response);

                exchange.sendResponseHeaders(200, transferResponse.response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(transferResponse.response.getBytes());
                os.close();
                exchange.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class AccountRegistration implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange){

            Map<String, String> queryParams;

            try{
                queryParams = queryParamsToMap(exchange.getRequestURI().getQuery());
                System.out.println(exchange.getRequestURI().getQuery());
                String accountId = queryParams.get("accountId");
                Float startingBalance = ( queryParams.get("startingBalance") == null ) ? 0 : Float.parseFloat(queryParams.get("startingBalance"));
                Response registrationResponse = accountDataStore.RegisterAccount(accountId, startingBalance);

                System.out.println(registrationResponse.status);
                System.out.println(registrationResponse.response);

                exchange.sendResponseHeaders(200, registrationResponse.response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(registrationResponse.response.getBytes());
                os.close();
                exchange.close();

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    }

        public static HttpServer getServerObject() {

            HttpServer server = null;
            try {
                server = HttpServer.create(new InetSocketAddress(8000), 0);
                server.createContext("/transfer", new FundTransfer());
                server.createContext("/register", new AccountRegistration());
                server.setExecutor(null); // creates a default executor

            } catch (Exception e) {
                System.out.println("ERROR!!");
                e.printStackTrace();
            } finally {

                return server;
            }
        }
    }

