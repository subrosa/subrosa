package com.subrosagames.subrosa.infrastructure.storage.s3;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Amazon S3 file storage.
 */
public class AmazonS3Storage implements FileStorage {

    @Autowired
    private String amazonAccessKey;

    @Autowired
    private String amazonSecretKey;

    @Autowired
    private String imageBucketName;

    /**
     * test.
     * @return test
     */
    public String test() {
        AmazonS3 s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        s3.putObject(new PutObjectRequest(imageBucketName, amazonAccessKey, new File("/tmp/tmp")));
        return null;
    }

}
