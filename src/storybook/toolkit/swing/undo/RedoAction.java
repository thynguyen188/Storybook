/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package storybook.toolkit.swing.undo;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
class RedoAction extends AbstractAction {

	private UndoManager undo;

	public RedoAction(UndoManager undo) {
		super("Redo");
		this.undo = undo;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (undo.canRedo()) {
			undo.redo();
		}
	}
}
