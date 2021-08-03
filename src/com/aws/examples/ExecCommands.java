package com.aws.examples;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecCommands {
	public static void main(String[] args) {

		Process p;
		String cm = "cd; cd ../home/;"+
				" echo \"instalando aws cli\";"+
				" wget https://s3.amazonaws.com/aws-cli/awscli-bundle.zip;"+
				" unzip awscli-bundle.zip;"+
				" sudo ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws";
		String[] cmd = { "/bin/sh", "-c", cm};
		String stdIn = "", stdErr = "", s;
		try {
			//executar ls -la no diretório corrente
			p = Runtime.getRuntime().exec(cmd);
			
			BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(p.getInputStream()));
			
			BufferedReader stdError = new BufferedReader(new
					InputStreamReader(p.getErrorStream()));
			
			// lê o conteudo da saída padrão do comando e guarda em variável
			while ((s = stdInput.readLine()) != null) {
				stdIn += s + "\n";
				}
			
			// lê o conteudo da saída de erro do comando e guarda em variável
			while ((s = stdError.readLine()) != null) {
				stdErr += s + "\n";
				}
			
			// Exibir a saída padrão e de erro na tela
			System.out.println("Saida Padrao: \n" + stdIn);
			System.out.println("Saida Erro: \n" + stdErr);
			
			}
		catch (IOException ex) {
			ex.printStackTrace();
			}
		}
}