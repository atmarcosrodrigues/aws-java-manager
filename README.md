# AWS Java Manager

O SDK do AWs para o Java fornece uma API para os serviços da infraestrutura do AWS. Usando o SDK, você pode construir aplicações sobre o Amazon Simple Storage Service (Amazon S3), Amazon Elastic Compute Cloud (Amazon EC2), Amazon SimpleDB, e mais. Esse documento visa detalhar o processo de instalação do ambiente e demonstrar testes práticos de utilização, em especial dos serviços EC2 e S3.

## Instalação do ambiente

### Requisitos iniciais:

    • Possuir um conta na AWS;
    • Possuir o java instalado (o aws sdk necessita do JDK 5.0 ou superior http://developers.sun.com/downloads/ );
    • Possuir as credenciais de segurança  que são usadas para autenticar as requisições a um serviço e identificar-se como remetente da requisição (Access Key ID, Secret Access Key).  Essas informações podem ser obtidas em: http://aws.amazon.com/security-credentials;
    • É possível utilizar os exemplos do aws sdk sem nenhuma IDE específica, mas para maior facilidade utilizaremos o plugin aws java para eclipse (O plugin requer eclipse 3.6 ou superior https://www.eclipse.org/downloads/).
#### Usando o SDK do AWS para Java

Baixe o SDK do AWS para o Java no endereço http://aws.amazon.com/sdkforjava. Depois de ter baixado o arquivo, extraia o conteúdo em uma pasta de seu disco rígido.
A pasta /samples do SDK inclui vários exemplos de código:
    • Exemplo de linha de comando — Demonstra como criar uma requisição para múltiplos serviços.
    • Exemplo com o Amazon S3 — Demonstra como usar os recursos básicos do Amazon S3, como por e obter um objeto do Amazon S3.
    • Exemplo com o Amazon SimpleDB — Demonstra como usar os recursos básico do Amazon SimpleDB, incluindo a criação e remoção de domínios, e seleção de dados em um domínio.
    • Exemplo com o Amazon SQS — Demonstra como usar os recursos básicos do Amazon SQS, incluindo o adicionamento e a retirada de mensagens de uma fila.

#### Instalando o plugin para eclipse

1. Com o eclipse aberto vá no menu Help → Install New Software….
2. Digite http://aws.amazon.com/eclipse na caixa de texto rotulada “Work with”;
3. Selecione “AWS Toolkit for Eclipse” na lista;
4. Clique em “Next”. O Eclipse o direciona pelas etapas de instalação restantes.

#### Configurar o toolkit com suas credenciais de segurança

6.   Abra Window → Preferences;
7.   Escolha a opção “AWS Toolkit”;
8.   Preencha os campos “Access Key ID” e  “Secret Access Key” com os dados credenciais obtidos no site da amazon.
Configurar o SDK

Ao criar um aws project, o eclipse atualiza o sdk automaticamente, caso queira agilizar o processo de atualização, configure o sdk baixado no http://aws.amazon.com/sdkforjava:
9.   Extraia o pacote aws sdk no diretorio desejado;
10.   Abra Window → Preferences;
11.   Escolha a opção “AWS Toolkit” → “AWS SDK For java”
12.   No campo SDK directory insira o caminho do diretorio em que está o aws sdk.
Criando um novo projeto

13.   Crie um novo projeto Java for AWS pela seleção de File -> New -> Project. O assistente The New Project é aberto.
14.   Expanda a categoria AWS, e em seguida selecione AWS Java Project.
15.   Clique em Next. As configurações do projeto serão exibidas. (Pode ser necessário aguardar a atualização do sdk)
16.   Informe um nome para o projeto no campo Project Name. 
17.   Selecione o item correspondente as suas credenciais da amazon conforme foi configurado nos passos 6-8 (O nome padrão das configurações credenciais é: ‘default’).
18.   A seçãoAWS SDK for Java Samples exibe os exemplos disponíveis no SDK. Selecione os exemplos que você quer incluir em seu projeto pela seleção de cada opção dispnível.
19.   Clique em Finish. O projeto será criado e adicionado ao Project explorer.
Ao iniciar um novo projeto é criado automaticamente um arquivo AwsCredencials.properties que guarda as chaves credencias que são chamadas no código para criar as classes de serviços como o AmazonEC2 ou o AmazonS3.

#### Exemplos práticos
#####  Utilizando o aws for java para manipulação de instâncias EC2
Os site: http://www.kpbird.com/2013/09/aws-sdk-for-java-tutorial-2-ec2.html contém um tutorial com diversas opções de uso dos serviços do EC2 como, criação de Tags para a instância, configurações de acesso aos serviços (ssh, HTTP, HTTPS) entre outros. Os exemplos abaixo apresentam trechos de códigos essenciais para criação/manipulação das instâncias.

##### Iniciando um serviço EC2 e criando um security Group
Antes de utilizar um serviço do EC2 deve-se criar um objeto AmazonEC2. A chamada ClasspathPropertiesFileCredentialsProvider() autentica o objeto AmazonEC2 com as chaves configuradas no arquivo: AwsCredencials.properties. No setRegion(..) você seta a localidade do seu serviço EC2. Na primeira execução do serviço, é necessário criar um novo security group, caso não possua um. 

```
private AmazonEC2 ec2;
...
public static void init(){
		// Cria o objeto AmazonEC2Client 
		ec2 = new AmazonEC2Client(new ClasspathPropertiesFileCredentialsProvider());
		
//Caso nao tenha configurado as keys credenciais nas //preferencias do eclipse basta fazer:
//String accessKey = "SUA ACCESS KEY";
//String secretKey = "SUA SECRET KEY" ;

//AWSCredentials credentials  = new
// BasicAWSCredentials(accessKey, secretKey);
//ec2 = new AmazonEC2Client(credentials);

		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		ec2.setRegion(usWest2);
}

String secGroupName = "JavaStartedGroup";
String secGroupDesc = "Java Started Security Group";
...
public static void createGroup(){
		try {
			CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest(secGroupName, secGroupDesc);
			ec2.createSecurityGroup(securityGroupRequest);
		} catch (AmazonServiceException ase) {
			System.out.println(ase.getMessage());		
}
}

```

##### Criando um keypair

Também é necessário criar uma chave criptografada para o “login” no serviço EC2. 
String keyName = “javatestkeypair”;

```
...
private static void createKeyPair(){
		 try {
			 String keyName = "javatstkeypair";
		  CreateKeyPairRequest ckpr = new CreateKeyPairRequest();
		  ckpr.withKeyName(keyName);
		  
		  CreateKeyPairResult ckpresult = ec2.createKeyPair(ckpr);
		  KeyPair keypair = ckpresult.getKeyPair();
		  String privateKey = keypair.getKeyMaterial();
		  System.out.println("KeyPair :" + privateKey);
		 } catch (Exception e) {
		  e.printStackTrace();
		  System.exit(0);
		 }
	}

```

##### Criando uma nova instância (onDemand)
Nesse exemplo será apresentado apenas a criação de instâcias on Demand. No tutorial destacado no início do tópico há um exemplo detalhado da criação de instâncias Spots.
Para criar uma nova instância, cria-se um novo objeto RunInstancesRequest e adiciona os atributos necessários como no exemplo abaixo. É importante lembrar de inserir um nome de securityGroup e uma kayPair válidos, como os criado nos passos anteriores. O atributo InstaceType define o tipo da instância desejada. Deve-se inserir um identificador válido de instância disponibilizado pela amazon (No nosso exemplo criamos uma instância t1.micro). A capacidade de armazenamento e memória da sua instância depende do tipo de instância escolhida: um instância t1.micro por exemplo, possui 0,615 GB de memória, 1 vCPU e armazenamento EBS. Você pode conferir detalhadamente o tipo de instância que atenderá suas necessidades em:
http://aws.amazon.com/pt/ec2/instance-types/
O item ImageId refere-se ao tipo de imagem computacional que será utilizado na instância. Essa imagem inclui sistema operacional, pacotes, ferramentas de configuração de execução e muitas bibliotecas e ferramentas populares da AWS. No site http://aws.amazon.com/pt/amazon-linux-ami/ é possível conferir os diversos tipos de Amazon Linux AMI disponíveis. Nosso exemplo estamos criando uma imagem ami-b8f69f88. No site https://aws.amazon.com/marketplace/ref=csl_ec2_ami encontram-se várias amis disponívies para antender ao seu tipo de aplicação e plataforma de suporte.

```
private static void createInstance() {
		RunInstancesRequest runInstancesRequest = 
				new RunInstancesRequest();
		runInstancesRequest.withImageId("ami-b8f69f88") //AMI id
		.withInstanceType("t1.micro") //tipo da instância
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName("javatestkeypair") //chave keypair
		.withSecurityGroups("JavaStartedGroup"); //security gropup
		//inicia a instância
		RunInstancesResult runInstancesResult = 
				ec2.runInstances(runInstancesRequest);
		System.out.println(runInstancesRequest.toString());
		System.out.println("Instance created");
	}

```
##### Visualizar o id das instâncias existentes

O método abaixo busca todas as instâncias no seu serviço EC2 e exibe seus ids.
	
```
	public static void getInstacesID(){
		  /// Find newly created instance id
		  String instanceId=null;
		  DescribeInstancesResult result = ec2.describeInstances();
		  Iterator<Reservation> i = result.getReservations().iterator();
		  while (i.hasNext()) {
		   Reservation r = i.next();
		   List<Instance> instances = r.getInstances();
		   for (Instance ii : instances) {
		    System.out.println(ii.getImageId() + "t" + ii.getInstanceId()+ "t" + ii.getState().getName() + "t"+ ii.getPrivateDnsName());
		    if (ii.getState().getName().equals("pending")) {
		     instanceId = ii.getInstanceId();
		    }
		   }
		  }
		
	}

```

##### Iniciando uma instância existente
O método abaixo exemplifica a iniciação da instância onde o id é dado como parâmetro.

```
	private static void start(String instanceId) {
		StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(instanceId);
		StartInstancesResult startResult = ec2.startInstances(startRequest);
		System.out.println(startResult.toString());
		System.out.println("Instance started");
	}
```

Um exemplo de execução do código mostra que ao chamar o método, o estado da instância na aws ec2 interface muda para: running

##### Parando uma instância


```
private static void stop(String instanceId) {
		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest();
		stopInstancesRequest.withInstanceIds(instanceId);
		StopInstancesResult stopInstancesResult = ec2.stopInstances(stopInstancesRequest);
		System.out.println(stopInstancesResult.toString());
		System.out.println("Instance stopped");
	}

```

No site https://console.aws.amazon.com/ec2/ é possível verificar a mudança de estado das instâncias.

#### Utilizando o aws for java para armazenamento no S3

Antes de utilizar os serviços deve-se criar um objeto AmazonS3:

```
	private static AmazonS3 s3client;
	...
//private String accessKey = "SUA ACCESS KEY";
	//private String secretKey = "SUA SECRET KEY" ;

	public void init(){
		s3client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		//Caso nao tenha configurado as keys credenciais nas 
		//preferencias do eclipse basta fazer:
		//AWSCredentials credentials  = new
// BasicAWSCredentials(accessKey, secretKey);
		//s3client = new AmazonS3Client(credentials);
		
	}

```
	
##### Criar/Deletar um bucket
	Dica: Os nomes dos buckets no aws S3 são globais, ou seja você deve dar um nome relativamente específico ao seu bucket, caso contrário vai dar conflito com um bucket já criado.
	
```
	String bucketName = "javabucket-antoniotest-1";
    //Cria um novo bucket
	Bucket bucket = s3client.createBucket(bucketName);
	//Deleta um bucket
	s3client.deleteBucket(bucketName);

```

No site: https://console.aws.amazon.com/s3/ você pode conferir os buckets criados.

##### Salvando um novo objeto no S3

```
String bucketName     = "javabucket-antoniotest-1";
String uploadFileName = "file_test.txt";
//Salva um dado objeto no bucket passado como parametro
ByteArrayInputStream input = new ByteArrayInputStream("Hello World aws S3!".getBytes());
s3client.putObject(bucketName, uploadFileName, input, new ObjectMetadata());

Fazendo upload de um arquivo

String bucketName     = "javabucket-antoniotest-1";
String filePath = new File("").getAbsolutePath().toString();
uploadFile(bucketName, "lsd_ic.jpg", filePath+"/lsd_ic.jpg");
...

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

```

##### Ver buckets criados


```
public static void getBuckets(){
		List<Bucket> buckets = s3client.listBuckets();
		for (Bucket bucket : buckets) {
			System.out.println(bucket.getName() + "\t" +
					StringUtils.fromDate(bucket.getCreationDate()));
		}
	}

```

##### Ver arquivos salvos em um bucket


```
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

```

##### Fazer downoad der um arquivo


```
String localPath = “/home/antoniorodrigues/Documentos/lsd_ic.jpg”;
String bucketName     = "javabucket-antoniotest-1";
String fileName = "lsd_ic.jpg";
public void downloadFile(String bucket, String fileName, String localPath) {
		s3client.getObject(
				new GetObjectRequest(bucket, fileName),
				new File(localPath)
				);

	}
```


