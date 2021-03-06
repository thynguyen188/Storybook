/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun

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

package storybook.model.hbn.entity;

import java.sql.Timestamp;
import java.util.Date;
import org.w3c.dom.Node;
import storybook.toolkit.html.HtmlUtil;


/**
 * Part generated by hbm2java
 *
 * @hibernate.class
 *   table="PART"
 */
@SuppressWarnings("serial")
public class Part extends AbstractEntity {

	private Integer number;
	private String name;
	private String notes;
	private Part superpart;
    private Timestamp creationTime;
    private Timestamp objectiveTime;
    private Timestamp doneTime;
    private Integer objectiveChars;

	public Part() {
		this.creationTime = new Timestamp(new Date().getTime());
	}

	public Part(Integer number, String name, String notes, Part superpart,
			Timestamp creationTime, Timestamp objectiveTime,
			Timestamp doneTime) {
		this.number = number;
		this.name = name;
		this.notes = notes;
		this.superpart = superpart;
		this.creationTime = creationTime;
		this.objectiveTime = objectiveTime;
		this.doneTime = doneTime;
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasNotes() {
		return(!HtmlUtil.htmlToText(this.notes).equals(""));
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNumberName() {
		return this.number + ": " + this.name;
	}

	public Part getSuperpart() {
        return this.superpart;
		//return(null);
    }

    public boolean hasSuperpart() {
        return superpart != null;
    }

    public void setSuperpart(Part superpart) {
    	this.superpart = superpart;
    }
    
    public boolean isPartOfPart(Part ancestor) {
        if (this.getId().equals(ancestor.getId())) {
    		return true;
    	}
    	/*if (this.hasSuperpart()) {
    		return this.getSuperpart().isPartOfPart(ancestor);
    	}*/
    	return false;
    }

    public boolean hasCreationTime() {
        return creationTime != null;
    }

    public void setCreationTime(Timestamp ts) {
    	creationTime = ts;
    }

    public Timestamp getCreationTime() {
    	return creationTime;
    }

    public boolean hasObjectiveTime() {
        return objectiveTime != null;
    }

    public void setObjectiveTime(Timestamp ts) {
    	objectiveTime = ts;
    }

    public Timestamp getObjectiveTime() {
    	return objectiveTime;
    }

    public boolean hasObjectiveChars() {
        return objectiveChars != null;
    }

	public Integer getObjectiveChars() {
		return (hasObjectiveChars()) ? this.objectiveChars : 0;
	}

	public void setObjectiveChars(Integer objectiveChars) {
		this.objectiveChars = objectiveChars;
	}

    public boolean isDone() {
        return hasDoneTime();
    }
    public boolean hasDoneTime() {
        return doneTime != null;
    }

    public void setDoneTime(Timestamp ts) {
    	doneTime = ts;
    }

    public Timestamp getDoneTime() {
    	return doneTime;
    }

	@Override
	public String toString() {
		return getNumber() + ": " + getName();
	}

	@Override
	public String toCsv(String quoteStart,String quoteEnd, String separator) {
		StringBuilder b=new StringBuilder();
		b.append(quoteStart).append(getId().toString()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getNumber().toString()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getName()).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getSuperpart())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getCreationTime())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getObjectiveTime())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getDoneTime())).append(quoteEnd).append(separator);
		b.append(quoteStart).append(getClean(getObjectiveChars())).append(quoteEnd).append(separator);
		b.append(xmlMeta(3,"notes",getNotes())).append(quoteEnd).append("\n");
		return(b.toString());
	}
	
	@Override
	public String toHtml() {
		return(toCsv("<td>","</td>","\n"));
	}
	
	@Override
	public String toText() {
		return(toCsv("","","\t"));
	}
	
	@Override
	public String toXml() {
		StringBuilder b=new StringBuilder();
		b.append(xmlTab(1)).append("<part \n");
		b.append(xmlCommon());
		b.append(xmlAttribute("number", getNumber().toString()));
		b.append(xmlAttribute("name", getName()));
		if (getSuperpart()!=null) b.append(xmlAttribute("superpart", getClean(getSuperpart().getName())));
		b.append(xmlTab(2)).append(">\n");
		b.append(xmlTab(2)).append("<objective \n");
		b.append(xmlAttribute("creation", getClean(getCreationTime())));
		b.append(xmlAttribute("objective", getClean(getObjectiveTime())));
		b.append(xmlAttribute("done", getClean(getDoneTime())));
		b.append(xmlAttribute("chars", getClean(getObjectiveChars())));
		b.append(xmlTab(2)).append("/>\n");
		b.append(xmlMeta(2,"notes",getNotes()));
		b.append(xmlTab(1)).append("</part>\n");
		return(b.toString());
	}
	
	public static Part fromXml(Node node) {
		Part p=new Part();
		p.setId(getXmlLong(node,"id"));
		p.setNumber(getXmlInteger(node,"number"));
		p.setName(getXmlString(node,"name"));
		p.setNotes(getXmlText(node,"notes"));
		return(p);
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		Part test = (Part) obj;
		boolean ret = true;
		ret = ret && equalsIntegerNullValue(number, test.getNumber());
		ret = ret && equalsStringNullValue(name, test.getName());
		ret = ret && equalsStringNullValue(notes, test.getNotes());
		ret = ret && equalsObjectNullValue(superpart, test.getSuperpart());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (number != null ? number.hashCode() : 0);
		hash = hash * 31 + (name != null ? name.hashCode() : 0);
		hash = hash * 31 + (notes != null ? notes.hashCode() : 0);
		//hash = hash * 31 + (superpart != null ? superpart.hashCode() : 0);
		return hash;
	}

}
