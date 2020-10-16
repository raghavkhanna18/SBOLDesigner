package edu.utah.ece.async.sboldesigner.sbol.editor;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import java.applet.Applet;

public abstract class JSObjectWeb extends JSObject{
    JSObjectWeb(){
        super();
    }

    @SuppressWarnings("exports")
    public static JSObject getWindow(Applet applet) throws JSException {
        return netscape.javascript.JSObject.getWindow(applet);
    }
}
