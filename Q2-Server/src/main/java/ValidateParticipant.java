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

public class ValidateParticipant implements TransactionParticipant{
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
            String name = m.getString("45");
            String email = m.getString("43");
            String phone = m.getString("46");
            if ( name == null || email ==null || phone== null){
                return ABORTED | READONLY | NO_JOIN; 
            }
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