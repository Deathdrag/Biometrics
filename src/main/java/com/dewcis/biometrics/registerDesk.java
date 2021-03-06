package com.dewcis.biometrics;

import java.sql.Connection;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.JDesktopPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

import org.json.JSONObject;
import org.json.JSONArray;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Map;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import javax.swing.border.LineBorder;

public class registerDesk implements ActionListener {
	Logger log = Logger.getLogger(enrollDesk.class.getName());

	Connection db = null;
	Device dev = new Device();
	
	JFrame eFrame;
	JDialog eDialog;
	JSONObject jStudent;
	JPanel mainPanel, detailPanel, buttonPanel, fpPanel, camPanel, statusPanel;
	List<JButton> btns;
	List<JLabel> lbls;
    List<JLabel> msg;
	List<JTextField> txfs;
	List<JLabel> lblPhoto;
	List<JDesktopPane> dsk;

	String sessionId = "";
	String scan1Details = "";
	String scan2Details = "";

	JSONObject jfinger = new JSONObject();
	JSONArray jarrayFinger = new JSONArray();
	JSONObject jfingerItem = new JSONObject();
	JSONObject jfingerItem2 = new JSONObject();

	base64Decoder myImage = new base64Decoder();
	

	public registerDesk(Vector<String> titles, Vector<String> rowData, String sessionId) {
		this.sessionId = sessionId;

		mainPanel = new JPanel(null);
		
		// Fields panel with fields
		detailPanel = new JPanel(null);
		detailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Details"));
		detailPanel.setBounds(5, 5, 800, 100);
		mainPanel.add(detailPanel);
		
		addField(titles.get(0), rowData.get(0), 10, 10, 120, 20, 200);
		addField(titles.get(1), rowData.get(1), 400, 10, 120, 20, 200);
		addField(titles.get(2), rowData.get(2), 10, 30, 120, 20, 200);
		addField(titles.get(3), rowData.get(3), 400, 30, 120, 20, 200);
		addField(titles.get(4), rowData.get(4), 10, 50, 120, 20, 200);

		addJstudent(rowData);

		// Butons panel
		buttonPanel = new JPanel(null);
		buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Functions"));
		buttonPanel.setBounds(5, 450, 850, 100);
		mainPanel.add(buttonPanel);
		
		btns = new ArrayList<JButton>();
		addButton("Update", 10, 20, 100, 25, true);
		addButton("Scan 1", 130, 20, 75, 25, false);
		addButton("Scan 2", 230, 20, 75, 25, false);
		addButton("Enroll", 330, 20, 75, 25, false);
		addButton("Open Camera", 430, 20, 120, 25, false);
		addButton("Take Photo", 580, 20, 120, 25, false);
		addButton("Inactivate", 720, 20, 120, 25, false);
		addButton("Close", 720, 60, 120, 25, true);
		
		// Fingerprint panel
		txfs = new ArrayList<JTextField>();
		lbls = new ArrayList<JLabel>();
		fpPanel = new JPanel(null);
		fpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Finger Prints"));
		fpPanel.setBounds(5, 130, 400, 300);
		mainPanel.add(fpPanel);

		ImageIcon image1 = new ImageIcon(""+myImage.results+"/finger print images/"+jStudent.getString("user_id")+"T1"+".PNG");
        ImageIcon image2 = new ImageIcon(""+myImage.results+"/finger print images/"+jStudent.getString("user_id")+"T2"+".PNG");
		Image fimage1 = image1.getImage();
        Image fimage2 = image2.getImage();
		Image fnewimg1 = fimage1.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
        Image fnewimg2 = fimage2.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
		image1 = new ImageIcon(fnewimg1);
        image2 = new ImageIcon(fnewimg2);

		addDevice("Device ID ", "541612052", 10, 20, 100, 20, 200);
		addFinger(image1,10,80,180,200);
		addFinger(image2,200,80,180,200);

		// Camera panel
		lblPhoto = new ArrayList<JLabel>();
		dsk = new ArrayList<JDesktopPane>();
		camPanel = new JPanel(null);
		camPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Photo"));
		camPanel.setBounds(425, 130, 350, 300);
		mainPanel.add(camPanel);

		ImageIcon pImage = new ImageIcon(""+myImage.results+"/user photo images/"+jStudent.getString("user_id")+".PNG");
		Image pimage1 = pImage.getImage();
		Image pnewimg1 = pimage1.getScaledInstance(330,240,  Image.SCALE_SMOOTH);
		pImage = new ImageIcon(pnewimg1);

		addDesktop(10,30,330, 240);
		addPhoto(pImage,10,30,330, 240);


		// Status panel
        msg = new ArrayList<JLabel>();
		statusPanel = new JPanel(null);
		statusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Status"));
		statusPanel.setBounds(5, 580, 850, 70);
		mainPanel.add(statusPanel);
                
        addMessage("Message", 10, 10, 120, 20, 600);
		
		// Load on main form
		eFrame = new JFrame("Enroll");
		eDialog = new JDialog(eFrame , "Enroll User", true);
		eDialog.setSize(900, 700);
		eDialog.getContentPane().add(mainPanel, BorderLayout.CENTER);
		eDialog.setVisible(true);
	}
	
