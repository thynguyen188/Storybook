package storybook.ui.panel.reading;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.entity.Strand;
import storybook.i18n.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class StrandPanel extends AbstractPanel implements ItemListener {

	private final ReadingPanel bookReading;
	private HashSet<Long> strandIds;
	private List<JCheckBox> cbList;

	public StrandPanel(MainFrame mainFrame, ReadingPanel booReading) {
		this.mainFrame = mainFrame;
		this.bookReading = booReading;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (BookController.StrandProps.UPDATE.check(propName)) {
			refresh();
		}
	}

	@Override
	public void init() {
		strandIds = new HashSet<>();
		cbList = new ArrayList<>();
		addAllStrands();
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap"));
		setBackground(Color.white);
		setBorder(SwingUtil.getBorderDefault());

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		List<Strand> list = dao.findAll();
		for (Strand strand : list) {
			JCheckBox cb = new JCheckBox(strand.toString());
			cbList.add(cb);
			long id = strand.getId();
			if (strandIds.contains(id)) {
				cb.setSelected(true);
			}
			cb.setName(Long.toString(id));
			cb.setOpaque(false);
			cb.addItemListener(this);
			add(new JLabel(strand.getColorIcon()), "split 2");
			add(cb);
		}

		JButton btAll = new JButton(getSelectAllAction());
		btAll.setText(I18N.getMsg("tree.show.all"));
		btAll.setName("all");
		btAll.setOpaque(false);
		add(btAll, "sg,gapy 20");

		JButton cbNone = new JButton(getSelectNoneAction());
		cbNone.setText(I18N.getMsg("tree.show.none"));
		cbNone.setName("none");
		cbNone.setOpaque(false);
		add(cbNone, "sg");
	}

	private AbstractAction getSelectAllAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllStrands();
				for (JCheckBox cb : cbList) {
					cb.setSelected(true);
				}
				bookReading.refresh();
			}
		};
	}

	private AbstractAction getSelectNoneAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				strandIds.clear();
				for (JCheckBox cb : cbList) {
					cb.setSelected(false);
				}
				bookReading.refresh();
			}
		};
	}

	private void addAllStrands() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		List<Strand> list = dao.findAll();
		for (Strand strand : list) {
			strandIds.add(strand.getId());
		}
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		try {
			JCheckBox cb = (JCheckBox) evt.getSource();
			Long id = new Long(cb.getName());
			if (cb.isSelected()) {
				strandIds.add(id);
			} else {
				strandIds.remove(id);
			}
			bookReading.refresh();
		} catch (Exception e) {
			System.err.println(Arrays.toString(e.getStackTrace()));
		}
	}

	public HashSet<Long> getStrandIds() {
		return strandIds;
	}
}
