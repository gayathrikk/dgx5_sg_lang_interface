package com.test.DockerStatusTest;

import com.jcraft.jsch.*;

import org.testng.annotations.Test;

import java.io.BufferedReader;

import java.io.InputStreamReader;

public class IITM_mysql_Docker {
	
	@Test(priority=1)

	public void MySql_DB_Status() {

		String vmIpAddress = "192.168.10.5";

        String username = "hbp";

        String password = "Health#123"; 

        String containerId = "d710fc8d7cb8"; 
        
        System.out.println("IITM Mysql docker is ="+containerId);



        if (containerId.isEmpty()) {

            System.out.println("Container ID is required.");

            return;

        }



        try {

            JSch jsch = new JSch();

            Session session = jsch.getSession(username, vmIpAddress, 22);

            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no"); 

            session.connect();



            // Execute the docker inspect command to check the container's status

            ChannelExec channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand("docker inspect --format='{{.State.Status}}' " + containerId);

            channel.connect();



            // Read the output of the command

            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println(line);

                assert line.equals("running") : "Container is not in the expected state.";

            }



            channel.disconnect();

            session.disconnect();

        } catch (Exception e) {

            e.printStackTrace();

        }

		

	}

}
