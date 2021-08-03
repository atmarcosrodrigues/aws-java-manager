package com.aws.examples;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.StringUtils;

public class S3examples {
	private static AmazonS3 s3client;

	//Altere os campos abaixo
	private static String bucketName     = "javabucket-antoniotest-1";
	private static String uploadFileName = "file_test.txt";
	private static String text = "Hello World aws S3!";


	private String accessKey = "SUA ACCESS KEY";
	private String secretKey = "SUA SECRET KEY" ;

	public void init(){
		//s3client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		//Caso nao tenha configurado as keys credenciais nas 
		//preferencias do eclipse basta fazer:
		AWSCredentials credentials  = new BasicAWSCredentials(accessKey, secretKey);
		s3client = new AmazonS3Client(credentials);

	}

	public void main() {

		try {
			System.out.println("\n===========================================");
			System.out.println(" Example S3 AWS Java SDK!");
			System.out.println("===========================================");
			init();
			System.out.println("-Buckets created:");
			getBuckets();

			//Selecione os trechos de codigos necessarios

			//Cria um novo bucket
			//Bucket bucket = s3client.createBucket(bucketName);
			//System.out.println(bucketName+" was created");

			//Deleta um bucket
			//s3client.deleteBucket(bucket.getName());
			//System.out.println(bucketName+" was deleted");

			//Cria um novo objeto no bucket
			//crateObjectBucket();

			//Fazer upload de um arquivo no s3 
			//(exemplo com uma imagem salva no diretorio corrente)
			//System.out.println("\n-Uploading a file");
			//String filePath = new File("").getAbsolutePath().toString();
			//System.out.println(filePath+"/lsd_ic.jpg");
			//uploadFile(bucketName, "lsd_ic.jpg", filePath+"/lsd_ic.jpg");

			//uploadFile(bucketName, fileName, filePath);


			//Ver objetos salvos em um bucket

			System.out.println("\n-Objects in bucket: "+bucketName);
			getObjectsBuckets(bucketName);

			//Download de um arquivo
			//downloadFile(bucket, fileName, localPath);


		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException");
			System.out.println("Error Message:    " + ase.getMessage());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Cria um novo objeto no bucket com o nome e texto definidos
	 */
	public void crateObjectBucket(){
		ByteArrayInputStream input = new ByteArrayInputStream(text.getBytes());
		s3client.putObject(bucketName, uploadFileName, input, new ObjectMetadata());
	}


	/**
	 * Metodo que exibe todos os bucket criados no cliente s3
	 */
	public static void getBuckets(){
		List<Bucket> buckets = s3client.listBuckets();
		for (Bucket bucket : buckets) {
			System.out.println(bucket.getName() + "\t" +
					StringUtils.fromDate(bucket.getCreationDate()));
		}
	}

	/**
	 *  Método que faz upload de um arquivo passando o path do diretorio 
	 *  e o bucket em que ele sera uppado
	 * @param bucket
	 * @param keyName
	 * @param filePath
	 * @throws Exception
	 */
	public static void uploadFile(String bucket, String keyName, String filePath) throws Exception {
		TransferManager tm = new TransferManager(new ClasspathPropertiesFileCredentialsProvider());        

		try {
			Upload upload = tm.upload(
					bucket, keyName, new File(filePath));
			upload.waitForCompletion();
			System.out.println("Upload complete.");
		} catch (AmazonClientException amazonClientException) {
			System.out.println("Unable to upload file, upload was aborted.");
			amazonClientException.printStackTrace();
		}
	}

	/**
	 * Método que mostra todos os objetos salvos em um bucket
	 * @param bucketName
	 */
	public static void getObjectsBuckets(String bucketName) {
		ObjectListing objects = s3client.listObjects(bucketName);
		do {
			for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
				System.out.println(objectSummary.getKey() + "\t" +
						objectSummary.getSize() + "\t" +
						StringUtils.fromDate(objectSummary.getLastModified()));
			}
			objects = s3client.listNextBatchOfObjects(objects);
		} while (objects.isTruncated());
	}

	/**
	 * Método que baixa um arquivo do repositorio  do cliente s3 aws
	 * @param bucket
	 * @param fileName
	 * @param localPath
	 */
	public void downloadFile(String bucket, String fileName, String localPath) {
		s3client.getObject(
				new GetObjectRequest(bucket, fileName),
				new File(localPath)
				);

	}

}
