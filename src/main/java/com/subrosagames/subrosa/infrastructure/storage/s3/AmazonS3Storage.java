package com.subrosagames.subrosa.infrastructure.storage.s3;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.subrosagames.subrosa.domain.file.FileStorage;

/**
 * Amazon S3 file storage.
 */
public class AmazonS3Storage implements FileStorage {

    @Value("${amazon.aws.accessKey}")
    private String amazonAccessKey;

    @Value("${amazon.aws.secretKey}")
    private String amazonSecretKey;

    @Value("${amazon.s3.bucket.image}")
    private String imageBucketName;

    /**
     * test.
     *
     * @return test
     */
    public String test() {
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(amazonAccessKey, amazonSecretKey));
        s3.createBucket(imageBucketName);
        s3.putObject(new PutObjectRequest(imageBucketName, amazonAccessKey, new File("/tmp/tmp")));
        return null;
    }

}
