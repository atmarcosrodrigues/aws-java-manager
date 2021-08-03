package com.aws.examples;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;


public class EC2Examples {
	private AmazonEC2 ec2;
	//altere os campos abaixo
	private String keyName = "keyPair";
	private String ami = "ami-b8f69f88";
	private String tipoInstancia = "t1.micro";
	private String keyPair = "keyPair";
	private String securityGroup = "projsecurityGroup";
	private String secGroupName = "projsecurityGroup";
	private String secGroupDesc = "AWSproj securityGroup";
	private String instaceID = "valid-instance-id";

	private String secretKey= "BQXJonajP53U6stR4Ihy/6H6PBm/CR7/IEdowrYp";
	private String 	accessKey="AKIAIY6GAT3LLLGL7PQQ";


	/**
	 * Metodo inicial que cria o serviço EC2
	 */
	public void init(){	
		//ec2 = new AmazonEC2Client(new ClasspathPropertiesFileCredentialsProvider());

		//Caso nao tenha configurado as keys credenciais nas 
		//preferencias do eclipse basta fazer:
		AWSCredentials credentials  = new BasicAWSCredentials(accessKey, secretKey);
		ec2 = new AmazonEC2Client(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		ec2.setRegion(usWest2);
	}

	/**
	 * Medodo que cria um keypair
	 */
	public void createKeyPair(){
		try {
			//String keyName = keyName;
			CreateKeyPairRequest ckpr = new CreateKeyPairRequest();
			ckpr.withKeyName(keyName);

			CreateKeyPairResult ckpresult = ec2.createKeyPair(ckpr);
			KeyPair keypair = ckpresult.getKeyPair();
			String privateKey = keypair.getKeyMaterial();
			System.out.println("KeyPair :" + privateKey);

			File file = new File("/home/"+ keyName+".pem");

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(privateKey);
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Método para criação do cliente e security group
	 */

	private String sshIpRange = "179.185.38.250/32";
	private String sshprotocol = "tcp";
	private int sshFromPort = 22;
	private int sshToPort =22;

	private String httpIpRange = "0.0.0.0/0";
	private String httpProtocol = "tcp";
	private int httpFromPort = 80;
	private int httpToPort = 80;

	private String httpsIpRange = "0.0.0.0/0";
	private String httpsProtocol = "tcp";
	private int httpsFromPort = 443;
	private int httpsToProtocol = 443;
	private void createEC2SecurityGroup(){
		//String groupName = "SecurityGroupAMFR_EC2";
		//String groupDescription = "AMFR EC2 Security Group";



		try {
			System.out.println("Create Security Group Request");   
			CreateSecurityGroupRequest createSecurityGroupRequest =  new CreateSecurityGroupRequest();
			createSecurityGroupRequest.withGroupName(secGroupName)
			.withDescription(secGroupDesc);
			CreateSecurityGroupResult csgr = ec2.createSecurityGroup(createSecurityGroupRequest);

			String groupid = csgr.getGroupId();
			System.out.println("Security Group Id : " + groupid);

			System.out.println("Create Security Group Permission");
			Collection<IpPermission> ips = new ArrayList<IpPermission>();
			// Permission for SSH only to your ip
			IpPermission ipssh = new IpPermission();
			ipssh.withIpRanges(sshIpRange).withIpProtocol(sshprotocol)
			.withFromPort(sshFromPort).withToPort(sshToPort);
			ips.add(ipssh);

			// Permission for HTTP, any one can access
			IpPermission iphttp = new IpPermission();
			iphttp.withIpRanges(httpIpRange).withIpProtocol(httpProtocol)
			.withFromPort(httpFromPort).withToPort(httpToPort);
			ips.add(iphttp);

			//Permission for HTTPS, any one can accesss
			IpPermission iphttps = new IpPermission();
			iphttps.withIpRanges(httpsIpRange).withIpProtocol(httpsProtocol)
			.withFromPort(httpsFromPort).withToPort(httpsToProtocol);
			ips.add(iphttps);

			System.out.println("Attach Owner to security group");
			// Register this security group with owner
			AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest();
			authorizeSecurityGroupIngressRequest
			.withGroupName(secGroupName).withIpPermissions(ips);
			ec2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}


	/**
	 * Método para criar uma nova instancia onDemand
	 * @param instanceId
	 */
	public void createInstance() {
		RunInstancesRequest runInstancesRequest = 
				new RunInstancesRequest();
		runInstancesRequest.withImageId(ami)
		.withInstanceType(tipoInstancia)
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName(keyPair)
		.withSecurityGroups(securityGroup);
		//start Instance
		RunInstancesResult runInstancesResult = 
				ec2.runInstances(runInstancesRequest);
		System.out.println(runInstancesResult.toString());
		System.out.println("Instance created");
	}

	/**
	 * Método para visualizar o id das instancias criadas
	 * @param instanceId
	 */
	public void getInstacesID(){
		/// Find newly created instance id
		String instanceId=null;
		DescribeInstancesResult result = ec2.describeInstances();
		Iterator<Reservation> i = result.getReservations().iterator();
		while (i.hasNext()) {
			Reservation r = i.next();
			List<Instance> instances = r.getInstances();
			for (Instance ii : instances) {
				System.out.println(ii.getImageId() + "  " + ii.getInstanceId()+ "  " + ii.getPublicDnsName() + " - "+ ii.getPrivateDnsName());
				if (ii.getState().getName().equals("pending")) {
					instanceId = ii.getInstanceId();
				}
			}
		}

	}

	/**
	 * Método para iniciar uma instancia ja criada na conta
	 * @param instanceId
	 */
	public void start(String instanceId) {
		StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(instanceId);
		StartInstancesResult startResult = ec2.startInstances(startRequest);
		System.out.println(startResult.toString());
		System.out.println("Instance started");
	}

	/**
	 * Método para parar uma instancia
	 * @param instanceId
	 */
	public void stop(String instanceId) {
		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest();
		stopInstancesRequest.withInstanceIds(instanceId);
		StopInstancesResult stopInstancesResult = ec2.stopInstances(stopInstancesRequest);
		System.out.println(stopInstancesResult.toString());
		System.out.println("Instance stopped");
	}

	public void main() {
		try{
			System.out.println("===========================================");
			System.out.println(" Example EC2 AWS Java SDK!");
			System.out.println("===========================================");

			//Escolha abaixo os metodos necessarios
			init();
			//createEC2SecurityGroup();
			//createKeyPair();
			createInstance();
			//start("i-d982d6d0");
			//stop(instaceID);
			//System.out.println("My instances:");
			//getInstacesID();


		}catch (Exception e) {
			System.out.println("Erro:");
			System.out.println(e.getMessage());
		}

	}

}