	public void addField(String fieldTitle, String fieldValue, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		detailPanel.add(lbTitle);
		
		JLabel lbValue = new JLabel(fieldValue);
		lbValue.setBounds(x + w + 10, y, dw, h);
		detailPanel.add(lbValue);
	}
        
    public void addMessage(String fieldTitle, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		statusPanel.add(lbTitle);
		
		JLabel lbValue = new JLabel();
		lbValue.setBounds(x + w + 10, y, dw, h);
		statusPanel.add(lbValue);
		msg.add(lbValue);
	}

	public void addFinger(ImageIcon fingerTemplate, int x, int y, int w, int h) {

		JLabel lbFinger = new JLabel();
		lbFinger.setBounds(x, y, w, h);
		lbFinger.setIcon(fingerTemplate);
		lbFinger.setBorder(new LineBorder(Color.black, 3));
		fpPanel.add(lbFinger);
		lbls.add(lbFinger);
	}

	public void addPhoto(ImageIcon photo,int x, int y, int w, int h) {

		JLabel photoView = new JLabel();
		photoView.setBounds(x, y, w, h);
		photoView.setIcon(photo);
		photoView.setBorder(new LineBorder(Color.black, 2));
		camPanel.add(photoView);
		lblPhoto.add(photoView);
	}

	public void addDevice(String fieldTitle, String fieldValue, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		fpPanel.add(lbTitle);
		
		JTextField tfDevice = new JTextField();
		tfDevice.setBounds(x + w + 5, y, dw, h);
		tfDevice.setText(fieldValue);
		fpPanel.add(tfDevice);
		txfs.add(tfDevice);
	}
	
	public void addButton(String btTitle, int x, int y, int w, int h, boolean enabled) {
		
		JButton btn = new JButton(btTitle);
		btn.setBounds(x, y, w, h);
		buttonPanel.add(btn);
		btn.addActionListener(this);
		btn.setEnabled(enabled);
		btns.add(btn);
	}

	public void addDesktop(int x, int y, int w, int h) {
		JDesktopPane desktopCam = new JDesktopPane();
		desktopCam.setBounds(x, y, w, h);
		desktopCam.setBackground(Color.black);
		desktopCam.setVisible(false);
		camPanel.add(desktopCam);
		dsk.add(desktopCam);
	}
	
