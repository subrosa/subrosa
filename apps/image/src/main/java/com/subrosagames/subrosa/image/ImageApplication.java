package com.subrosagames.subrosa.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;
import fm.last.moji.MojiFile;
import fm.last.moji.spring.SpringMojiBean;


@SpringBootApplication
public class ImageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageApplication.class, args);
    }

    @Bean
    public SpringMojiBean moji() {
        SpringMojiBean mojiBean = new SpringMojiBean();
        mojiBean.setAddressesCsv("10.10.10.42:7001");
        mojiBean.setDomain("subrosa");
        return mojiBean;
    }

}

@RestController
class ImageController {

    @Value("${subrosa.files.assetDirectory}")
    private String assetDirectory;

    @Autowired
    private SpringMojiBean moji;

    @RequestMapping(value = "/{name}")
    public ResponseEntity<Resource> get(@PathVariable("name") String name) {
        return get(name, null, null);
    }

    @RequestMapping(value = "/{name}.{ext}")
    public ResponseEntity<Resource> get(
            @PathVariable("name") String name,
            @PathVariable("ext") String ext) {
        return get(name, null, ext);
    }

    @RequestMapping(value = "/{name}/{size}.{ext}")
    public ResponseEntity<Resource> get(
            @PathVariable("name") String name,
            @PathVariable("size") String size,
            @PathVariable("ext") String ext)
    {
        Image image = new Image(assetDirectory, name);

        Resource imageResource = new ImageResource(image);
        HttpHeaders responseHeaders = new HttpHeaders();
        if (ext != null) {
            ImageType typeFromExtension = ImageType.fromExtension(ext);
            responseHeaders.setContentType(MediaType.parseMediaType(typeFromExtension.contentType()));
        } else {
            responseHeaders.setContentType(MediaType.IMAGE_JPEG);
        }
        return new ResponseEntity<>(imageResource, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/mogile/{filename}")
    public ResponseEntity<Resource> getMogile(
            @PathVariable("filename") String filename) throws IOException
    {
        MojiFile file = moji.getFile(filename);
        Resource resource = new MogileResource(file);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/aws/{filename}")
    public ResponseEntity<Resource> getAws(
            @PathVariable("filename") String filename) throws IOException
    {
        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        AmazonS3 s3 = new AmazonS3Client(credentials);
        S3Object s3Object = s3.getObject(new GetObjectRequest("subrosa-games-images", filename));
        try (final Reader reader = new InputStreamReader(s3Object.getObjectContent());
             final Writer writer = new FileWriter("/tmp/" + filename)) {
            CharStreams.copy(reader, writer);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()));
        return new ResponseEntity<>(new AbstractResource() {
            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream("/tmp/" + filename);
            }
        }, responseHeaders, HttpStatus.OK);
    }

}

enum ImageType {
    JPG("image/jpeg", "jpg", "jpeg"),
    PNG("image/png", "png");

    private final String contentType;
    private final String[] extensions;

    private static final Map<String, ImageType> EXTENSION_MAP = new HashMap<>(ImageType.values().length);
    static {
        for (ImageType imageType : ImageType.values()) {
            for (String extension : imageType.extensions) {
                EXTENSION_MAP.put(extension, imageType);
            }
        }
    }

    ImageType(String contentType, String... extensions) {
        this.contentType = contentType;
        this.extensions = extensions;
    }

    public static ImageType fromExtension(String extension) {
        return EXTENSION_MAP.get(extension);
    }

    public String contentType() {
        return contentType;
    }
}

class Image {

    private final String name;
    private final String imageDirectory;

    public Image(String imageDirectory, String name) {
        this.imageDirectory = imageDirectory;
        this.name = name;
    }

    public String getLocation() {
        String relativePath = StringUtils.join(
                File.separator,
                Iterables.toArray(Splitter.fixedLength(2).split(name), String.class));
        return imageDirectory + File.separator + relativePath + File.separator + name;
    }
}

class ImageResource extends AbstractResource {

    private final Image image;

    public ImageResource(Image image) {
        this.image = image;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(image.getLocation());
    }
}

class MogileResource extends AbstractResource {

    private final MojiFile mojiFile;

    public MogileResource(MojiFile mojiFile) {
        this.mojiFile = mojiFile;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mojiFile.getInputStream();
    }
}

class S3Resource extends AbstractResource {

    private final S3Object s3Object;

    public S3Resource(S3Object s3Object) {
        this.s3Object = s3Object;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return s3Object.getObjectContent();
    }
}
