package com.vttp.day38server.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
    
    @Value("${bucket.access}")
    String bucketAccess;

    @Value("${bucket.secret}")
    String bucketSecret;

    @Bean
    public AmazonS3 getS3Client(){
        BasicAWSCredentials cred = new BasicAWSCredentials(bucketAccess, bucketSecret);

        EndpointConfiguration epConfig = new EndpointConfiguration("sgp1.digitaloceanspaces.com", "sgp1");
        return AmazonS3ClientBuilder.standard().withEndpointConfiguration(epConfig).withCredentials(new AWSStaticCredentialsProvider(cred)).build();
    }
}
