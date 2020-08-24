package edu.utah.ece.async.sboldesigner.sbol;

import netscape.javascript.JSObject;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class SBOLWriter extends org.sbolstandard.core2.SBOLWriter {
    SBOLWriter() {
    }

    public static void newWrite(SBOLDocument doc, File file) throws IOException, SBOLConversionException {
        System.out.println("New Writer");
        org.sbolstandard.core2.SBOLWriter.write(doc, file);

    }
}
