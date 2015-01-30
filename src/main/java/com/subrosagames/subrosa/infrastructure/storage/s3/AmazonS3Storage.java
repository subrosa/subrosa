package com.subrosagames.subrosa.infrastructure.storage.s3;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.subrosagames.subrosa.bootstrap.AwsIntegration;
import com.subrosagames.subrosa.domain.file.FileStorage;

/**
 * Amazon S3 file storage.
 */
@Component
public class AmazonS3Storage implements FileStorage {

    @Autowired
    private AwsIntegration awsIntegration;

    /**
     * test.
     *
     * @return test
     */
    public String test() {
        String accessKey = awsIntegration.getAccessKey();
        String secretKey = awsIntegration.getSecretKey();
        String imageBucket = awsIntegration.getImageBucket();
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
        s3.createBucket(imageBucket);
        s3.putObject(new PutObjectRequest(imageBucket, accessKey, new File("/tmp/tmp")));
        return null;
    }

}
