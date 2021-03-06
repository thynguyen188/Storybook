package org.jdesktop.swingx;

/**
 * BeanInfo class for JXButton.
 * 
 * @author Jan Stola
 */
public class JXButtonBeanInfo extends BeanInfoSupport {

    public JXButtonBeanInfo() {
        super(JXButton.class);        
    }
    
    @Override
    protected void initialize() {
        setPreferred(true, "backgroundPainter", "foregroundPainter");
    }
}