	public void actionPerformed(ActionEvent ev) {
		if(ev.getActionCommand().equals("Close")) {
			eDialog.dispose();
		}

		if(ev.getActionCommand().equals("Inactivate")) {
            jStudent.remove("status");
            jStudent.put("status", "IN");
            dev.acinUser(jStudent.getString("user_id"),sessionId,jStudent);
            btns.get(6).setEnabled(false);
            
        }

		if(ev.getActionCommand().equals("Update")) {
            msg.get(0).setText("Update");
			btns.get(0).setEnabled(false);
			btns.get(1).setEnabled(true);
			btns.get(2).setEnabled(true);
            btns.get(4).setEnabled(true);
            btns.get(6).setEnabled(true);
		}

		if(ev.getActionCommand().equals("Scan 1")) {

			// fImage1 = new ImageIcon(fNewimg1);

			String finger1Details = dev.scan(txfs.get(0).getText(),sessionId);
            msg.get(0).setText(finger1Details);
                        
			if(finger1Details.contains("Scan quality is low.")){
			    System.out.println("Scan quality is low.");
			    finger1Details=null;
			    // lbls.get(0).setIcon(fImage1);
			}else if(finger1Details.contains("Device is not connected.")){
			    System.out.println("Device is not connected.");
			    finger1Details=null;
			    // lbls.get(0).setIcon(fImage1);
			}else if(finger1Details.contains("Device not found.")){
                System.out.println("Device not found.");
                finger1Details=null;
                // lbls.get(1).setIcon(fImage1);
            }else if(finger1Details.contains("Device Timed Out")){
			    System.out.println("Device Timed Out");
			    finger1Details=null;
			    // lbls.get(0).setIcon(fImage1);
			}else{
				scan1Details = "Scan quality is Good.";

			    JSONObject jFingerScan = new JSONObject(finger1Details);
			    String template0 = jFingerScan.getString("template0");
			    String template1 = template0;
			    
			    jfingerItem.put("is_prepare_for_duress", false);
			    jfingerItem.put("template0", template0);
			    jfingerItem.put("template1", template1);
			    
			    base64Decoder imgFingerPrint = new base64Decoder();
			    imgFingerPrint.decode(jFingerScan.getString("template_image0"),jStudent.getString("user_id")+"T1");
			    
			    ImageIcon imageF1 = new ImageIcon(""+imgFingerPrint.results+"/finger print images/"+jStudent.getString("user_id")+"T1"+".PNG");
			    Image imgF1 = imageF1.getImage();
			    Image newF1 = imgF1.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
			    imageF1 = new ImageIcon(newF1);
			    
			    lbls.get(0).setIcon(imageF1);
			    
			}
		}

		if(ev.getActionCommand().equals("Scan 2")) {

	        // fImage1 = new ImageIcon(fNewimg1);
	        
	        String finger2Details = dev.scan(txfs.get(0).getText(),sessionId);
	        msg.get(0).setText(finger2Details);
                
	        if(finger2Details.contains("Scan quality is low.")){
	            System.out.println("Scan quality is low.");
	            finger2Details=null;
	            // lbls.get(1).setIcon(fImage1);
	        }else if(finger2Details.contains("Device is not connected.")){
	            System.out.println("Device is not connected.");
	            finger2Details=null;
	            // lbls.get(1).setIcon(fImage1);
	        }else if(finger2Details.contains("Device not found.")){
	            System.out.println("Device not found.");
	            finger2Details=null;
	            // lbls.get(1).setIcon(fImage1);
	        }else if(finger2Details.contains("Device Timed Out")){
	            System.out.println("Device Timed Out");
	            finger2Details=null;
	            // lbls.get(1).setIcon(fImage1);
	        }else{
	        	scan2Details = "Scan quality is Good.";

	            JSONObject jFingerScan = new JSONObject(finger2Details);
	            String template0 = (String) jFingerScan.get("template0");
	            String template1 = template0;
	            
	            jfingerItem2.put("is_prepare_for_duress", false);
	            jfingerItem2.put("template0", template0);
	            jfingerItem2.put("template1", template1);
	            
	            base64Decoder imgFingerPrint = new base64Decoder();
	            imgFingerPrint.decode((String) jFingerScan.get("template_image0"),jStudent.getString("user_id")+"T2");
	            
	            ImageIcon imageF2 = new ImageIcon(""+imgFingerPrint.results+"/finger print images/"+jStudent.getString("user_id")+"T2"+".PNG");
	            Image imgF2 = imageF2.getImage();
	            Image newF2 = imgF2.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
	            imageF2 = new ImageIcon(newF2);
	            
	            lbls.get(1).setIcon(imageF2);
	            btns.get(3).setEnabled(true);
	        }
	        
		}

		if(ev.getActionCommand().equals("Enroll")) {

			if(!scan1Details.isEmpty() && !scan2Details.isEmpty()){
				jarrayFinger.put(jfingerItem);
				jarrayFinger.put(jfingerItem2);
				jfinger.put("fingerprint_template_list",jarrayFinger);
				String enResults =dev.enroll(jStudent.getString("user_id"),sessionId,jfinger);
                                
                msg.get(0).setText(enResults);

			}
		}

		if(ev.getActionCommand().equals("Open Camera")){

            Dimension[] nonStandardResolutions = new Dimension[] {WebcamResolution.HD.getSize(),};
            Webcam webcam = Webcam.getDefault();

            if (webcam != null){

            	lblPhoto.get(0).setVisible(false);
            	dsk.get(0).setVisible(true);

	            webcam.setCustomViewSizes(nonStandardResolutions);
	            webcam.setViewSize(WebcamResolution.HD.getSize());
	            webcam.open(true);
                msg.get(0).setText("Webcam Opened");

	            WebcamPanel panel = new WebcamPanel(webcam, false);
	            panel.setPreferredSize(WebcamResolution.QVGA.getSize());
	            panel.setFPSDisplayed(false);
	            panel.setFPSLimited(true);
	            panel.setFPSLimit(20);
	            panel.start();


	            JInternalFrame window = new JInternalFrame();
	            ((javax.swing.plaf.basic.BasicInternalFrameUI)window.getUI()).setNorthPane(null);
	            window.add(panel);
	            window.pack();
	            window.setMaximumSize(WebcamResolution.QVGA.getSize());
	            window.setVisible(true);

	            dsk.get(0).add(window);
	            btns.get(4).setEnabled(false);
	            btns.get(5).setEnabled(true);
            }else{
                JOptionPane.showMessageDialog(null,"No webcam detected");
                msg.get(0).setText("No webcam detected");
            }
        }

        if(ev.getActionCommand().equals("Take Photo")){

            String photoTaken = dev.takePhoto(jStudent.getString("user_id"));

            JSONObject jObject = new JSONObject();
            jObject.put("encoded_File", ""+photoTaken+"");

            if (photoTaken!=null) {
				Webcam webcam = Webcam.getDefault();
				webcam.close();

				dsk.get(0).setVisible(false);

				base64Decoder photoImage = new base64Decoder();
				ImageIcon pImage = new ImageIcon(""+photoImage.results+"/user photo images/"+jStudent.getString("user_id")+".PNG");
				Image imgP = pImage.getImage();
				Image newPImg = imgP.getScaledInstance(330,240,  Image.SCALE_SMOOTH);
				pImage = new ImageIcon(newPImg);

                msg.get(0).setText("Photo Taken Successfully");
				lblPhoto.get(0).setVisible(true);
				lblPhoto.get(0).setIcon(pImage);
            }   
        }
	}

