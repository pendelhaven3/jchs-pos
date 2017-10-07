package com.pj.magic.exception;

public class NotEnoughPromoPointsException extends Exception {

	private int claimPoints;
	private int availablePoints;

	public NotEnoughPromoPointsException(int claimPoints, int availablePoints) {
		this.claimPoints = claimPoints;
		this.availablePoints = availablePoints;
	}

	public int getClaimPoints() {
		return claimPoints;
	}
	
	public int getAvailablePoints() {
		return availablePoints;
	}
	
}