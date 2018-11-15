package lambda;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class ParsePDF {

  public static class ParsePDFRequest {
      String srcBucketName, srcKeyName, dstBucketName, dstKeyName;

      public String getSrcBucketName() {
          return srcBucketName;
      }

      public String getSrcKeyName() {
          return srcKeyName;
      }

      public void setSrcBucketName(String srcBucketName) {
          this.srcBucketName = srcBucketName;
      }

      public void setSrcKeyName(String srcKeyName) {
          this.srcKeyName = srcKeyName;
      }

      public String getDstBucketName() {
          return dstBucketName;
      }

      public String getDstKeyName() {
          return dstKeyName;
      }

      public void setDstBucketName(String dstBucketName) {
          this.dstBucketName = dstBucketName;
      }

      public void setDstKeyName(String dstKeyName) {
          this.dstKeyName = dstKeyName;
      }

      public ParsePDFRequest(String srcBucketName, String srcKeyName, String dstBucketName, String dstKeyName) {
          this.srcBucketName = srcBucketName;
          this.srcKeyName = srcKeyName;
          this.dstBucketName = dstBucketName;
          this.dstKeyName = dstKeyName;

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
        String srcBucketName = event.getSrcBucketName();
        String srcKeyName = event.getSrcKeyName();
        String dstBucketName = event.getDstBucketName();
        String dstKeyName = event.getDstKeyName();
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

            // Upload convrted file to s3
            PutObjectRequest request = new PutObjectRequest(
              dstBucketName, dstKeyName, new File(dstLclFilePath));
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setContentType("plain/text");
            // metadata.addUserMetadata("x-amz-meta-title", "someTitle");
            // request.setMetadata(metadata);
            s3.putObject(request);
        } catch (Exception e) {
            System.out.println ("Encountered exception: " + e);
        }
        return new ParsePDFResponse(event.getDstKeyName());
    }
}
