package com.nutriplus.NutriPlusBack.domain.patient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Patient extends PatientModel {

    //Constructor
    public Patient(){
        super();
    }

    //Set Functions
    public void setPatientRecord(PatientRecord recordList){patientRecordList.add(recordList);}
    public void setCpf(String cpfValue){
        cpf = cpfValue;
    }
    public void setName(String nameValue){
        name = nameValue;
    }
    public void setDateOfBirth(String date) throws ParseException {
        dateOfBirthValue = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        dateOfBirth = new SimpleDateFormat("dd/MM/yyyy").format(dateOfBirthValue);
    }
    public void setBiologicalSex(Integer biologicalSexValue){
        biologicalSex = biologicalSexValue.shortValue();
    }
    public void setEthnicGroup(Double ethnicGroupValue){ ethnicGroup = ethnicGroupValue; }
    public void setNutritionist(String nutritionistValue){
        nutritionist = nutritionistValue;
    }
    public void setFoodRestrictionsUUID(ArrayList<String> foodRestrictionsUUID){
        foodRestrictions.addAll(foodRestrictionsUUID);
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    //Get Functions
    public String getEmail()
    {
        return this.email;
    }
    public Long getId() { return id; }
    public String getCpf(){
        return cpf;
    }
    public String getName(){
        return name;
    }
    public String getDateOfBirth() { return dateOfBirth; }
    public Short getBiologicalSex(){
        return biologicalSex;
    }
    public Double getEthnicGroup(){
        return ethnicGroup;
    }
    public String getNutritionist(){
        return nutritionist;
    }
    public ArrayList<String> getFoodRestrictionsUUID(){return foodRestrictions;}
    public ArrayList<PatientRecord> getPatientRecordList(){return patientRecordList;}
    public PatientRecord getLastPatientRecord(){
        int sizeList = getPatientRecordList().size();
        if(sizeList>0) {
            return getPatientRecordList().get(sizeList-1);
        }

        else return null;
    }

    public PatientRecord getRecordByUuid(String uuid)
    {
        return patientRecordList.stream().filter(record -> record.getUuid().equals(uuid)).findAny().orElse(null);
    }

}
