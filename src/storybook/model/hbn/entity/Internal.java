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

public class Internal extends AbstractEntity {

	private String key;
	private String stringValue;
	private Integer integerValue;
	private Boolean booleanValue;
	private byte[] binValue;

	public Internal() {
	}

	public Internal(String key) {
		this.key = key;
	}

	public Internal(String key, String stringValue) {
		this.key = key;
		this.stringValue = stringValue;
	}

	public Internal(String key, Integer integerValue) {
		this.key = key;
		this.integerValue = integerValue;
	}

	public Internal(String key, Boolean booleanValue) {
		this.key = key;
		this.booleanValue = booleanValue;
	}

	public Internal(String key, byte[] binValue) {
		this.key = key;
		this.binValue = binValue;
	}

	public Internal(String key, Object value) {
		this.key = key;
		if (value instanceof String) {
			this.stringValue = (String) value;
		} else if (value instanceof Integer) {
			this.integerValue = (Integer) value;
		} else if (value instanceof Boolean) {
			this.booleanValue = (Boolean) value;
		} else if (value instanceof byte[]) {
			// ignore
		} else {
			System.err.println("Internal.Internal(): Unknown Type");
		}
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public boolean hasStringValue() {
		return stringValue != null && stringValue.length() > 0;
	}

	public Integer getIntegerValue() {
		return this.integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public boolean hasIntegerValue() {
		return integerValue != null;
	}

	public Boolean getBooleanValue() {
		return this.booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public boolean hasBooleanValue() {
		return booleanValue != null;
	}

	public byte[] getBinValue() {
		return binValue;
	}

	public void setBinValue(byte[] binValue) {
		this.binValue = binValue;
	}

	public boolean hasBinValue() {
		return binValue != null;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(key);
		buf.append(": ");
		if (hasStringValue()) {
			buf.append("string: '").append(stringValue).append("' ");
		} else {
			if (hasIntegerValue()) {
				buf.append("int: ").append(integerValue).append(" ");
			}
			if (hasBooleanValue()) {
				buf.append("boolean: ").append(booleanValue).append(" ");
			}
			if (hasBinValue()) {
				buf.append("'bin: [binary]");
			}
		}
		return buf.toString();
	}
	
	@Override
	public String toCsv(String quoteStart,String quoteEnd, String separator) {
		String b="";
		b+=quoteStart+key+quoteStart+separator+quoteEnd+quoteStart;
		if (hasStringValue()) {
			b=stringValue;
		} else if (hasIntegerValue()) {
			b=integerValue.toString();
		} else if (hasBooleanValue()) {
			b=booleanValue.toString();
		} else {
			b+="";
		}
		b+=quoteEnd+"\n";
		return(b);
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
		String b="";
		String k=key.replace(" ", "_");
		if (hasStringValue()) {
			b=xmlTab(2)+"<"+k+">"+stringValue+"</"+k+">\n";
		} else if (hasIntegerValue()) {
			b=xmlTab(2)+"<"+k+">"+integerValue.toString()+"</"+k+">\n";
		} else if (hasBooleanValue()) {
			b=xmlTab(2)+"<"+k+">"+booleanValue.toString()+"</"+k+">\n";
		}
		return(b);
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		Internal test = (Internal) obj;
		boolean ret = true;
		ret = ret && equalsStringNullValue(stringValue, test.getStringValue());
		ret = ret
				&& equalsIntegerNullValue(integerValue, test.getIntegerValue());
		ret = ret
				&& equalsBooleanNullValue(booleanValue, test.getBooleanValue());
		if (binValue != null) {
			ret = ret && binValue.equals(test.binValue);
		}
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (stringValue != null ? stringValue.hashCode() : 0);
		hash = hash * 31 + (integerValue != null ? integerValue.hashCode() : 0);
		hash = hash * 31 + (booleanValue != null ? booleanValue.hashCode() : 0);
		hash = hash * 31 + (binValue != null ? binValue.hashCode() : 0);
		return hash;
	}
}
