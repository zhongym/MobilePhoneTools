package com.zhong.mobilephonetools.domain;

public class BlackNumberInfo {

	private String name;
	private String number;
	
	/**����ģʽ: 1.�绰���� 2.�������� 3.ȫ������**/
	private String mode;

	public BlackNumberInfo() {
		super();
	}

	public BlackNumberInfo(String name, String number, String mode) {
		super();
		this.name = name;
		this.number = number;
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	/**����ģʽ: 1.�绰���� 2.�������� 3.ȫ������**/
	public String getMode() {
		return mode;
	}
	/**����ģʽ: 1.�绰���� 2.�������� 3.ȫ������**/
	public void setMode(String mode) {
		this.mode = mode;
	}

	public String toString() {
		return "BlackNumberInfo [name=" + name + ", number=" + number + ", mode=" + mode + "]";
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlackNumberInfo other = (BlackNumberInfo) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

}
