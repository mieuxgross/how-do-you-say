package com.cs683proj.howdoyousay;

import java.util.UUID;

public class Translation {
	
	// Translation members
	private UUID mId;				// for unique onject serialization
	private String mSource;			// to store source text
	private String mTranslated;		// to store translated text
	private String mFrom;			// to store "from" language
	private String mTo;				// to store "to" language
	private Boolean isValid;		// validity boolean if translation's meaning is preserved

	// constructor gets unique id
	public Translation() {
		// Generating unique id
		mId = UUID.randomUUID();
	}
	
	// overloaded toString
	@Override
	public String toString() {
		return mSource;
	}

	// mSource getter
	public String getmSource() {
		return mSource;
	}

	// mSource setter
	public void setmSource(String mSource) {
		this.mSource = mSource;
	}

	// mTranslated getter
	public String getmTranslated() {
		return mTranslated;
	}

	// mTranslated setter
	public void setmTranslated(String mTranslated) {
		this.mTranslated = mTranslated;
	}

	// mFrom getter
	public String getmFrom() {
		return mFrom;
	}

	// mFrom setter
	public void setmFrom(String mFrom) {
		this.mFrom = mFrom;
	}

	// mTo getter
	public String getmTo() {
		return mTo;
	}

	// mTo setter
	public void setmTo(String mTo) {
		this.mTo = mTo;
	}

	// mId getter (no setter to ensure serialization)
	public UUID getmId() {
		return mId;
	}
	
	// isValid getter
	public Boolean getIsValid() {
		return isValid;
	}

	// isValid setter
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
}
