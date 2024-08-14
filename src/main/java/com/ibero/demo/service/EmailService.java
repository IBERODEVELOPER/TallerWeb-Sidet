package com.ibero.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.ibero.demo.util.EmailValuesDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
    JavaMailSender mailSender;
 
    @Autowired
    TemplateEngine templateEngine;
    
	public void sendEmail(EmailValuesDTO dto) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        Context context = new Context();
	        Map<String, Object> model = new HashMap<>();
	        model.put("userName", dto.getUserName());
	        model.put("newPassword", dto.getNewuserpass());
	        context.setVariables(model);
	        String htmlText = templateEngine.process("email-template", context);
	        //para enviar el correo
	        helper.setFrom(dto.getMailFrom());
            helper.setTo(dto.getMailTo());
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText, true);
            mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	} 
}
