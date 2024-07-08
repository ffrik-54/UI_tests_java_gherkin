package com.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

/**
 * Get email messages
 * 
 * You should enable few options to allow connection with secured mailer (like
 * Gmail): Allow less-secure application to access to your Gmail account Allow
 * pop3 protocol for every mails. Sometimes, you have to disable your antivirus
 * software to allow connection.
 * 
 * @author ffrik
 */
public class Mailer {

	private Mailer() {
	}

}