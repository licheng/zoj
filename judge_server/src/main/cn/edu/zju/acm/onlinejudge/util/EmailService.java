/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

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
        String email = user.getEmail();
        if (email.endsWith("@magicemailhost.com")) {
        	email = user.getOldEmail();
        }
        EmailService.sendEmail(email, template.getTitle(p), template.getContent(p));
        return true;
    }

    public static void sendEmail(String email, String title, String content) throws Exception {

        EmailService.sendEmail(email, title, ConfigManager.getValue("default_reply_to"), content);
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
