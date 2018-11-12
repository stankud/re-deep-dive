package lambda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.amazonaws.services.lambda.runtime.Context;


public class ParsePDF {

    public static class ParsePDFRequest {
        String filePath;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public ParsePDFRequest(String filePath) {
            this.filePath = filePath;
        }

        public ParsePDFRequest() {
        }
    }

    public static class ParsePDFResponse {
        String filePath;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public ParsePDFResponse(String filePath) {
            this.filePath = filePath;
        }

        public ParsePDFResponse() {
        }
    }

    public ParsePDFResponse handler(ParsePDFRequest event, Context context) {
        String jarpath = "lib/tabula-1.0.3-jar-with-dependencies.jar";
        String filePath = event.getFilePath();
        String lclFilePath = "myLocalPath.pdf";
        String comm = String.format("java -jar %s -r %s", jarpath, lclFilePath);
        String s;
        Process p;

        System.out.println(filePath);
        System.out.println(comm);
        try {
            p = Runtime.getRuntime().exec("ls -lah");
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);

            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {}
        return new ParsePDFResponse(event.getFilePath());
    }
}
