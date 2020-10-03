/*
 * Copyright (c) 2012 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.utah.ece.async.sboldesigner.sbol.editor;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JFrame;
import javax.swing.UIManager;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialLiteTheme;
import org.sbolstandard.core2.SBOLValidationException;

import edu.utah.ece.async.sboldesigner.sbol.editor.io.FileDocumentIO;
import netscape.javascript.JSObject;
import org.webswing.toolkit.api.WebswingUtil;
import org.webswing.toolkit.api.action.WebActionEvent;
import org.webswing.toolkit.api.action.WebActionListener;
import org.webswing.toolkit.api.security.UserEvent;
import org.webswing.toolkit.api.security.WebswingUserListener;

import static edu.utah.ece.async.sboldesigner.sbol.editor.SBOLDesignerStandalone.SBOLFileStringParse;
import static edu.utah.ece.async.sboldesigner.sbol.editor.SBOLDesignerStandalone.getSBOLFileString;


/**
 * The JFrame shown for the standalone SBOLDesigner application
 *
 * @author Michael Zhang
 *
 */
class PingService{

    public String send(String sbolFileString){
        System.out.println("Action Performed Case 1");
        SBOLFileStringParse(sbolFileString);
        return "true";
    }

    public String get(){
        System.out.println("Action Performed Case 2");
        String sbol = getSBOLFileString();
        return sbol;
    }
}


public class SBOLDesignerStandalone extends JFrame {
    private static SBOLDesignerStandalone frame;
	SBOLDesignerPanel panel = null;
    static PingService ping;

	public SBOLDesignerStandalone() throws SBOLValidationException, IOException {
		// reset the path
		Preferences.userRoot().node("path").put("path", "");
		// creates the panel with this frame so title can be set
		panel = new SBOLDesignerPanel(this);
		// Only ask for a URI prefix if the current one is
		// "http://www.dummy.org"
		panel.newPart(SBOLEditorPreferences.INSTANCE.getUserInfo().getURI().toString().equals("http://www.dummy.org/"),
				true);

		setContentPane(panel);
		setLocationRelativeTo(null);
		setSize(1280, 720);
		setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/icon.png")));

		// set behavior for close operation
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					if (panel.confirmSave()) {
						System.exit(0);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) throws SBOLValidationException, IOException {
		setup();

		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		if (args.length > 0 && false) {
            if ("--webswing".equals(args[0])) {
                try {
                    String sbolString = getSBOLStringFromWebSwing();
                    System.out.println(sbolString);
                    try {
                        if(sbolString != null) {
                            String path = "/root/webswing-examples-20.1.3/sandbox/anonym/upload";
                            String fileName = "test.xml";
                            File theDir = new File(path);
                            if (!theDir.exists()){
                                theDir.mkdirs();
                            }
                            File newXMLFile = new File(path + "/"+fileName);
                            FileWriter fw = new FileWriter(newXMLFile);
                            fw.write(sbolString);
                            fw.close();
                            Preferences.userRoot().node("path").put("path", newXMLFile.getPath());
                            frame.panel.openDocument(new FileDocumentIO(false));
                        }
                    } catch (Exception iox) {
                        //do stuff with exception
                        iox.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("In else");
                try {
                    File file = new File(args[0]);
                    Preferences.userRoot().node("path").put("path", file.getPath());
                    frame.panel.openDocument(new FileDocumentIO(false));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		}
	}

	private static void setup() {
		setupLogging();
		setupLookAndFeel();
		setupSynBioHubCertificate();
		try {
            frame = new SBOLDesignerStandalone();
        } catch (Exception e){
            System.out.println(e);
        }
        setupWebSwing();
    }

	private static void setupWebSwing(){
        System.out.println("setup running");
//        WebswingUtil.getWebswingApi().addUserConnectionListener(new WebswingUserListener() {
//
//            @Override
//            public void onPrimaryUserDisconnected(UserEvent evt) {
//                System.out.println("onUserDisconnected(" + evt.getUser() + ")\n");
//
//            }
//
//            @Override
//            public void onPrimaryUserConnected(UserEvent evt) {
//                System.out.println("onUserConnected(" + evt.getUser() + ")\n");
//            }
//
//            @Override
//            public void onMirrorViewDisconnected(UserEvent evt) {
//                System.out.println("onMirrorViewDisconnected(" + evt.getUser() + ")\n");
//
//            }
//
//            @Override
//            public void onMirrorViewConnected(UserEvent evt) {
//                System.out.println("onMirrorViewConnected(" + evt.getUser() + ")\n");
//
//            }
//        });
//
//        WebswingUtil.getWebswingApi().addBrowserActionListener(new WebActionListener() {
//            @Override
//            public void actionPerformed(WebActionEvent actionEvent) {
//                System.out.println("Action Performed");
//                switch (actionEvent.getActionName()) {
//                    case "setSBOLFileString":
//                        System.out.println("Action Performed Case 1");
//                        String sbolFileString = actionEvent.getData();
//                        SBOLFileStringParse(sbolFileString);
//                        break;
//                    case "getSBOLFileString":
//                        System.out.println("Action Performed Case 2");
//                        sendSBOLFileString();
//                        break;
//                }
//            }
//        });
        ping = new PingService();
        JSObject global = JSObject.getWindow(null);
        global.setMember("pingService", ping);
    }



    private static void setupSynBioHubCertificate() {
		try {
			BufferedInputStream is = new BufferedInputStream(
					SBOLDesignerStandalone.class.getResourceAsStream("/letsEncryptCert.cer"));

			X509Certificate ca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);

			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);
			ks.setCertificateEntry(Integer.toString(1), ca);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException
				| IOException e) {
			e.printStackTrace();
		}
	}

	private static void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(new MaterialLookAndFeel(new MaterialLiteTheme()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setupLogging() {
		final InputStream inputStream = SBOLDesignerStandalone.class.getResourceAsStream("/logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final Exception e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}


    public static void SBOLFileStringParse(String resAsString){


	    try {
            System.out.println(resAsString);
            byte[] encoded = resAsString.getBytes();
            byte[] decoded = Base64.getDecoder().decode(encoded);
            String decodedXML = new String(decoded);
            System.out.println(decodedXML);
            try {
                if(decodedXML != null) {
                    String path = "/root/webswing-examples-20.1.3/sandbox/anonym/upload";
                    String fileName = "test.xml";
                    File theDir = new File(path);
                    if (!theDir.exists()){
                        theDir.mkdirs();
                    }
                    File newXMLFile = new File(path + "/"+fileName);
                    FileWriter fw = new FileWriter(newXMLFile);
                    fw.write(decodedXML);
                    fw.close();
                    Preferences.userRoot().node("path").put("path", newXMLFile.getPath());
                    frame.panel.openDocument(new FileDocumentIO(false));
                }
            } catch (Exception iox) {
                //do stuff with exception
                iox.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

     public static String getSBOLFileString() {
	    try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            frame.panel.getEditor().getDesign().getDesign().write(output);
            String outputString =  new String(output.toByteArray(), "UTF-8");
            return outputString;
        } catch (Exception e){
            System.out.println(e);
        }
        return "empty";
    }

    public static String getSBOLStringFromWebSwing(){
        JSObject global = JSObject.getWindow(null);
        Object res = global.eval("test()");
        assert res instanceof String;
        String resAsString = (String) res;
        System.out.println(resAsString);
        byte[] encoded = resAsString.getBytes();
        byte[] decoded = Base64.getDecoder().decode(encoded);
        String decodedXML = new String(decoded);
        return decodedXML;
    }
}
