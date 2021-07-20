
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.space.LocalSpace;
import org.jpos.space.SpaceSource;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;
import org.jpos.transaction.TransactionManager;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

public class OdooParticipant implements TransactionParticipant{
    private String source;
    private String request;
    private String response;
    private LocalSpace isp;
    private long timeout = 70000L;

    public int prepare (long id, Serializable context) {
        Context ctx = (Context) context;
        ISOSource source = (ISOSource) ctx.get (this.source);
        ISOMsg m = (ISOMsg) ctx.get("REQUEST");

        try{
            
            m.setResponseMTI();
			m.set(39, "00");
            String name = m.getString("45"),
                    email = m.getString("43"),
                    phone = m.getString("46");

            String db = "odoo_db",
                    password = "c364751df97355f77b3b50ec675a4be206885ed7";
            final XmlRpcClient client = new XmlRpcClient();
            final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
            common_config.setServerURL(
                    new URL("http://localhost:8069/xmlrpc/2/common")
            );

            final XmlRpcClient models = new XmlRpcClient() {{
                setConfig(new XmlRpcClientConfigImpl() {{
                    setServerURL(new URL("http://localhost:8069/xmlrpc/2/object"));
                }});
            }};
            int uid = (int)client.execute(
                    common_config,
                    "authenticate",
                    asList(db, "omarquez@payall.com.ve", password, emptyMap())
            );
            HashMap<String,String> fields = new HashMap<String,String>();
            fields.put("name", name);
            fields.put("email",email);
            fields.put("phone", phone);
            final Integer userId = (Integer)models.execute("execute_kw", asList(
                    db, uid, password,
                    "res.partner", "create",
                    asList(fields)
            ));
            m.set(3, String.valueOf(userId));
            ctx.put("RESPONSE", m);
        }catch(Exception e){
            e.printStackTrace();
        }

        return PREPARED | READONLY;
    }
    public void commit (long id, Serializable context) {
        
    }

    public void abort (long id, Serializable context) {
    }

    
}