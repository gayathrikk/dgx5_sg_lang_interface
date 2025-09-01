package com.test.DockerStatusTest;

import com.jcraft.jsch.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class sg_lang_interface {

    @Test(priority = 1)
    public void sg_lang_interface_Status() {

        String vmIpAddress = "172.20.23.157";
        String username = "appUser";
        String password = "Brain@123";
        String containerId = "831bb1d2ded4";

        System.out.println("sg_lang_interface Docker ID = " + containerId);

        if (containerId.isEmpty()) {
            System.out.println("Container ID is required.");
            return;
        }

        try {
            JSch jsch = new JSch();
            com.jcraft.jsch.Session session = jsch.getSession(username, vmIpAddress, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // Execute the docker inspect command to check the container's status
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("docker inspect --format='{{.State.Status}}' " + containerId);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            boolean isRunning = false;

            while ((line = reader.readLine()) != null) {
                System.out.println("Docker Status: " + line.trim());
                if (line.trim().equals("running")) {
                    isRunning = true;
                }
            }

            channel.disconnect();
            session.disconnect();

            // If container is not running, send alert
            if (!isRunning) {
                sendEmailAlert("Hi,\n\n🚨 This is sg_lang_interface Docker. I am currently down. Kindly restart the container at your earliest convenience.");
                assert false : "Container is not in the expected state.";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEmailAlert(String messageBody) {
        String from = "automationsoftware25@gmail.com";
        String to = "gayathri@htic.iitm.ac.in";
       // String cc = "";
        String subject = "Docker Container Alert - Apollo2 LiveWeb";
        final String username = "automationsoftware25@gmail.com";
        final String password = "wjzcgaramsqvagxu"; // App-specific password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        jakarta.mail.Session mailSession = jakarta.mail.Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from, "Docker Monitor"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
           // message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            System.out.println("✅ Alert email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}