	public void addJstudent(Vector<String> rowData) {

        base_url base = new base_url();
        Map<String, String> mapResults = base.base_url();
        
        jStudent = new JSONObject();
        
        
        jStudent.put("login_id", rowData.get(0));
        jStudent.put("name", rowData.get(1));
        jStudent.put("phone_number", rowData.get(3));
        jStudent.put("email", rowData.get(4));
        jStudent.put("user_id", rowData.get(2));
        jStudent.put("password", "password");
        jStudent.put("pin", "");
        jStudent.put("security_level", "");
        jStudent.put("start_datetime", "2017-01-13T00:00:00.000Z");
        jStudent.put("expiry_datetime", "2030-01-13T23:59:59.000Z");
        jStudent.put("status", "AC");
        
        JSONArray jAccessGroups = new JSONArray();
        JSONObject jAccessGroup = new JSONObject();
        jAccessGroup.put("id", mapResults.get("access_group_id"));
        jAccessGroup.put("included_by_user_group", "Yes");
        jAccessGroup.put("name", mapResults.get("access_group_name"));
        jAccessGroups.put(jAccessGroup);
        
        jStudent.put("access_groups", jAccessGroups);
        
        JSONObject jUserGroup = new JSONObject();
        jUserGroup.put("id", mapResults.get("user_group_id"));
        jUserGroup.put("name", mapResults.get("user_group_name"));
        
        jStudent.put("user_group", jUserGroup);
        
        JSONObject jpermission = new JSONObject();
        jpermission.put("id", "255");
        jpermission.put("name", "User");
        
        
        JSONArray jpermissions = new JSONArray();
        JSONObject jpermissionls = new JSONObject();
        jpermissionls.put("allowed_group_id_list", "[1]");
        jpermissionls.put("module", "CARD");
        jpermissionls.put("read", true);
        jpermissionls.put("write", true);
        jpermissions.put(jpermissionls);
        
        jpermission.put("permissions", jpermissions);
        jStudent.put("permission", jpermission);

	}
    
}
