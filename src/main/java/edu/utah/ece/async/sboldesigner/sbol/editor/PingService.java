package edu.utah.ece.async.sboldesigner.sbol.editor;

import edu.utah.ece.async.sboldesigner.sbol.editor.io.FileDocumentIO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.prefs.Preferences;

public class PingService {
    SBOLDesignerStandalone frame;
    PingService(SBOLDesignerStandalone frame){
        this.frame = frame;
    }
    public String send(String sbolFileString ){
        System.out.println("Action Performed Case 1");
        SBOLFileStringParse(sbolFileString, this.frame);
        return "true";
    }

    public String get(){
        System.out.println("Action Performed Case 2");
        String sbol = getSBOLFileString(this.frame);
        return sbol;
    }

    @Override
    protected void finalize() throws Throwable {
        while (true) {
            Thread.yield();
        }
    }

    public static void SBOLFileStringParse(String resAsString, SBOLDesignerStandalone frame){


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

    public static String getSBOLFileString(SBOLDesignerStandalone frame) {
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
}
