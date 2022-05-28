package mz.co.isutc.si.i42;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicConnectionFactory;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;
import jakarta.jms.TopicSubscriber;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.naming.InitialContext;
import javax.swing.JButton;

public class Chatexe extends JFrame implements MessageListener{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tfInput;
	private JTextArea toContent;
	private JButton btnSend;
	
	private TopicConnectionFactory connectionFactory;
	private TopicConnection connection;
	private TopicSession pubSession;
	private TopicSession subSession;
	private TopicPublisher publisher;
	private TopicSubscriber subscriber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chatexe frame = new Chatexe();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void set(TopicSession pubSession, TopicSession subSession,
			TopicConnection connection,
			TopicPublisher publisher, TopicSubscriber subscriber) 
	{
		
		this.pubSession = pubSession;
		this.subSession = subSession;
		this.connection = connection;
		this.publisher = publisher;
		this.subscriber = subscriber;
	}
	
	private void close() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeMessage(String text) {
		try {
			TextMessage message = pubSession.createTextMessage(text);
			publisher.publish(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the frame.
	 */
	public Chatexe() {
		
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		toContent = new JTextArea();
		toContent.setBounds(10, 11, 414, 189);
		contentPane.add(toContent);
		
		tfInput = new JTextField();
		tfInput.setBounds(20, 210, 285, 27);
		contentPane.add(tfInput);
		tfInput.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(327, 210, 97, 27);
		contentPane.add(btnSend);
		
		this.setTitle("A");
		try{
		    InitialContext initContext=new InitialContext();
		    connectionFactory=(TopicConnectionFactory)initContext.lookup("August Special");
		    connection=connectionFactory.createTopicConnection();
		    pubSession=connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		    subSession=connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		    Topic topic=(Topic)initContext.lookup("August");
		    publisher=pubSession.createPublisher(topic);
		    subscriber=subSession.createSubscriber(topic);
		    subscriber.setMessageListener(this);
		    set(pubSession,subSession,connection,publisher,subscriber);
		    connection.start();
		    
		    tfInput.addActionListener(new ActionListener(){
		      
				@Override
				public void actionPerformed(ActionEvent e) {
					String text= tfInput.getText().trim();
					if(!text.equalsIgnoreCase("exit")) {
						DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
						String time=dateFormat.format(new Date());
						writeMessage(getTitle()+ " ["+time+"]: "+text);
						tfInput.setText("");
					}else{
							close();
							System.exit(0);
					}
				}
				
		    });
		    
		    btnSend.addActionListener(new ActionListener(){
			      
				@Override
				public void actionPerformed(ActionEvent e) {
					String text= tfInput.getText().trim();
					if(!text.equalsIgnoreCase("exit")) {
						DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
						String time=dateFormat.format(new Date());
						writeMessage(getTitle()+ " ["+time+"]: "+text);
					}else{
							close();
							System.exit(0);
					}
				}
				
		    });
		}catch(Exception e){
		    e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			
			TextMessage mgs = (TextMessage) message;
			toContent.append(mgs.getText() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
