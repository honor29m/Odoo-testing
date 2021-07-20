package Q2.Client;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.packager.GenericPackager;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class App {
    public static void main(String[] args) {
        int port = 8000;
        String host = "localhost";
        GenericPackager packager;
        ISOMsg m;

        String name = "Dionny Fernandez",
                email = "dfernandez@payall.com.ve", 
                phone = "04140000000";
        try {
                    m = new ISOMsg();
                    m.setMTI("0200");

                    //Solicitud de chinazo
                    m.set("45", name); //Name
                    m.set("43", email); //Email
                    m.set("46", phone); //Phone

                    packager = new GenericPackager("cfg/base1.xml");
                    NACChannel channel = new NACChannel(host, port, packager, ISOUtil.hex2byte ("081210paya"));

                    channel.setTimeout(90000);
                    channel.connect();
                    channel.send(m);
                    ISOMsg r = channel.receive();

                    System.out.println("Codigo de respuesta: "+r.getString("39"));
                    System.out.println("Id usuario: "+r.getString("3"));

                    channel.disconnect();
                    String mti = r.getMTI();
        } catch (Exception e) {
            System.out.println("Receive Error IO ====> " + e.getMessage());
            e.printStackTrace();
        }
    }
}