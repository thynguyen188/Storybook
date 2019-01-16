/*
 * Created on Mar 3, 2005
 *
 */
package shef.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import shef.ui.ShefUtils;
import storybook.i18n.I18N;

/**
 * Action which inserts a horizontal rule
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLHorizontalRuleAction extends HTMLTextEditAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public HTMLHorizontalRuleAction() {
		super(I18N.getMsg("shef.horizontal_rule"));
		putValue(MNEMONIC_KEY, (int) I18N.getMnemonic("shef.horizontal_rule"));
		putValue(SMALL_ICON, ShefUtils.getIconX16("hrule"));
		putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
	}

	@Override
	protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
		editor.replaceSelection("<hr>");
	}

	@Override
	protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
		HTMLDocument document = (HTMLDocument) editor.getDocument();
		int caret = editor.getCaretPosition();
		Element elem = document.getParagraphElement(caret);

		HTML.Tag tag = HTML.getTag(elem.getName());
		if (elem.getName().equals("p-implied")) {
			tag = HTML.Tag.IMPLIED;
		}

		HTMLEditorKit.InsertHTMLTextAction a
				= new HTMLEditorKit.InsertHTMLTextAction("", "<hr>", tag, HTML.Tag.HR);
		a.actionPerformed(e);
	}
}
