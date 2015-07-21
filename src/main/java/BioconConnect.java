
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Created by vincekyi on 7/17/15.
 */
public class BioconConnect {
    private static Properties prop = null;
    private static String json_list_url = "";
    private static final String BASE_URL = "http://bioconductor.org/packages/release/bioc";
    private static final String CSS_SELECTOR = "p,h2,h3,pre,tr,a";
    private static final String MAINTAINER_WORD = "Maintainer: ";
    private static final String DOCUMENTATION_WORD = "Documentation";
    private static final String INSTALLATION_WORD = "Installation";
    private static final String VERSION_WORD = "Version";
    private static final String LICENSE_WORD = "License";
    private static final String DEPENDS_WORD = "Depends";
    private static final String IMPORTS_WORD = "Imports";
    private static final String SUGGESTS_WORD = "Suggests";
    private static final String BIOCVIEWS_WORD = "biocViews";
    private static final String GITHUB_WORD = "GitHub source";
    private static final String SYSREQ_WORD = "SystemRequirements";
    private static final String PACKAGESRC_WORD = "Package Source";
    private static final int PRINT_SPACING = 40;

    

    private List<BioconductorTool> tools;
    private List<JSONObject> notFound;
    private int totalTools;
    private int totalRetrieved;
    private int numRetries;

    public BioconConnect(int retries) {
        tools = new ArrayList<BioconductorTool>();
        notFound = new ArrayList<JSONObject>();
        totalTools = 0;
        totalRetrieved = 0;
        numRetries = retries;
    }

    private JSONObject getJSON(String uri){

        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.get(uri)
                    .header("accept", "application/json")
                    .asJson();

            return request.getBody().getObject();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean getPackages(String url){

        JSONObject json = getJSON(url);

        if(json == null)
            return false;

        JSONArray tables = json.getJSONArray("tables");
        JSONArray packageList = tables.getJSONObject(0).getJSONArray("results");

        totalTools = packageList.length();
        for (int i = 0; i < totalTools; i++) {
            JSONObject jsonEntry = packageList.getJSONObject(i);
            BioconductorTool tool = populateWithInfo(jsonEntry, numRetries);
            if(tool==null) {
                notFound.add(jsonEntry);
                printStatus(tool.getName(), "Try again later");
            }
            else {
                totalRetrieved++;
                //System.out.println(tool);
                printStatus(tool.getName(), "processed");
            }
            System.out.println("*******************************************************");
        }

        System.out.println("RETRYING TOOLS NOT FOUND");
        //try tools not found
        for (int i = 0; i < notFound.size(); i++) {
            BioconductorTool tool = populateWithInfo(notFound.get(i), numRetries);
            if(tool!=null) {
                totalRetrieved++;
                printStatus(tool.getName(), "processed");
            }else
                printStatus(tool.getName(), "Failed to retrieve");
            System.out.println("*******************************************************");
        }
        return true;
    }

    private BioconductorTool populateWithInfo(JSONObject entry, int numTries){

        if(numTries <= 0)
            return null;

        BioconductorTool tool = new BioconductorTool();
        tool.setUrl(entry.getString("link"));
        tool.setName(entry.getString("link/_text"));
        tool.setDescription(entry.getString("description"));
        tool.setAuthors(entry.getString("value").split(","));

        Document doc = null;
        try {

            // need http protocol
            doc = Jsoup.connect(tool.getUrl()).get();


            Elements install_desc = doc.getElementsByClass("do_not_rebase");
            Elements info = install_desc.select("div").select(CSS_SELECTOR);
            for (int i = 0; i < info.size(); i++) {
                String line = info.get(i).text();
                String cl = info.get(i).nodeName();

                if(line.contains(MAINTAINER_WORD))
                    tool.setMaintainers(line.substring(line.indexOf(':')+1).split(","));
                else if(line.contains(INSTALLATION_WORD) && tool.getInstallation()==null){
                    if(info.get(i+1).nodeName().equals("p") && info.get(i+2).nodeName().equals("pre"))
                        tool.setInstallation(info.get(++i).text()+"\n"+info.get(++i).text());
                }
                else if(line.contains(DOCUMENTATION_WORD) && tool.getDocumentation()==null){
                    if(info.get(i+1).nodeName().equals("p") && info.get(i+2).nodeName().equals("pre"))
                        tool.setDocumentation(info.get(++i).text()+"\n"+info.get(++i).text());
                }
                else if(line.contains(VERSION_WORD) && tool.getVersion()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setVersion(elements.get(1).text());
                }
                else if(line.contains(LICENSE_WORD) && tool.getLicense()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setLicense(elements.get(1).text());
                }
                else if(line.contains(DEPENDS_WORD) && tool.getDependencies()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setDependencies(elements.get(1).text().split(","));
                }
                else if(line.contains(IMPORTS_WORD) && tool.getImports()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setImports(elements.get(1).text().split(","));
                }
                else if(line.contains(BIOCVIEWS_WORD) && tool.getBiocViews()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setBiocViews(elements.get(1).text().split(","));
                }
                else if(line.contains(GITHUB_WORD) && tool.getCodeRepo()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setCodeRepo(elements.get(1).select("a").get(0).attr("href"));
                }
                else if(line.contains(SYSREQ_WORD) && tool.getSystemReqs()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setSystemReqs(elements.get(1).text());
                }
                else if(line.contains(SUGGESTS_WORD) && tool.getSuggestions()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setSuggestions(elements.get(1).text().split(","));
                }
                else if(line.contains(PACKAGESRC_WORD) && tool.getPackageLink()==null){
                    Elements elements = info.get(i).select("td");
                    if(elements.size() > 1)
                        tool.setPackageLink(elements.get(1).select("a").get(0).attr("href").replace("..", BASE_URL));
                }


                //System.out.println("line = " + line+"\t"+cl);
            }
            tools.add(tool);


        } catch (IOException e) {
            //e.printStackTrace();
            printStatus(tool.getName(), "Retrieve Error");
            printStatus(tool.getName(), "Retrying...");
            try{
                TimeUnit.SECONDS.sleep(numRetries - (--numTries));
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            return populateWithInfo(entry, numTries);
        }
        return tool;
    }

    public List<BioconductorTool> getTools() {
        return tools;
    }

    public boolean extract(String properties) {
        InputStream input = null;
        prop = new Properties();
        try {//"./target/classes/application.properties"
            input = new FileInputStream(properties);

            // load a properties file
            prop.load(input);

            // get the property value and print it out

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        json_list_url = prop.getProperty("json_list_url");

        if(getPackages(json_list_url)) {
            System.out.println("Got packages!");
            return true;
        }
        else
            System.out.println("Failed to retrieve packages");

        return false;
    }

    private String convertToASCII(String utf8String){
        try {
            return new String(utf8String.getBytes("US-ASCII"));
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return utf8String;
    }

    private void printStatus(String toolName, String status){
        String message = toolName;
        int numSpaces = PRINT_SPACING - toolName.length();
        for (int i = 0; i < numSpaces; i++) {
            message +=" ";
        }
        message += status;
        System.out.println(message);
    }

    public float getPercentage(){
        return (totalRetrieved * 100.0f) / totalTools;
    }

}
