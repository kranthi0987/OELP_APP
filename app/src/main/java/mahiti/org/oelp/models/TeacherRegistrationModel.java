package mahiti.org.oelp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAJ ARYAN on 18/07/19.
 */
public class TeacherRegistrationModel {
    private String name;
    private String phoneNo;
    private String school;
    private String state;
    private String district;
    private String village;
    private String block;

    private String phoneNoOld;
    private String phoneNoNew;
    private String phoneNoConfirm;

    private List<String> stateArrayList;
    private List<String> districtArrayList;
    private List<String> blockArrayList;
    private List<String> villageArrayList;

    public TeacherRegistrationModel() {
        prepareDataForState();
        prepareDataForDistrict();
        prepareDataForBlock();
        prepareDataForVillage();
    }

    public TeacherRegistrationModel(String name, String phoneNo, String school, String state, String district, String village, String block) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.school = school;
        this.state = state;
        this.district = district;
        this.village = village;
        this.block = block;
    }

    public TeacherRegistrationModel(String phoneNoOld, String phoneNoNew, String phoneNoConfirm) {
        this.phoneNoOld = phoneNoOld;
        this.phoneNoNew = phoneNoNew;
        this.phoneNoConfirm = phoneNoConfirm;
    }

    public String getPhoneNoOld() {
        return phoneNoOld;
    }

    public void setPhoneNoOld(String phoneNoOld) {
        this.phoneNoOld = phoneNoOld;
    }

    public String getPhoneNoNew() {
        return phoneNoNew;
    }

    public void setPhoneNoNew(String phoneNoNew) {
        this.phoneNoNew = phoneNoNew;
    }

    public String getPhoneNoConfirm() {
        return phoneNoConfirm;
    }

    public void setPhoneNoConfirm(String phoneNoConfirm) {
        this.phoneNoConfirm = phoneNoConfirm;
    }

    public TeacherRegistrationModel(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public List<String> getStateArrayList() {
        if (stateArrayList==null){
            prepareDataForState();
        }
        return stateArrayList;
    }

    public void setStateArrayList(List<String> stateArrayList) {
        this.stateArrayList = stateArrayList;
    }

    public List<String> getDistrictArrayList() {
        return districtArrayList;
    }

    public void setDistrictArrayList(List<String> districtArrayList) {
        this.districtArrayList = districtArrayList;
    }

    public List<String> getBlockArrayList() {
        return blockArrayList;
    }

    public void setBlockArrayList(List<String> blockArrayList) {
        this.blockArrayList = blockArrayList;
    }

    public List<String> getVillageArrayList() {
        return villageArrayList;
    }

    public void setVillageArrayList(List<String> villageArrayList) {
        this.villageArrayList = villageArrayList;
    }

    public void prepareDataForState(){
        stateArrayList = new ArrayList<>();
        stateArrayList.add("Karnataka");
        stateArrayList.add("Tamil Nadu");
        stateArrayList.add("Kerala");
        stateArrayList.add("Madhya Pradesh");
        stateArrayList.add("Uttar Pradesh");
        stateArrayList.add("Delhi");
        stateArrayList.add("Gujrat");
    }

    public void prepareDataForDistrict(){
        districtArrayList = new ArrayList<>();
        districtArrayList.add("Karnataka");
        districtArrayList.add("Tamil Nadu");
        districtArrayList.add("Kerala");
        districtArrayList.add("Madhya Pradesh");
        districtArrayList.add("Uttar Pradesh");
        districtArrayList.add("Delhi");
        districtArrayList.add("Gujrat");
    }

    public void prepareDataForBlock(){
        blockArrayList = new ArrayList<>();
        blockArrayList.add("Karnataka");
        blockArrayList.add("Tamil Nadu");
        blockArrayList.add("Kerala");
        blockArrayList.add("Madhya Pradesh");
        blockArrayList.add("Uttar Pradesh");
        blockArrayList.add("Delhi");
        blockArrayList.add("Gujrat");
    }

    public void prepareDataForVillage(){
        villageArrayList = new ArrayList<>();
        villageArrayList.add("Karnataka");
        villageArrayList.add("Tamil Nadu");
        villageArrayList.add("Kerala");
        villageArrayList.add("Madhya Pradesh");
        villageArrayList.add("Uttar Pradesh");
        villageArrayList.add("Delhi");
        villageArrayList.add("Gujrat");
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }







}
