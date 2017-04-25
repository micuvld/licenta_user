package actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import command.Command;
import command.CommandState;
import ldap.LdapConnector;
import mongo.MongoConnector;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.Configs;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by vlad on 06.04.2017.
 */
@WebServlet("/resolve_command")
public class ResolveCommand extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String commandId = request.getParameter("commandId");
        ObjectMapper objectMapper = new ObjectMapper();

        MongoCollection<Document> collection = MongoConnector.getCollection("spital", Configs.MONGO_COMMANDS_COLLECTION);
        Document commandDoc = collection.find(eq("_id", Long.parseLong(commandId))).first();
        System.out.println(commandDoc.getString("issuer"));
//        Command command = objectMapper.readValue(commandDoc.toJson(), Command.class);

        Document setQuery = new Document("$set", new Document("state", CommandState.APPROVED.name()));
        collection.updateOne(eq("_id", Long.parseLong(commandId)), setQuery);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
