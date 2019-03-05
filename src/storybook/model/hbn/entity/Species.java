package storybook.model.hbn.entity;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import storybook.i18n.I18N;

public class Species extends AbstractEntity{
	private String species;
	
	public Species()
	{
		
	}
	
	public Species(String species)
	{
		this.species=species;
	}
	
	public String getName() {
		return this.species;
	}

	public void setName(String species) {
		this.species = species;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
