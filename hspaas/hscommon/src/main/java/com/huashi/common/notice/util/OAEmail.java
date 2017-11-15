package com.huashi.common.notice.util;


public class OAEmail {

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		InternetAddress[] popAddressList = null;
//
//		String smtpServer = "smtp.163.com";
//		String popServer = "pop.163.com";
//		String SmtpAddress = "bluewens@163.com";
//		String PopAddresslist = "bluewens@126.com";
//		String Subject = "这是一封测试邮件";
//		String Type = "text/html";
//		String messageText = "邮件的内容:hello,world";
//		String[] arrArchievList = new String[4];
//		arrArchievList[0] = "c:\\供应商网址.doc";
//		arrArchievList[1] = "c:\\FLEX3.jpg";
//		arrArchievList[2] = "c:\\FLEX4.jpg";
//		arrArchievList[3] = "c:\\FLEX6.jpg";
//
//		boolean sessionDebug = false;
//		try {
//			java.util.Properties props = System.getProperties();
//
//			props.put("mail.smtp.host", smtpServer);// 存储发送邮件服务器的信息
//			props.put("mail.smtp.auth", "true");// 同时通过验证
//			props.put("mail.transport.protocol", "smtp");
//			Session mailSession = Session.getInstance(props);// 根据属性新建一个邮件会话
//
//			mailSession.setDebug(sessionDebug);
//
//			Message msg = new MimeMessage(mailSession);
//
//			// 设定发件人的地址
//			msg.setFrom(new InternetAddress(SmtpAddress));
//
//			// 设定收信人的地址
//			popAddressList = InternetAddress.parse(PopAddresslist, false);
//			msg.setRecipients(Message.RecipientType.TO, popAddressList);
//
//			// 设定信中的主题
//			msg.setSubject(Subject);
//
//			// 设定送信的时间
//			msg.setSentDate(new Date());
//
//			// 是否以附件方式发送邮件
//			boolean bolSendByArchieve = false;
//
//			// 如果有附件，先将由件内容部分存起来
//			if (arrArchievList != null && arrArchievList.length > 0) {
//				// 1.保存内容
//				MimeMultipart mp = new MimeMultipart();
//				MimeBodyPart mailContentPart = new MimeBodyPart();
//				mailContentPart.setContent(messageText, Type + ";charset=GBK");
//
//				msg.setContent(messageText, Type + ";charset=GBK");
//				// 这句很重要，千万不要忘了
//				mp.setSubType("related");
//
//				mp.addBodyPart(mailContentPart);
//
//				// 2.保存多个附件
//				for (int index = 0; index < arrArchievList.length; index++) {
//					File file = new File(arrArchievList[index]);
//					MimeBodyPart mailArchieve = new MimeBodyPart();
//					
//					FileDataSource fds = new FileDataSource(arrArchievList[index]);
//					mailArchieve.setDataHandler(new DataHandler(fds));
//					mailArchieve.setFileName(MimeUtility.encodeText(fds.getName(), "GBK", "B"));
//					mp.addBodyPart(mailArchieve);
//				}
//
//				// 3.最后集成内容和附件，一起发送
//				msg.setContent(mp);
//			} else {
//				msg.setContent(messageText, Type + ";charset=GBK");
//
//			}
//
//			// 发送邮件
//			Transport transport;
//
//			msg.saveChanges();// 存储邮件信息
//			transport = mailSession.getTransport("smtp");
//			// 以smtp方式登录邮箱
//			// username填写你的发送邮件的用户名如bluewens,userpwd填写你的密码,如获88888888，即transport.connect("smtp.163.com","bluewens","88888888");
//			transport.connect("smtp.163.com", "username", "userpwd");
//
//			transport.sendMessage(msg, msg.getAllRecipients());// 发送邮件,其中第二个参数是所有
//
//			// 已设好的收件人地址
//			props.put("pop.163.com", "false");
//
//			transport.close();
//
//			System.out.println("邮件已发送成功!");
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//	}
}
