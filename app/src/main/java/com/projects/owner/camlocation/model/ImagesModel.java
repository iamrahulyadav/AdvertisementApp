package com.projects.owner.camlocation.model;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ImagesModel {

    private List<ImagesModel> imagesModelArrayList = new ArrayList<>();

    public List<ImagesModel> getImagesModelArrayList() {
        return imagesModelArrayList;
    }

    public void setImagesModelArrayList(ImagesModel imagesModel) {
        imagesModelArrayList.add(imagesModel);
    }

    //	constants for field references
    public static final String Id = "id";
    public static final String BRAND_NAME = "brandName";
    public static final String CATAGORY = "catagory";
    public static final String AGENCY = "agency";
    public static final String SIZE = "size";
    public static final String VENDOR = "vendor";
    public static final String RATING = "rating";
    public static final String BITMAP = "bitmap";
    public static final String DATES = "stringdate";
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    //	private fields
    private String ID;
    private String brandName;
    private String catagory;
    private String agency;
    private String size;
    private String vendor;
    private String rating;
    private byte[] bitmap;
    private String stringdate;
    private double lat;
    private double lng;

    //	getters and setters
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public String  getDate() {
        return stringdate;
    }

    public void setDate(String stringdate) {
        this.stringdate = stringdate;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    //	Used when creating the data object
    public ImagesModel(String id, String brandName, String vendor, String rating, String stringdate,
                       String size, String catagory, String agency, byte[] bitmap, Double lat, Double lng) {
        this.setID(id);
        this.setBrandName(brandName);
        this.setCatagory(catagory);
        this.setAgency(agency);
        this.setVendor(vendor);
        this.setSize(size);
        this.setRating(rating);
        this.setBitmap(bitmap);
        this.setDate(stringdate);
        this.setLat(lat);
        this.setLng(lng);
    }

    public ImagesModel() {
    }

    //	Create from a bundle
    public ImagesModel(Bundle b) {
        if (b != null) {
            this.setID(b.getString(b.getString(Id)));
            this.setBrandName(b.getString(BRAND_NAME));
            this.setCatagory(b.getString(CATAGORY));
            this.setAgency(b.getString(AGENCY));
            this.setVendor(b.getString(VENDOR));
            this.setSize(b.getString(SIZE));
            this.setRating(b.getString(RATING));
            this.setBitmap(b.getByteArray(BITMAP));
//            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
//            Date date = null;
//            try {
//                date = format.parse(b.getString(DATES));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            this.setDate(b.getString(DATES));
            this.setLat(b.getDouble(LAT));
            this.setLng(b.getDouble(LNG));
        }
    }

    //	Package data for transfer between activities
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString(Id, this.getID());
        b.putString(BRAND_NAME, this.getBrandName());
        b.putString(CATAGORY, this.getCatagory());
        b.putString(AGENCY, this.getAgency());
        b.putString(VENDOR, this.getVendor());
        b.putString(SIZE, this.getSize());
        b.putString(RATING, this.getRating());
        b.putByteArray(BITMAP, this.getBitmap());
        b.putString(DATES, this.getDate());
        b.putDouble(LAT, this.getLat());
        b.putDouble(LNG, this.getLng());
        return b;
    }

    @Override
    public String toString() {
        return getBrandName();
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}

