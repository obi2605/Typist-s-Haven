import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.*;
public class Main{
	static JFrame mainFrame;
	static JPanel panel;
	static JPanel panel2;
	static JTextArea settextArea,getTextArea,instruction;
	JButton startButton,resetButton;
	static JLabel label,accuracylabel,speedlabel,sublabel;
	static JComboBox comboBox,timeComboBox;
	static String inputtext;
	static String textString;
	static int typedWords,correctWords=0,i=0;
	String typingtext;
	static long startTime;
	static JProgressBar bar;
	static JScrollPane scrollpanel2;
	static TimerThread timerThread;
	static TextField timeField;
	ImageIcon labelIcon=new ImageIcon("WhatsApp Image 2023-09-28 at 17.11.39.jpg");
	static String jdbcUrl="jdbc:mysql://localhost:3306/aman";
	static String username="root";
	static String password="Asdf0987@#$_";
	static String[] passage=new String[1000];
	static Connection connection;
	Main(){
		try {
			//Connection connection=DriverManager.getConnection(jdbcUrl,username,password);
			Statement stm=connection.createStatement();
			ResultSet rs=stm.executeQuery("SELECT string FROM passage");
			while(rs.next()) {
				passage[i]=rs.getString("string");
				i++;
			}
			passage[i]="custom";
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("connection failed error: "+e.getMessage());
		}
	}
	public void text(){
		mainFrame=new JFrame();
		mainFrame.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//mainFrame.setSize(1000, 609);
		mainFrame.setTitle("typing project");
		startButton=new JButton("new Start");
		startButton.setBounds(5, 5, 210, 50);
		startButton.setFocusable(true);
		ImageIcon imageIcon=new ImageIcon("WhatsApp Image 2023-09-28 at 17.03.57.jpg");
		mainFrame.setIconImage(imageIcon.getImage());
		panel=new JPanel();
		panel2=new JPanel();
		accuracylabel = new JLabel("accuracy: ");
		accuracylabel.setLayout(null);
		speedlabel=new JLabel("speed: ");
		speedlabel.setLayout(null);
		sublabel=new JLabel();
		sublabel.setLayout(null);
		label=new JLabel();	
		label.setLayout(null);
		label.add(startButton);
		panel.setLayout(new GridLayout(3, 5,5,0));
		panel.setPreferredSize(new Dimension(1000,609));
		panel.setBackground(new Color(30,144,255));
		panel2.setLayout(new GridLayout(5, 3,5,5));
		panel2.setPreferredSize(new Dimension(1000,609));
		panel2.setBackground(new Color(220,220,220));
		startButton.setEnabled(false);
		settextArea=new JTextArea();
		settextArea.setEditable(false);
		settextArea.setLineWrap(true);
		settextArea.setWrapStyleWord(false);
		getTextArea=new JTextArea();
		getTextArea.setEditable(false);
		getTextArea.setLineWrap(true);
		getTextArea.setWrapStyleWord(false);
        comboBox=new JComboBox(passage);	
        label.setText("choice the passage.");
        comboBox.setBounds(5, 110, 200, 50);
        label.add(comboBox);
   		mainFrame.add(panel2);
    	resetButton=new JButton("back");
   		resetButton.setBounds(320, 0, 300, 50);
   		resetButton.setFocusable(true);
   		sublabel.add(resetButton);
   		//panel2.add(resetButton);
   		panel2.add(sublabel);
   		panel2.add(accuracylabel);
    	panel2.add(speedlabel); 
        timeField=new TextField();
        timeField.setBounds(250, 110, 150, 30);
        timeField.setFont(new Font("consolas", Font.PLAIN, 25));
        instruction=new JTextArea();
        instruction.setBounds(220, 0, 750, 80);
        instruction.setBackground(new Color(0,0,0));
        instruction.setFont(new Font("consolas", Font.PLAIN, 25));
        instruction.setForeground(Color.green);
        label.add(instruction);
        label.add(timeField);
        timeComboBox=new JComboBox();
        JScrollPane scrollPane=new JScrollPane(settextArea);
        settextArea.setFont(new Font("consolas", Font.PLAIN, 13));
        getTextArea.setFont(new Font("consolas", Font.PLAIN, 13));
        accuracylabel.setFont(new Font("consolas", Font.PLAIN, 30));
        speedlabel.setFont(new Font("consolas", Font.PLAIN, 30));
		scrollPane.setBounds(0, 30, 538, 270);		
		scrollpanel2=new JScrollPane(getTextArea);
		panel.add(label);
		panel.add(scrollPane);
		panel.add(scrollpanel2);
		start();	
		mainFrame.add(panel);
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	public void start() {
		bar=new JProgressBar(0,100);
		bar.setValue(100);
		bar.setStringPainted(true);
		bar.setBounds(0, 178, 1000, 20);
		label.add(bar);
		instruction.setText("choose first passage and time then start typing!");
		typedWords = 0;
	    correctWords = 0;
		panel.setVisible(true);
		panel2.setVisible(false);
		settextArea.setText("");
		timeField.setText("Enter time");
		timerThread = new TimerThread();
		 comboBox.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==comboBox) {
					startButton.setEnabled(true);
					if("custom".equals(comboBox.getSelectedItem())) {
						instruction.setText("Type your own passage-");
						settextArea.setEditable(true);
						JButton doneButton=new JButton("done");
						doneButton.setBounds(500, 110, 150, 30);
						doneButton.setFocusable(true);
						label.add(doneButton);
						doneButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								settextArea.setEditable(false);
								settextArea.setText(settextArea.getText());
								String line=settextArea.getText();
								String sql ="INSERT INTO passage(string) values ('"+line+"')";
							    PreparedStatement ps;
								try {
									ps = connection.prepareStatement(sql);
									ps.executeUpdate();
									passage[i]=line;
									comboBox.insertItemAt(line,i);
									passage[i++]="custom";
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}
						});
					}
					else {
						int index=comboBox.getSelectedIndex();
						settextArea.setText(passage[index]);
					}
				}
			}
		});
		 startButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				textString=settextArea.getText();
				if(startButton.getText().equals("new Start")) {
					timeField.setEnabled(false);
					getTextArea.setEditable(true);
					timerThread.start();
					instruction.setText("Start typing fast. time is running :)");
					setstring();
				}
				if(startButton.getText().equals("Start")) {
					timeField.setEnabled(false);
					getTextArea.setEditable(true);
					instruction.setText("Start typing fast. time is running :)");
					timerThread = new TimerThread();
					timerThread.start();
				}
			}
		});
	}
	public static void setstring() {
		getTextArea.getDocument().addDocumentListener(new DocumentListener() {			
			@Override
			public void removeUpdate(DocumentEvent e) {	
				getstring();
			}			
			@Override
			public void insertUpdate(DocumentEvent e) {
				getstring();
			}			
			@Override
			public void changedUpdate(DocumentEvent e) {
				getstring();
			}
		});  		
	}
	 class TimerThread extends Thread {
	    @Override
	    public void run() {
	    	long timeRemaining= (long)((Float.parseFloat(timeField.getText()))*60000);
	   		startTime=System.currentTimeMillis();
	   		long count=1000;
	   		while(timeRemaining>0) {
    			try {
    				Thread.sleep(count);
    				timeRemaining -= count;
	    		} 
	    		catch (InterruptedException e) {
	   				e.printStackTrace();
	   			}  			
	   		 long remainingSeconds = timeRemaining / 1000;
             bar.setValue((int) ((1 - (double) remainingSeconds / (timeRemaining / 1000)) * 100));
             bar.setString(remainingSeconds + " seconds remaining");
	   		}
    		bar.setString("time up!");
    		end();
    	}		
	}
	 public void end() {
	    panel.setVisible(false);
	    //panel2.add(sublabel);
    	panel2.setVisible(true);   
    	getTextArea.setEditable(false);
		timeField.setEnabled(true);
	    getTextArea.setText("");
	    startButton.setEnabled(false);
   		mainFrame.setVisible(true);
   		resetButton.addActionListener(new ActionListener() {				
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==resetButton) {
					start();
					startButton.setText("Start");
				}				
			}
		});  		
	 }
	public static void getstring() {		
		String inputText = getTextArea.getText();
        String[] inputWords = inputText.split("\\s+");
        String[] passageWords = textString.split("\\s+");
        typedWords = inputWords.length;
        correctWords = 0;
        for (int i = 0; i < Math.min(passageWords.length, typedWords); i++) {
            if (inputWords[i].equals(passageWords[i])) {
                correctWords++;
            }
        }
        int accuracy = (int) (((double) correctWords / typedWords) * 100);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        long elapsedTime = (System.currentTimeMillis()-startTime);       
        double minutes = elapsedTime / 60000.0; // Converting milliseconds to minutes
        double speed = typedWords / minutes; // Speed in words per minute
        String formattedSpeed = decimalFormat.format(speed);
        if(accuracy>0) {
        	accuracylabel.setText("Accuracy: " + accuracy + "%");
        	speedlabel.setText("Speed: " + formattedSpeed + " wpm");
        }
	}
	public static void main(String[] args) {
		try {
			connection=DriverManager.getConnection(jdbcUrl,username,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 SwingUtilities.invokeLater(() -> {
		        Main obj = new Main();
		        obj.text();
		    });
	}
}