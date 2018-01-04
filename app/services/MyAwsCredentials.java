package services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.typesafe.config.ConfigFactory;
import play.Play;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


public class MyAwsCredentials {

    private AWSCredentials credentials;
    private static final String SUFFIX = "/";

    public MyAwsCredentials() {

        credentials = new BasicAWSCredentials(ConfigFactory.load("aws.conf").getString("env.aws_access_key_id"),
                ConfigFactory.load("aws.conf").getString("env.aws_secret_access_key"));
    }

    public AmazonS3Client getClient() {
        return new AmazonS3Client(credentials);
    }

    public String getBucket() {
        return ConfigFactory.load("aws.conf").getString("env.bucket_name");
    }

    public String getUpFolder(String file_name) {
        return ConfigFactory.load("aws.conf").getString("env.file_upload_folder") + file_name;
    }

    public void createFolder(String bucketName, String folderName, AmazonS3Client client) {
        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                folderName + SUFFIX, emptyContent, metadata);
        // send request to S3 to create folder
        client.putObject(putObjectRequest);
    }

    /**
     * This method first deletes all the files in given folder and then the
     * folder itself
     */
    public void deleteFolder(String bucketName, String folderName, AmazonS3Client client) {
        List<S3ObjectSummary> fileList =
                client.listObjects(bucketName, folderName).getObjectSummaries();
        for (S3ObjectSummary file : fileList) {
            client.deleteObject(bucketName, file.getKey());
        }
        client.deleteObject(bucketName, folderName);
    }
}
