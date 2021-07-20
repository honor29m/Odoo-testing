package Client.RestService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@SuppressWarnings("unchecked")
public class RestService {
    private String url;

    public RestService() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void sendGetRequest(String path) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {

            HttpGet request = new HttpGet( this.url + path);

            // add request headers
            // request.addHeader("custom-key", "mkyong");
            CloseableHttpResponse response = httpClient.execute(request);
            try {

                // Get HttpResponse Status
                System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK

                HttpEntity entity = response.getEntity();
                if (entity != null) {

                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    System.out.println(result);
                }

            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }

    public void sendPostRequest(String path) throws IOException{
        String result = "";
        HttpPost post = new HttpPost( this.url + path);

        // add request parameters or form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("userId", "1"));
        urlParameters.add(new BasicNameValuePair("title", "Testing"));
        urlParameters.add(new BasicNameValuePair("body", "secret"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)){

            result = EntityUtils.toString(response.getEntity());
        }

        System.out.println(result);
    }

    public void testOdooGetRequest() throws XmlRpcException, IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String db = "odoo-db",
            password = "bc9eeab33d1bf64b3092f33a138fd01ea5a27cd5";
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
                asList(db, "omarquez@payall.com.ve", password, emptyMap()));
        System.out.println(uid);
        Object object = models.execute("execute_kw", asList(
                db, uid, password,
                "product.product", "search_read",
                emptyList(),
                new HashMap() {{
                    put("fields", asList("id", "name"));
                }}
        ));
        //String json = gson.toJson( object);
        //System.out.println( json);
        String filePath = "/home/payall/Projects/odooTest/OdooModels/data.json";
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
            System.out.println("Json created in OdooModels");
        }
    }

    public void createOdooModel() throws MalformedURLException, XmlRpcException {
        String email = "agonzalez@payall.com.ve",
                name = "Armando Gonzalez",
                phone= "00000000000";
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
        final Integer id = (Integer)models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "create",
                asList(fields)
        ));
        System.out.println(id);
    }

    public void getFieldsOdooModel(String model) throws XmlRpcException, IOException{
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String db = "odoo-db",
                password = "bc9eeab33d1bf64b3092f33a138fd01ea5a27cd5";
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
        System.out.println(uid);
        //String model ="res.partner";
        Object object = models.execute("execute_kw", asList(
                db, uid, password,
                model, "search_read",
                emptyList(),
                new HashMap() {{
                    put("limit", 1);
                }}
        ));
        String filePath = "/home/payall/Projects/odooTest/OdooModels/"+ model + ".example.json";
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
            System.out.println("Json created in OdooModels");
        }
    }
}
