package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MongoDBLogLoaderTest {

    @Test
    public void connect() {

//        String host = "172.21.73.101";
//        int port = 27017;
//        String userName = "adfactory";
//        String database = "MTLINKi";
//        String password = "adfactory";
//
//        MongoDBLogLoader connector = new MongoDBLogLoader(host, port, userName, database, password);
//
////        connector.connect(host, port, userName, database, password);
////        MongoCollection<Document> mongoCollection = db.getCollection("L1Signal_Pool_Active");
////        FindIterable<Document> iterDoc = mongoCollection.find();
////        for (Document document : iterDoc) {
////            System.out.println(document);
////        }
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.HOUR, -1);
////        lastUpdateTime = cal.getTime();
//
//
//        List<Bson> filters = Arrays.asList(
//                Filters.and(
//                        Filters.in("L1Name", "HAA"),
//                        Filters.in("signalname","ProductResultNumber_HAA"),
//                        Filters.lt("updatedate", cal.getTime()))
//        );
//
//        connector.getDocument(filters);
//
//        connector.disconnect();
//
//        int a=0;
//        ++a;
//


    }
}
