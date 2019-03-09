/*
    NotDuplicateVerifier checks for any duplication in the location
 */

package storybook.toolkit.swing.verifier;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import java.util.List;

import storybook.i18n.I18N;
import storybook.ui.MainFrame;
import storybook.model.BookModel;
import org.hibernate.Session;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.entity.Location;

public class NotDuplicateVerifier extends AbstractInputVerifier {

	public NotDuplicateVerifier() {
        super();
	}

	@Override
	public boolean verify(JComponent comp) {
		if (comp instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent) comp;
            BookModel model = mainFrame.getBookModel();
		    Session session = model.beginTransaction();
            LocationDAOImpl dao = new LocationDAOImpl(session);
            List<Location> allLocations = dao.findAll();
            for(Location location : allLocations) {
                if(tc.getText().trim().equals(location)) {
                    return false;
                    // JOptionPane.showMessageDialog(mainFrame, I18N.getMsg("verifier.nonduplicate"));
                    setErrorText(I18N.getMsg("verifier.nonduplicate"));
                    // setErrorText("Duplicate value"));
                }
            }
            return true;
            
            // if (!tc.getText().trim().isEmpty()) {
			// 	return true;
			// }
			// setErrorText(I18N.getMsg("verifier.nonempty"));
			// return false;
		}
		return false;
	}
}
