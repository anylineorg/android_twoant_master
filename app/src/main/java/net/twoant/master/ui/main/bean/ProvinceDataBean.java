package net.twoant.master.ui.main.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProvinceDataBean implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6459971133884918655L;
	private ArrayList<String> mDistrict;
	private HashMap<String, List<String>> mCitys;
	private HashMap<String, List<String>> mCounty;

	public ArrayList<String> getDistrict() {
		return mDistrict;
	}

	public void setDistrict(ArrayList<String> mDistrict) {
		this.mDistrict = mDistrict;
	}

	public HashMap<String, List<String>> getCitys() {
		return mCitys;
	}

	public void setCitys(HashMap<String, List<String>> mCitys) {
		this.mCitys = mCitys;
	}

	public HashMap<String, List<String>> getCounty() {
		return mCounty;
	}

	public void setCounty(HashMap<String, List<String>> mCounty) {
		this.mCounty = mCounty;
	}

	@Override
	public String toString() {
		return "ProvinceDataBean [mDistrict=" + mDistrict + ", mCitys=" + mCitys + ", mCounty=" + mCounty + "]";
	}
	
	

}