package com.utils;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utils for interaction with S3 bucket
 *
 * @author ffrik-54
 */

public class AwsS3Manager {

    static Region region = Region.EU_WEST_1;

    private static final S3Client S3_CLIENT = S3Client.builder().region(region).build();

    private AwsS3Manager() {
    }

    public static S3Client getS3() {
        return S3_CLIENT;
    }

    /**
     * Get Object Bytes from S3.
     *
     * @param S3Client s3Client.
     * @param String   bucketName, bucket name on the s3.
     * @param String   keyName, The key name of the content on the s3.
     * @param String   path, the path to save the s3 content
     * @throws IOException
     **/
    public static void getObjectBytes(S3Client s3Client, String bucketName, String keyName, String path) {

        Logger.getGlobal().log(Level.WARNING, "getObjectBytes -> bucket : {0} with key : {1} on the path {2}",
            new Object[] {bucketName, keyName, path});
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder().key(keyName).bucket(bucketName).build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            // Write the data to a local file.
            File myFile = new File(path);
            try (OutputStream os = new FileOutputStream(myFile)) {
                os.write(data);
                Logger.getGlobal().log(Level.INFO, "Successfully obtained bytes from an S3 object");
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
        } catch (S3Exception e) {
            Logger.getGlobal().log(Level.WARNING, "getObjectBytes error : {0}", e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Get the path of the last file created on the S3.
     *
     * @param S3Client s3Client.
     * @param String   bucket, bucket name on the s3.
     * @param String   path, path of the folder on the s3.
     * @return String of the final path of the file on the s3.
     **/
    public static String getPathLastFileFromS3(S3Client s3Client, String bucket, String path) {
        Instant tempDate = null;
        S3Object recentObj = null;

        List<S3Object> objects = getObjetsFromS3(s3Client, bucket, path);
        for (S3Object os : objects) {

            if (os.size() <= 0) {
                continue;
            }
            Instant modifiedDate = os.lastModified();
            if (tempDate == null || modifiedDate.compareTo(tempDate) > 0) {
                tempDate = modifiedDate;
                recentObj = os;
            }
        }

        String obKey = "";
		if (recentObj != null) {
			obKey = recentObj.key();
		}

        return obKey;
    }

    /**
     * GetList Objets from the S3.
     *
     * @param S3Client s3Client.
     * @param String   bucket, bucket name on the s3.
     * @param String   path, path of the folder on the s3.
     * @return List<S3Object>, list of each objects found on the s3.
     **/
    public static List<S3Object> getObjetsFromS3(S3Client s3Client, String bucket, String path) {

        Logger.getGlobal()
            .log(Level.WARNING, "getObjetsFromS3 -> bucket : {0} on the path : {1}", new Object[] {bucket, path});
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucket).prefix(path).build();
            ListObjectsResponse res = s3Client.listObjects(listObjects);
            return res.contents();

        } catch (S3Exception e) {
            Logger.getGlobal().log(Level.WARNING, "getObjetsFromS3 error : {0}", e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    /**
     * Get the Number of objects on the s3.
     *
     * @param AmazonS3 s3Client.
     * @param String   bucket, bucket name on the s3.
     * @return The number of files found on the S3.
     **/
    public static int getNbFileFromS3(S3Client s3Client, String bucket, String path) {
        Logger.getGlobal()
            .log(Level.WARNING, "getNbFileFromS3 -> bucket : {0} with the path : {1}", new Object[] {bucket, path});
        List<S3Object> s3Obj = getObjetsFromS3(s3Client, bucket, path);
        if (s3Obj != null) {
            return s3Obj.size();
        } else {
            return 0;
        }
    }

    /**
     * Get the content type of an objects on the s3.
     *
     * @param S3Client s3Client.
     * @param String   bucketName, bucket name on the s3.
     * @param String   keyName, the key name.
     * @return the type of the object.
     **/
    public static String getContentType(S3Client s3Client, String bucketName, String keyName) {

        Logger.getGlobal()
            .log(Level.WARNING, "getContentType -> bucket : {0} with key : {1}", new Object[] {bucketName, keyName});
        try {
            HeadObjectRequest objectRequest = HeadObjectRequest.builder().key(keyName).bucket(bucketName).build();

            HeadObjectResponse objectHead = s3Client.headObject(objectRequest);
            return objectHead.contentType();

        } catch (S3Exception e) {
            Logger.getGlobal().log(Level.WARNING, "getContentType error : {0}", e.awsErrorDetails().errorMessage());
            return null;
        }
    }
}