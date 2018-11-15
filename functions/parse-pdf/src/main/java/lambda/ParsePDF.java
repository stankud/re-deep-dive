package lambda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.FileOutputStream;
import java.io.File;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

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
        String comm = String.format(
          "java -jar %s -o %s -r %s", jarpath, dstLclFilePath, srcLclFilePath);
        String s;
        Process p;

        System.out.println(srcKeyName);
        System.out.println(comm);
        try {
            S3Object o = s3.getObject(srcBucketName, srcKeyName);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(srcLclFilePath));
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(srcBucketName).withMaxKeys(2);
            ListObjectsV2Result result;

            do {
                result = s3.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
                }
                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                System.out.println("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());

            p = Runtime.getRuntime().exec("ls -lah /tmp");
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);

            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            System.out.println ("Encountered exception: " + e);
        }
        return new ParsePDFResponse(event.getKeyName());
    }
}
