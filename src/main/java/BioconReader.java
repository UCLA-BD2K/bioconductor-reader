import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vincekyi on 7/21/15.
 */
public class BioconReader {

    public final static String DEFAULT_OUTPUT_PATH = "./bioconductor.json";
    public final static String PROGRAM_NAME = "Bioconductor Reader";
    public static final int DEFAULT_MAX_RETRY = 5;


    public static void main(String[] args) {

        // Create options
        Options options = new Options();

        Option.Builder optHelpBuilder = Option.builder("h");
        optHelpBuilder.longOpt("help");
        optHelpBuilder.desc("");
        Option optHelp = optHelpBuilder.build();
        options.addOption(optHelp);

        Option.Builder optFileBuilder = Option.builder("p");
        optFileBuilder.longOpt("properties");
        optFileBuilder.desc("(REQUIRED) Input properties file");
        optFileBuilder.hasArg();
        optFileBuilder.required();
        Option optFile = optFileBuilder.build();
        options.addOption(optFile);


        Option.Builder optOutputPathBuilder = Option.builder("o");
        optOutputPathBuilder.longOpt("output");
        optOutputPathBuilder.desc("Output path");
        optOutputPathBuilder.hasArg();
        Option optOutputPath = optOutputPathBuilder.build();
        options.addOption(optOutputPath);

        Option.Builder optNumRetriesBuilder = Option.builder("r");
        optNumRetriesBuilder.longOpt("retries");
        optNumRetriesBuilder.desc("Number of tries when requesting server when an error occurs. 0<r<50  Default: 5");
        optNumRetriesBuilder.hasArg();
        Option optNumRetries = optNumRetriesBuilder.build();
        options.addOption(optNumRetries);


        HelpFormatter formatter = new HelpFormatter();
        String header = "Retrieve all metadata about packages in Bioconductor"  + "\n\n";
        String footer = "\n";

        // Parse command line arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return ;
        }

        // Process arguments
        if (cmd.hasOption(optHelp.getOpt())) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return ;
        }

        String properties = null;
        if (cmd.hasOption(optFile.getOpt())) {
            properties = cmd.getOptionValue(optFile.getOpt());
        }


        String outputPath = DEFAULT_OUTPUT_PATH;
        if (cmd.hasOption(optOutputPath.getOpt())) {
            outputPath = cmd.getOptionValue(optOutputPath.getOpt());
            System.out.println(outputPath);
        }

        int numRetries = DEFAULT_MAX_RETRY;
        if (cmd.hasOption(optNumRetries.getOpt())) {
            try {
                numRetries = Integer.parseInt(cmd.getOptionValue(optNumRetries.getOpt()));

            }catch(NumberFormatException e){
                System.out.println("Input for retries must be an integer");
                return;
            }
            if(numRetries < 0 || numRetries > 50){
                System.out.println("Input for retries must be greater than 0 and less than 50");
                return;
            }

        }


        BioconConnect connector = new BioconConnect(numRetries);

        if(connector.extract(properties)) {
            GsonBuilder builder = new GsonBuilder();
            builder.disableHtmlEscaping();
            Gson gson = builder.create();

            // convert java object to JSON format,
            // and returned as JSON formatted string
            String json = gson.toJson(connector.getTools());

            try {
                //write converted json data to a file named "file.json"
                FileWriter writer = new FileWriter(outputPath);
                writer.write(json);
                writer.close();
                System.out.println("Percentage of tools retrieved: "+connector.getPercentage()+"%");
                System.out.println("Successfully created JSON file");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
