package com.ingesup.labojava.bean;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PROFESSOR")
public class Professor extends User {
	
	
	private int experience;
	
	/* Collection */
	
	//protected List<Student> myStudents = new ArrayList<Student>();

	
	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public Professor() {
		super();
		type = "PROFESSOR";
	}

	public String toString() {

		return type +"\n" + super.toString();
	}
}
