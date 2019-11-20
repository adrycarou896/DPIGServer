package com.igu;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.model.IPCamera;
import com.smarthings.IPCamerasManager;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.swing.JTextField;

public class VentanaPrincipal extends JFrame {

	private JPanel contentPane;
	private JComboBox comboCamaras;
	private JButton btVerVideo;
	
	private final JFXPanel jfxPanel = new JFXPanel(); 
	
	IPCamerasManager ipCamerasManager;
	List<IPCamera> ipCameras;
	private JLabel lblUsuario;
	private JTextField campoUsuario;
	private JButton btGuardarFrames;
	
	private String videoPath;
	private JButton btRestablecerNombres;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
					frame.setVisible(true);	
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaPrincipal() {
		System.load("C:\\opencv\\build\\java\\x64\\opencv_java400.dll");
		
		this.ipCamerasManager = new IPCamerasManager();
		this.ipCameras = ipCamerasManager.findDevices();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 928, 881);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		
		JPanel panel = getPanel();
		
		
	}
	
	private JPanel getPanel(){
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblCmaras = new JLabel("Cámaras:");
		lblCmaras.setBounds(10, 52, 71, 14);
		panel.add(lblCmaras);
		panel.add(getComboCamaras());
		panel.add(getBtVerVideo());
		jfxPanel.setBounds(10, 90, 882, 604);
		panel.add(jfxPanel);
		panel.add(getLblUsuario());
		panel.add(getCampoUsuario());
		panel.add(getBtGuardarFrames());
		panel.add(getBtRestablecerNombres());
		
		return panel;
	}
	public JComboBox getComboCamaras() {
		if (comboCamaras == null) {
			comboCamaras = new JComboBox();
			comboCamaras.setBounds(69, 48, 149, 22);
			
			for (IPCamera ipCamera : ipCameras) {
				this.comboCamaras.addItem(ipCamera.getName());
			}
		}
		return comboCamaras;
	}
	private JButton getBtVerVideo() {
		if (btVerVideo == null) {
			btVerVideo = new JButton("Ver video");
			btVerVideo.addActionListener(new AccionBotonVerVideo(this));
			btVerVideo.setBounds(228, 48, 91, 23);
		}
		return btVerVideo;
	}
	
	public void createScene(String videoPath){
		this.videoPath = videoPath;
        Platform.runLater(new Runnable() {
             @Override
             public void run() {                 
                File file = new File(videoPath);                                  
                    MediaPlayer oracleVid = new MediaPlayer(                                       
                        new Media(file.toURI().toString())
                    );
                    //se añade video al jfxPanel
                    jfxPanel.setScene(new Scene(new Group(new MediaView(oracleVid))));                    
                    oracleVid.setVolume(0.7);//volumen
                    oracleVid.setCycleCount(MediaPlayer.INDEFINITE);//repite video
                    oracleVid.play();//play video
             }
        });
    }
	
	private JLabel getLblUsuario() {
		if (lblUsuario == null) {
			lblUsuario = new JLabel("Usuario:");
			lblUsuario.setBounds(10, 721, 71, 14);
		}
		return lblUsuario;
	}
	public JTextField getCampoUsuario() {
		if (campoUsuario == null) {
			campoUsuario = new JTextField();
			campoUsuario.setBounds(69, 718, 149, 20);
			campoUsuario.setColumns(10);
		}
		return campoUsuario;
	}
	private JButton getBtGuardarFrames() {
		if (btGuardarFrames == null) {
			btGuardarFrames = new JButton("Guardar frames del video");
			btGuardarFrames.setBounds(273, 717, 273, 23);
			btGuardarFrames.addActionListener(new AccionBotonGuardarFrames(this));
		}
		return btGuardarFrames;
	}
	
	public List<IPCamera> getIPCameras(){
		return this.ipCameras;
	}
	
	public String getCameraVideoURL(IPCamera camera){
		try {
			return ipCamerasManager.getVideoURL(camera.getDeviceId());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void saveFile(String videoURL, String destinationFile){
		try {
			this.ipCamerasManager.saveFile(videoURL, destinationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getVideoPath(){
		return this.videoPath;
	}
	private JButton getBtRestablecerNombres() {
		if (btRestablecerNombres == null) {
			btRestablecerNombres = new JButton("Restablecer nombres");
			btRestablecerNombres.setBounds(10, 769, 177, 23);
			btRestablecerNombres.addActionListener(new AcctionBotonRestablecerNombres(this));
		}
		return btRestablecerNombres;
	}
}
