package cn.edu.zju.acm.onlinejudge.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;

public class EmailService {

	
	public static boolean sendPasswordEmail(UserProfile user, String resetUrl) throws Exception {
		
		EmailTemplate template = ConfigManager.getEmailTemplate("forgotPassword");
		Properties p = new Properties();
		p.setProperty("FIRST_NAME", user.getFirstName());
		p.setProperty("RESET_URL", resetUrl);
		sendEmail(user.getEmail(), template.getTitle(p), template.getContent(p));
		return true;
	}
	public static void sendEmail(String email, String title, String content) throws Exception {
		
		sendEmail(email, title, ConfigManager.getValue("default_reply_to"), content);
	}
	
	public static void sendEmail(String email, String title, String replyTo, String content) throws Exception {
		String smtpUser = ConfigManager.getValue("smtp_user");
		String smtpPassword = ConfigManager.getValue("smtp_password");
		String smtpHost = ConfigManager.getValue("smtp_host");

		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		if (smtpUser != null && smtpUser.length() > 0) {
			props.put("mail.smtp.auth", "true");
		}
		
	    Session sendMailSession = Session.getInstance(props, null);

	    Message newMessage = new MimeMessage(sendMailSession);
	    newMessage.setFrom(new InternetAddress(replyTo));
	    newMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
	    newMessage.setSubject(title);
	    newMessage.setSentDate(new Date());
	    newMessage.setText(content);
	    Transport trans = sendMailSession.getTransport("smtp");
	    
	    if (smtpUser != null && smtpUser.length() > 0) {
	    	trans.connect(smtpHost, smtpUser, smtpPassword);
	    } else {
	    	trans.connect();
	    }
        trans.sendMessage(newMessage, newMessage.getRecipients(javax.mail.Message.RecipientType.TO));
        trans.close();
	}
	
}
