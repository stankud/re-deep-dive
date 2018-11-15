package lambda;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


public class ParsePDF {

    public static class ParsePDFRequest {
        String bucketName;
        String keyName;

        public String getBucketName() {
            return bucketName;
        }

        public String getKeyName() {
            return keyName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }

        public ParsePDFRequest(String bucketName, String keyName) {
            this.bucketName = bucketName;
            this.keyName = keyName;
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
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

        String jarpath = "lib/tabula-1.0.3-jar-with-dependencies.jar";
        String srcBucketName = event.getBucketName();
        String srcKeyName = event.getKeyName();
        String srcLclFilePath = "/tmp/input.pdf";
        String dstLclFilePath = "/tmp/output.csv";
        String convertComm = String.format(
          "java -jar %s -o %s -r %s", jarpath, dstLclFilePath, srcLclFilePath);
        String lsComm = "ls -lah /tmp";
        String s;
        Process convertP, lsP;

        try {
            // Download pdf from s3
            InputStream in = s3.getObject(
              srcBucketName, srcKeyName).getObjectContent();
            Files.copy(in, Paths.get(srcLclFilePath));

            // Convert pdf to csv
            System.out.println("Running: " + convertComm);
            convertP = Runtime.getRuntime().exec(convertComm);
            BufferedReader convertBr = new BufferedReader(
                new InputStreamReader(convertP.getInputStream()));

            while ((s = convertBr.readLine()) != null)
                System.out.println("line: " + s);

            convertP.waitFor();
            System.out.println ("exit: " + convertP.exitValue());
            convertP.destroy();

            // Log out /tmp directory
            System.out.println("Running: " + lsComm);
            lsP = Runtime.getRuntime().exec(lsComm);
            BufferedReader lsBr = new BufferedReader(
                new InputStreamReader(lsP.getInputStream()));

            while ((s = lsBr.readLine()) != null)
                System.out.println("line: " + s);

            lsP.waitFor();
            System.out.println ("exit: " + lsP.exitValue());
            lsP.destroy();
        } catch (Exception e) {
            System.out.println ("Encountered exception: " + e);
        }
        return new ParsePDFResponse(event.getKeyName());
    }
}
