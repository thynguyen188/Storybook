/*
 * Created on Jan 14, 2006
 *
 */
package shef.ui.text.actions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Enumeration;
//import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import shef.ui.text.dialogs.ImageDialog;

import org.bushe.swing.action.ShouldBeEnabledDelegate;
import shef.ui.text.CompoundUndoManager;
import shef.ui.text.HTMLUtils;
import shef.ui.text.dialogs.ElementStyleDialog;
import shef.ui.text.dialogs.HyperlinkDialog;
import shef.ui.text.dialogs.ListDialog;
import shef.ui.text.dialogs.TablePropertiesDialog;
import storybook.i18n.I18N;

/**
 * Action for editing an element's properties depending on the current caret position.
 *
 * Currently supports links, images, tables, lists, and paragraphs.
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLElementPropertiesAction extends HTMLTextEditAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final int TABLE_PROPS = 0;
	public static final int LIST_PROPS = 1;
	public static final int IMG_PROPS = 2;
	public static final int LINK_PROPS = 3;
	public static final int ELEM_PROPS = 4;

	public static final String PROPS[]
			= {
				I18N.getMsg("shef.table_properties_"),
				I18N.getMsg("shef.list_properties_"),
				I18N.getMsg("shef.image_properties_"),
				I18N.getMsg("shef.hyperlink_properties_"),
				I18N.getMsg("shef.object_properties_")
			};

	public HTMLElementPropertiesAction() {
		super(PROPS[ELEM_PROPS]);
		addShouldBeEnabledDelegate(new ShouldBeEnabledDelegate() {
			@Override
			public boolean shouldBeEnabled(Action a) {
				return getEditMode() != SOURCE && elementAtCaretPosition(getCurrentEditor()) != null;
			}
		});
	}

	//public void actionPerformed(ActionEvent e)
	@Override
	protected void wysiwygEditPerformed(ActionEvent e, JEditorPane ed) {
		Element elem = elementAtCaretPosition(ed);
		int type = getElementType(elem);
		int caret = ed.getCaretPosition();

		switch (type) {
			case LINK_PROPS:
				editLinkProps(elem);
				break;
			case IMG_PROPS:
				editImageProps(elem);
				break;
			case TABLE_PROPS:
				editTableProps(elem);
				break;
			case LIST_PROPS:
				editListProps(elem);
				break;
			case ELEM_PROPS:
				editStyleProps(elem.getParentElement());
				break;
			default:
				break;
		}

		try {
			ed.setCaretPosition(caret);
		} catch (Exception ex) {
		}
	}

	@SuppressWarnings("unchecked")
	private Map getAttribs(Element elem) {
		Map at = new HashMap();

		AttributeSet a = elem.getAttributes();
		for (Enumeration e = a.getAttributeNames(); e.hasMoreElements();) {
			Object n = e.nextElement();
			//dont return the name attribute
			if (n.toString().equals("name") && !elem.getName().equals("a")) {
				continue;
			}
			at.put(n.toString(), a.getAttribute(n).toString());
		}

		return at;
	}

	private String getElementHTML(Element el, Map attribs) {
		String html = "<" + el.getName();
		for (Iterator e = attribs.keySet().iterator(); e.hasNext();) {
			Object name = e.next();
			Object val = attribs.get(name);
			html += " " + name + "=\"" + val + "\"";
		}

		String txt = HTMLUtils.getElementHTML(el, false);
		html += ">\n" + txt + "\n</" + el.getName() + ">";

		return html;
	}

	private Map getLinkAttributes(Element elem) {
		String link = HTMLUtils.getElementHTML(elem, true).trim();
		Map attribs = new HashMap();
		if (link.startsWith("<a")) {
			link = link.substring(0, link.indexOf('>'));
			link = link.substring(link.indexOf(' '), link.length()).trim();

			attribs = HTMLUtils.tagAttribsToMap(link);
		}

		return attribs;
	}

	private void editImageProps(Element elem) {
		ImageDialog d = createImageDialog();

		if (d != null) {
			Map imgAttribs = getAttribs(elem);
			d.setImageAttributes(imgAttribs);
			d.setLocationRelativeTo(d.getParent());
			d.setVisible(true);
			if (!d.hasUserCancelled()) {
				replace(elem, d.getHTML());
			}
		}
	}

	private void editLinkProps(Element elem) {
		HyperlinkDialog d = createLinkDialog();
		if (d != null) {
			d.setAttributes(getLinkAttributes(elem));
			d.setLocationRelativeTo(d.getParent());

			try {
				//get the link text...
				String text = elem.getDocument().getText(
						elem.getStartOffset(),
						elem.getEndOffset() - elem.getStartOffset());
				d.setLinkText(text);
			} catch (BadLocationException ex) {
			}
			d.setVisible(true);
			if (!d.hasUserCancelled()) {
				replace(elem, d.getHTML());
			}
		}
	}

	private void editTableProps(Element paraElem) {
		HTMLDocument doc = null;
		try {
			doc = (HTMLDocument) paraElem.getDocument();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		Element tdElem = HTMLUtils.getParent(paraElem, HTML.Tag.TD);
		Element trElem = HTMLUtils.getParent(paraElem, HTML.Tag.TR);
		Element tableElem = HTMLUtils.getParent(paraElem, HTML.Tag.TABLE);
		TablePropertiesDialog dlg = createTablePropertiesDialog();
		if (dlg == null || tdElem == null || trElem == null || tableElem == null) {
			return; //no dialog or malformed table! Just return...
		}
		dlg.setCellAttributes(getAttribs(tdElem));
		dlg.setRowAttributes(getAttribs(trElem));
		dlg.setTableAttributes(getAttribs(tableElem));
		dlg.setLocationRelativeTo(dlg.getParent());
		dlg.setVisible(true);
		
		if (dlg.doRemoveTable) {
			CompoundUndoManager.beginCompoundEdit(doc);
			doc.removeElement(tableElem);
			CompoundUndoManager.endCompoundEdit(doc);
			return;
		}

		if (!dlg.hasUserCancelled()) {
			CompoundUndoManager.beginCompoundEdit(doc);
			try {
				String html = getElementHTML(tdElem, dlg.getCellAttributes());
				doc.setOuterHTML(tdElem, html);

				html = getElementHTML(trElem, dlg.getRowAttribures());
				doc.setOuterHTML(trElem, html);

				html = getElementHTML(tableElem, dlg.getTableAttributes());
				doc.setOuterHTML(tableElem, html);
			} catch (BadLocationException | IOException ex) {
				ex.printStackTrace();
			}
			CompoundUndoManager.endCompoundEdit(doc);
		}
	}

	private void editListProps(Element elem) {
		elem = HTMLUtils.getListParent(elem);
		if (elem == null) {
			return;
		}
		int type;
		switch (elem.getName()) {
			case "ul":
				type = ListDialog.UNORDERED;
				break;
			case "ol":
				type = ListDialog.ORDERED;
				break;
			default:
				return;
		}

		Map attr = getAttribs(elem);
		ListDialog d = createListDialog();
		if (d == null) {
			return;
		}

		d.setListType(type);
		d.setListAttributes(attr);
		d.setLocationRelativeTo(d.getParent());
		d.setVisible(true);
		if (!d.hasUserCancelled()) {
			attr = d.getListAttributes();
			String html = "";
			if (d.getListType() != type) {
				HTML.Tag tag = HTML.Tag.UL;
				if (d.getListType() == ListDialog.ORDERED) {
					tag = HTML.Tag.OL;
				}
				String txt = HTMLUtils.getElementHTML(elem, false);
				html = "<" + tag;
				for (Iterator ee = attr.keySet().iterator(); ee.hasNext();) {
					Object o = ee.next();
					html += " " + o + "=" + attr.get(o);
				}
				html += ">" + txt + "</" + tag + ">";
			} else {
				html = getElementHTML(elem, attr);
			}

			replace(elem, html);
		}

	}

	private void editStyleProps(Element elem) {
		if (elem.getName().equals("p-implied")) {
			elem = elem.getParentElement();
		}
		Map attr = getAttribs(elem);
		ElementStyleDialog d = createStyleDialog();
		if (d == null) {
			return;
		}

		d.setLocationRelativeTo(d.getParent());
		d.setStyleAttributes(attr);
		d.setVisible(true);

		if (!d.hasUserCancelled()) {
			System.err.println(elem.getName());
			String html = getElementHTML(elem, d.getStyleAttributes());
			System.err.println(html);
			replace(elem, html);
		}
	}

	protected HyperlinkDialog createLinkDialog() {
		Component c = getCurrentEditor();
		HyperlinkDialog d = null;
		if (c != null) {
			Window w = SwingUtilities.getWindowAncestor(c);
			if (w != null && w instanceof Frame) {
				d = new HyperlinkDialog((Frame) w);
			} else if (w != null && w instanceof Dialog) {
				d = new HyperlinkDialog((Dialog) w);
			}
		}

		return d;
	}

	protected ImageDialog createImageDialog() {
		Component c = getCurrentEditor();
		ImageDialog d = null;
		if (c != null) {
			Window w = SwingUtilities.getWindowAncestor(c);
			if (w != null && w instanceof Frame) {
				d = new ImageDialog((Frame) w);
			} else if (w != null && w instanceof Dialog) {
				d = new ImageDialog((Dialog) w);
			}
		}

		return d;
	}

	protected TablePropertiesDialog createTablePropertiesDialog() {
		Component c = getCurrentEditor();
		TablePropertiesDialog d = null;
		if (c != null) {
			Window w = SwingUtilities.getWindowAncestor(c);
			if (w != null && w instanceof Frame) {
				d = new TablePropertiesDialog((Frame) w,this);
			} else if (w != null && w instanceof Dialog) {
				d = new TablePropertiesDialog((Dialog) w,this);
			}
		}

		return d;
	}

	protected ListDialog createListDialog() {
		Component c = getCurrentEditor();
		ListDialog d = null;
		if (c != null) {
			Window w = SwingUtilities.getWindowAncestor(c);
			if (w != null && w instanceof Frame) {
				d = new ListDialog((Frame) w);
			} else if (w != null && w instanceof Dialog) {
				d = new ListDialog((Dialog) w);
			}
		}

		return d;
	}

	protected ElementStyleDialog createStyleDialog() {
		Component c = getCurrentEditor();
		ElementStyleDialog d = null;
		if (c != null) {
			Window w = SwingUtilities.getWindowAncestor(c);
			if (w != null && w instanceof Frame) {
				d = new ElementStyleDialog((Frame) w);
			} else if (w != null && w instanceof Dialog) {
				d = new ElementStyleDialog((Dialog) w);
			}
		}

		return d;
	}

	private void replace(Element elem, String html) {
		HTMLDocument document = null;
		try {
			document = (HTMLDocument) elem.getDocument();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		CompoundUndoManager.beginCompoundEdit(document);
		try {
			document.setOuterHTML(elem, html);
		} catch (BadLocationException | IOException ex) {
			ex.printStackTrace();
		}
		CompoundUndoManager.endCompoundEdit(document);
	}

	@Override
	protected void updateWysiwygContextState(JEditorPane ed) {
		int t = ELEM_PROPS;
		Element elem = elementAtCaretPosition(ed);
		if (elem != null) {
			t = getElementType(elem);
		}

		putValue(NAME, PROPS[t]);
	}

	private int getElementType(Element elem) {
		AttributeSet att = elem.getAttributes();
		String name = att.getAttribute(StyleConstants.NameAttribute).toString();

		//is it an image?
		if (name.equals("img")) {
			return IMG_PROPS;
		}

		//is it a link?
		for (Enumeration ee = att.getAttributeNames(); ee.hasMoreElements();) {
			if (ee.nextElement().toString().equals("a")) {
				return LINK_PROPS;
			}
		}

		//is it a list?
		if (HTMLUtils.getParent(elem, HTML.Tag.UL) != null) {
			return LIST_PROPS;
		}

		if (HTMLUtils.getParent(elem, HTML.Tag.OL) != null) {
			return LIST_PROPS;
		}

		//is it a table?
		if (HTMLUtils.getParent(elem, HTML.Tag.TD) != null) {
			return TABLE_PROPS;
		}

		//return the default
		return ELEM_PROPS;
	}

	/**
	 * Computes the (inline or block) element at the focused editor's caret position
	 *
	 * @return the element, or null of the element cant be retrieved
	 */
	private Element elementAtCaretPosition(JEditorPane ed) {
		if (ed == null) {
			return null;
		}

		HTMLDocument doc = (HTMLDocument) ed.getDocument();
		int caret = ed.getCaretPosition();

		Element elem = doc.getParagraphElement(caret);
		HTMLDocument.BlockElement blockElem = (HTMLDocument.BlockElement) elem;
		return blockElem.positionToElement(caret);
	}

	/* (non-Javadoc)
	 * @see shef.ui.text.actions.HTMLTextEditAction#sourceEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
	 */
	@Override
	protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {

	}

}
