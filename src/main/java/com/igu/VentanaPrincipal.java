package com.igu;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.model.IPCamera;
import com.smarthings.IPCamerasManager;
import com.utils.Util;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VentanaPrincipal extends JFrame {

	private JPanel contentPane;
	
	IPCamerasManager ipCamerasManager;
	List<IPCamera> ipCameras;
	
	private String videoPath;
	private JPanel panelTop;
	private JLabel lblCmaras;
	private JComboBox comboCamaras;
	private JLabel label;
	private JLabel label_2;
	private JPanel panelVideo;
	private JPanel panelDown;
	private JLabel label_1;
	private JButton btVerVideo;
	private JLabel lblNombreDelUsuario;
	private JTextField campoUsuario;
	private JButton btGuardarRostros;
	private JButton btRestablecerNombres;
	
	private final JFXPanel jfxPanel = new JFXPanel(); 
	private JPanel panel_1;
	private JPanel panel_2;
	private JButton btGuardarParaUso;
	
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
		System.load(Util.LOAD_OPENCV_PATH);
		
		this.ipCamerasManager = new IPCamerasManager();
		this.ipCameras = ipCamerasManager.findDevices();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 973, 777);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		
		getPanel();
		
		
	}
	
	private JPanel getPanel(){
		contentPane.setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(getPanelTop(), BorderLayout.NORTH);
		panel.add(getPanelVideo(), BorderLayout.CENTER);
		panel.add(getPanelDown(), BorderLayout.SOUTH);
		
		return panel;
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
	private JPanel getPanelTop() {
		if (panelTop == null) {
			panelTop = new JPanel();
			panelTop.setLayout(new GridLayout(1, 0, 0, 0));
			panelTop.add(getLblCmaras());
			panelTop.add(getComboCamaras());
			panelTop.add(getBtVerVideo());
			panelTop.add(getLabel_1());
			panelTop.add(getLabel());
			panelTop.add(getLabel_2());
		}
		return panelTop;
	}
	private JLabel getLblCmaras() {
		if (lblCmaras == null) {
			lblCmaras = new JLabel("Cámaras: ");
			lblCmaras.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblCmaras;
	}
	public JComboBox getComboCamaras() {
		if (comboCamaras == null) {
			comboCamaras = new JComboBox();
			
			for (IPCamera ipCamera : ipCameras) {
				this.comboCamaras.addItem(ipCamera.getName());
			}
		}
		return comboCamaras;
	}
	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel(" ");
		}
		return label;
	}
	private JLabel getLabel_2() {
		if (label_2 == null) {
			label_2 = new JLabel(" ");
		}
		return label_2;
	}
	private JPanel getPanelVideo() {
		if (panelVideo == null) {
			panelVideo = new JPanel();
			panelVideo.setLayout(new BorderLayout(0, 0));
			
			jfxPanel.setBounds(10, 90, 882, 604);
			panelVideo.add(jfxPanel);
			panelVideo.add(getPanel_1(), BorderLayout.NORTH);
			panelVideo.add(getPanel_2(), BorderLayout.SOUTH);
		}
		return panelVideo;
	}
	private JPanel getPanelDown() {
		if (panelDown == null) {
			panelDown = new JPanel();
			panelDown.setLayout(new GridLayout(1, 0, 0, 0));
			panelDown.add(getLblNombreDelUsuario());
			panelDown.add(getCampoUsuario());
			panelDown.add(getBtGuardarRostros());
			panelDown.add(getBtGuardarParaUso());
			panelDown.add(getBtRestablecerNombres());
		}
		return panelDown;
	}
	private JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel(" ");
		}
		return label_1;
	}
	private JButton getBtVerVideo() {
		if (btVerVideo == null) {
			btVerVideo = new JButton("Ver video");
			btVerVideo.addActionListener(new AccionBotonVerVideo(this));
		}
		return btVerVideo;
	}
	private JLabel getLblNombreDelUsuario() {
		if (lblNombreDelUsuario == null) {
			lblNombreDelUsuario = new JLabel("Nombre del usuario: ");
			lblNombreDelUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblNombreDelUsuario;
	}
	public JTextField getCampoUsuario() {
		if (campoUsuario == null) {
			campoUsuario = new JTextField();
			campoUsuario.setColumns(10);
		}
		return campoUsuario;
	}
	private JButton getBtGuardarRostros() {
		if (btGuardarRostros == null) {
			btGuardarRostros = new JButton("Guardar rostros");
			btGuardarRostros.addActionListener(new AccionBotonGuardarFrames(this));
		}
		return btGuardarRostros;
	}
	private JButton getBtRestablecerNombres() {
		if (btRestablecerNombres == null) {
			btRestablecerNombres = new JButton("Restablecer nombres");
			btRestablecerNombres.addActionListener(new AcctionBotonRestablecerNombres(this));
		}
		return btRestablecerNombres;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
		}
		return panel_1;
	}
	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
		}
		return panel_2;
	}
	private JButton getBtGuardarParaUso() {
		if (btGuardarParaUso == null) {
			btGuardarParaUso = new JButton("Guardar para uso");
			btGuardarParaUso.addActionListener(new AccionBotonGuardarParaUso(this));
		}
		return btGuardarParaUso;
	}
}
