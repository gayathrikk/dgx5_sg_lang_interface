package com.test.DockerStatusTest;

import com.jcraft.jsch.*;

import java.io.*;

import org.testng.annotations.Test;

public class Docker_Storage {
	 @Test
	    public void testStorageDetails() {
	        // Set up SSH connection
	        JSch jsch = new JSch();
	        Session session = null;
	        try {
	            // Replace these with your SSH server details
	            String user = "hbp";
	            String host = "ap6.humanbrain.in";
	            String password = "Health#123";
	            int port = 22;
	            
	            // Establish SSH session
	            session = jsch.getSession(user, host, port);
	            session.setPassword(password);
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.connect();
	            
	            // Execute command on SSH server
	            Channel channel = session.openChannel("exec");
	            ((ChannelExec) channel).setCommand(" df -h /store/repos1"); // Command to retrieve storage details for /dev/mapper devices
	            channel.setInputStream(null);
	            ((ChannelExec) channel).setErrStream(System.err);

	            // Get output
	            InputStream in = channel.getInputStream();
	            channel.connect();
	            byte[] tmp = new byte[1024];
	            StringBuilder output = new StringBuilder();
	            while (true) {
	                while (in.available() > 0) {
	                    int i = in.read(tmp, 0, 1024);
	                    if (i < 0) break;
	                    output.append(new String(tmp, 0, i));
	                }
	                if (channel.isClosed()) {
	                    if (in.available() > 0) continue;
	                    System.out.println("Exit status: " + channel.getExitStatus());
	                    break;
	                }
	                try {
	                    Thread.sleep(1000);
	                } catch (Exception ee) {
	                }
	            }
	            
	            // Parse and format output as table
	            String[] lines = output.toString().split("\n");
	            System.out.println("+----------------------------------+------+------+-------+--------+---------------+");
	            System.out.println("|       Filesystem                 | Size | Used | Avail |  Use%  | Mounted on    |");
	            System.out.println("+----------------------------------+------+------+-------+--------+---------------+");
	            for (int i = 1; i < lines.length; i++) {
	                String[] parts = lines[i].trim().split("\\s+");
	                System.out.printf("| %16s | %4s | %4s | %5s | %6s | %10s |\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
	            }
	            System.out.println("+----------------------------------+------+------+-------+--------+---------------+");
	            channel.disconnect();
	            session.disconnect();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}
