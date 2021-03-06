package com.nutriplus.NutriPlusBack.domain.patient;


import com.nutriplus.NutriPlusBack.domain.AbstractEntity;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


@NodeEntity
public class PatientRecord extends AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;


    public PatientRecord(){ super(); }

    String uuidPatient;

    String dateModified;

    Boolean isAthlete ;
    Integer age;
    Double physicalActivityLevel;
    Double corporalMass;
    Double height ;
    String observations;

    //Fat Folds
    Double subscapular;
    Double triceps;
    Double biceps;
    Double chest;
    Double axillary;
    Double supriailiac;
    Double abdominal;
    Double thigh;
    Double calf;

    //Circumferences
    Double waistCirc;
    Double abdominalCirc;
    Double hipsCirc;
    Double rightArmCirc;
    Double thighCirc;
    Double calfCirc;

    Double muscularMass;
    Double corporalDensity;
    Double bodyFat;
    Double methabolicRate;
    Double energyRequirements;

    String methodBodyFat;
    String methodMethabolicRate;

    String anamnesis;
    String exam;
    //Set Functions
    public void setAnamnesis(String anamnesisValue){anamnesis=anamnesisValue;}
    public void setExam(String examValue){exam=examValue;}
    public void setMethodBodyFat(String methodBodyFatValue){methodBodyFat=methodBodyFatValue;}
    public void setMethodMethabolicRate(String methodMethabolicRateValue){methodMethabolicRate=methodMethabolicRateValue;}
    public void setUuidPatient(String uuid){uuidPatient = uuid;}
    public void setDateModified(String stringDate) { dateModified = stringDate; }
    public void setIsAthlete(Boolean value){isAthlete = value;}
    public void setAge(Integer ageValue){age = ageValue;}
    public void setPhysicalActivityLevel(Double physicalActivityLevelValue){physicalActivityLevel = physicalActivityLevelValue;}
    public void setCorporalMass(Double corporalMassValue){corporalMass = corporalMassValue;}
    public void setHeight(Double heightValue){height = heightValue;}
    public void setObservations(String observationsValue){observations = observationsValue;}

    //Fat Folds
    public void setSubscapular(Double subscapularValue){subscapular = subscapularValue;}
    public void setTriceps(Double tricepsValue){triceps = tricepsValue;}
    public void setBiceps(Double bicepsValue){biceps = bicepsValue;}
    public void setChest(Double chestValue){chest = chestValue;}
    public void setAxillary(Double axillaryValue){axillary = axillaryValue;}
    public void setSupriailiac(Double supriailiacValue){supriailiac = supriailiacValue;}
    public void setAbdominal(Double abdominalValue){abdominal = abdominalValue;}
    public void setThigh(Double thighValue){thigh = thighValue;}
    public void setCalf(Double calfValue){calf = calfValue;}

    //Circumferences
    public void setWaistCirc(Double waistCircValue){waistCirc=waistCircValue;}
    public void setAbdominalCirc(Double abdominalCircValue){abdominalCirc=abdominalCircValue;}
    public void setHipsCirc(Double hipsCircValue){hipsCirc = hipsCircValue;}
    public void setRightArmCirc(Double rightArmCircValue){rightArmCirc = rightArmCircValue;}
    public void setThighCirc(Double thighCircValue){thighCirc = thighCircValue;}
    public void setCalfCirc(Double calfCircValue){calfCirc = calfCircValue;}

    public void setMuscularMass(Double muscularMassValue){muscularMass = muscularMassValue;}
    public void setCorporalDensity(Double corporalDensityValue){corporalDensity = corporalDensityValue;}
    public void setBodyFat(Double bodyFatValue){bodyFat = bodyFatValue;}
    public void setMethabolicRate(Double methabolicRateValue){methabolicRate = methabolicRateValue;}
    public void setEnergyRequirements(Double energyRequirementsValue){energyRequirements = energyRequirementsValue;}

    //Get Functions
    public String getAnamnesis(){return anamnesis;}
    public String getExam(){return exam;}
    public String getMethodBodyFat(){return methodBodyFat;}
    public String getMethodMethabolicRate(){return methodMethabolicRate;}
    public String getUuidPatient(){return uuidPatient;}
    public String getDateModified(){//return new SimpleDateFormat("dd/MM/yyyy").format(dateModified);
        return dateModified;
    }
    public Boolean getIsAthlete(){return isAthlete;}
    public Integer getAge(){return age;}
    public Double getPhysicalActivityLevel(){return physicalActivityLevel;}
    public Double getCorporalMass(){return corporalMass;}
    public Double getHeight(){return height;}
    public String getObservations(){return observations;}

    //Fat Folds
    public Double getSubscapular(){return subscapular;}
    public Double getTriceps(){return triceps;}
    public Double getBiceps(){return biceps;}
    public Double getChest(){return chest;}
    public Double getAxillary(){return axillary;}
    public Double getSupriailiac(){return supriailiac;}
    public Double getAbdominal(){return abdominal;}
    public Double getThigh(){return thigh;}
    public Double getCalf(){return calf;}

    //Circumferences
    public Double getWaistCirc(){return waistCirc;}
    public Double getAbdominalCirc(){return abdominalCirc;}
    public Double getHipsCirc(){return hipsCirc;}
    public Double getRightArmCirc(){return rightArmCirc;}
    public Double getThighCirc(){return thighCirc;}
    public Double getCalfCirc(){return calfCirc;}

    public Double getMuscularMass(){return muscularMass;}
    public Double getCorporalDensity(){return corporalDensity;}
    public Double getBodyFat(){return bodyFat;}
    public Double getMethabolicRate(){return methabolicRate;}
    public Double getEnergyRequirements(){return energyRequirements;}

    //Calculate Functions
    public Double calculateEnergyRequirements(){
        Double energyRequirements = getMethabolicRate()*getPhysicalActivityLevel();
        setEnergyRequirements(energyRequirements);
        return energyRequirements;
    }

    public Constants convertMethodStringToConstants(String method){
        Constants methodConstants = null;
        switch (method.toUpperCase()){
            case "TINSLEY": methodConstants = Constants.TINSLEY;break;
            case "TINSLEY_NO_FAT": methodConstants = Constants.TINSLEY_NO_FAT;break;
            case "CUNNINGHAM": methodConstants = Constants.CUNNINGHAM;break;
            case "FAULKNER": methodConstants = Constants.FAULKNER;break;
            case "MIFFLIN": methodConstants = Constants.MIFFLIN;break;
            case "POLLOK": methodConstants = Constants.POLLOK;break;
        }
        return methodConstants;
    }

    public Double calculateMethabolicRate(Constants methodMethabolicRate, Short biologicalSex){
        //the default is "" string when patient is no athlete.
        double value = 0;
        if(getIsAthlete()){
            switch (methodMethabolicRate){
                case TINSLEY:
                    value = (Double) 24.8 * getCorporalMass() + 10;
                    break;
                case TINSLEY_NO_FAT:
                    value = (Double)(25.9*getCorporalMass()*(100 - getBodyFat())/100 + 284);
                    break;
                case CUNNINGHAM:
                    value = (Double)(22*getCorporalMass()*(100 - getBodyFat())/100 + 500);
                    break;
                default:
                    value = (Double) 24.8 * getCorporalMass() + 10;
            }
        }
        else {
            if (biologicalSex == 0) //female
                value = (Double) (9.99 * getCorporalMass() + 6.25 * getHeight() - 4.92 * getAge() - 161);
            else                                //male
                value = (Double) (9.99 * getCorporalMass() + 6.25 * getHeight() - 4.92 * getAge() + 5);
        }
        setMethabolicRate(value);
        return value;
    }

    public Double calculateMuscularMass(Short biologicalSex,Double ethnicGroup){
        Double value = (Double)(getHeight()*0.0074*Math.pow(getRightArmCirc() - 3.1416*getTriceps()/10,2)+0.00088*Math.pow(getThighCirc()-3.1416*getThigh()/10,2)+0.00441*Math.pow(getCalfCirc()-3.1416*getCalf()/10,2)+2.4*biologicalSex-0.048*getAge()+ethnicGroup+7.8);
        setMuscularMass(value);
        return value;
    }

    public float sumSevenSkinFolds(){
        return (float) (getSubscapular()+getTriceps()+getChest()+getAxillary()+getSupriailiac()+getAbdominal()+getThigh());
    }

    public float sumAllSkinFolds(){
        return (float) (getSubscapular()+getTriceps()+getChest()+getAxillary()+getSupriailiac()+getAbdominal()+getThigh()+getBiceps()+getCalf());
    }

    public Double calculateCorporalDensity(Short biologicalSex){
        Double corporal_density;

        if (biologicalSex == 0) //female
            corporal_density = (Double) (1.097 - 0.00046971*sumSevenSkinFolds()+0.00000056*Math.pow(sumSevenSkinFolds(),2)- 0.00012828*getAge());

        else
            corporal_density = (Double) (1.112 - 0.00043499*sumSevenSkinFolds() + 0.00000055*Math.pow(sumSevenSkinFolds(),2)-0.00028826*sumSevenSkinFolds()*getAge());

        setCorporalDensity(corporal_density);

        return corporal_density;
    }

    public Double calculateBodyFat(Constants methodBodyFat){

        Double body_fat;
        if(methodBodyFat == Constants.FAULKNER)
            body_fat = (Double) ((getSubscapular()+getTriceps()+getAbdominal()+getSupriailiac())*0.153+5.18);
        else
            body_fat = (Double) ((4.95/(getCorporalDensity()) - 4.5)*100);

        setBodyFat(body_fat);

        return body_fat;
    }


}
