package com.gmail.trentech.wirelessred.utils;

public class InvalidItemTypeException extends Exception {
	
	private static final long serialVersionUID = 1997753363232807009L;
	
	public InvalidItemTypeException() {
		
	}
	
	public InvalidItemTypeException(String message) {
		super(message);
	}
}
