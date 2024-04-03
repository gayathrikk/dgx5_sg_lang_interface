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
	            String host = "apollo2.humanbrain.in";
	            String password = "Health#123";
	            int port = 22;
	            
	            // Establish SSH session
	            session = jsch.getSession(user, host, port);
	            session.setPassword(password);
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.connect();
	            
	            // Execute command on SSH server
	            Channel channel = session.openChannel("exec");
	            ((ChannelExec) channel).setCommand("df -h"); // Command to retrieve storage details
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
	            System.out.println(output.toString());
	            channel.disconnect();
	            session.disconnect();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}
