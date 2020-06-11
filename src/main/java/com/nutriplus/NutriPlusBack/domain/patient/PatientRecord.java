package com.nutriplus.NutriPlusBack.domain.patient;

import com.nutriplus.NutriPlusBack.domain.AbstractEntity;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.format.DateTimeFormatter;

@NodeEntity
public class PatientRecord extends AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    DateTimeFormatter dateModified;

    public PatientRecord(){
        super();
        dateModified = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    String uuidPatient;

    boolean isAthlete ;
    int age;
    float physicalActivityLevel;
    float corporalMass;
    float height ;
    String observations;

    //Fat Folds
    float subscapular;
    float triceps;
    float biceps;
    float chest;
    float axillary;
    float supriailiac;
    float abdominal;
    float thigh;
    float calf;

    //Circumferences
    float waistCirc;
    float abdominalCirc;
    float hipsCirc;
    float rightArmCirc;
    float thighCirc;
    float calfCirc;

    float muscularMass;
    float corporalDensity;
    float bodyFat;
    float methabolicRate;
    float energyRequirements;


    //Set Functions
    public void setUuidPatient(String uuid){uuidPatient = uuid;}
    public void setDateModified(DateTimeFormatter date){dateModified = date;}
    public void setIsAthlete(boolean value){isAthlete = value;}
    public void setAge(int ageValue){age = ageValue;}
    public void setPhysicalActivityLevel(float physicalActivityLevelValue){physicalActivityLevel = physicalActivityLevelValue;}
    public void setCorporalMass(float corporalMassValue){corporalMass = corporalMassValue;}
    public void setHeight(float heightValue){height = heightValue;}
    public void setObservations(String observationsValue){observations = observationsValue;}

    //Fat Folds
    public void setSubscapular(float subscapularValue){subscapular = subscapularValue;}
    public void setTriceps(float tricepsValue){triceps = tricepsValue;}
    public void setBiceps(float bicepsValue){biceps = bicepsValue;}
    public void setChest(float chestValue){chest = chestValue;}
    public void setAxillary(float axillaryValue){axillary = axillaryValue;}
    public void setSupriailiac(float supriailiacValue){supriailiac = supriailiacValue;}
    public void setAbdominal(float abdominalValue){abdominal = abdominalValue;}
    public void setThigh(float thighValue){thigh = thighValue;}
    public void setCalf(float calfValue){calf = calfValue;}

    //Circumferences
    public void setWaistCirc(float waistCircValue){waistCirc=waistCircValue;}
    public void setAbdominalCirc(float abdominalCircValue){abdominalCirc=abdominalCircValue;}
    public void setHipsCirc(float hipsCircValue){hipsCirc = hipsCircValue;}
    public void setRightArmCirc(float rightArmCircValue){rightArmCirc = rightArmCircValue;}
    public void setThighCirc(float thighCircValue){thighCirc = thighCircValue;}
    public void setCalfCirc(float calfCircValue){calfCirc = calfCircValue;}

    public void setMuscularMass(float muscularMassValue){muscularMass = muscularMassValue;}
    public void setCorporalDensity(float corporalDensityValue){corporalDensity = corporalDensityValue;}
    public void setBodyFat(float bodyFatValue){bodyFat = bodyFatValue;}
    public void setMethabolicRate(float methabolicRateValue){methabolicRate = methabolicRateValue;}
    public void setEnergyRequirements(float energyRequirementsValue){energyRequirements = energyRequirementsValue;}

    //Get Functions
    public String getUuidPatient(){return uuidPatient;}
    public DateTimeFormatter getDateModified(){return dateModified;}
    public boolean getIsAthlete(){return isAthlete;}
    public int getAge(){return age;}
    public float getPhysicalActivityLevel(){return physicalActivityLevel;}
    public float getCorporalMass(){return corporalMass;}
    public float getHeight(){return height;}
    public String getObservations(){return observations;}

    //Fat Folds
    public float getSubscapular(){return subscapular;}
    public float getTriceps(){return triceps;}
    public float getBiceps(){return biceps;}
    public float getChest(){return chest;}
    public float getAxillary(){return axillary;}
    public float getSupriailiac(){return supriailiac;}
    public float getAbdominal(){return abdominal;}
    public float getThigh(){return thigh;}
    public float getCalf(){return calf;}

    //Circumferences
    public float getWaistCirc(){return waistCirc;}
    public float getAbdominalCirc(){return abdominalCirc;}
    public float getHipsCirc(){return hipsCirc;}
    public float getRightArmCirc(){return rightArmCirc;}
    public float getThighCirc(){return thighCirc;}
    public float getCalfCirc(){return calfCirc;}

    public float getMuscularMass(){return muscularMass;}
    public float getCorporalDensity(){return corporalDensity;}
    public float getBodyFat(){return bodyFat;}
    public float getMethabolicRate(){return methabolicRate;}
    public float getEnergyRequirements(){return energyRequirements;}

    //Calculate Functions
    public void calculateEnergyRequirements(){
        setEnergyRequirements(getMethabolicRate()*getPhysicalActivityLevel());
    }

    public void calculateMethabolicRate(Constants method, short biologicalSex){
        //the default is "" string when patient is no athlete.
        float value = 0;
        if(getIsAthlete()){
            switch (method){
                case TINSLEY:
                    value = (float) 24.8 * getCorporalMass() + 10;
                    break;
                case TINSLEY_NO_FAT:
                    value = (float)(25.9*getCorporalMass()*(100 - getBodyFat())/100 + 284);
                    break;
                case CUNNINGHAM:
                    value = (float)(22*getCorporalMass()*(100 - getBodyFat())/100 + 500);
                    break;
            }
        }
        else if (biologicalSex == 0) //female
            value = (float) (9.99 * getCorporalMass() + 6.25 * getHeight() - 4.92 * getAge() - 161);
        else                                //male
            value = (float) (9.99 * getCorporalMass() + 6.25 * getHeight() - 4.92 * getAge() + 5);

        setMethabolicRate(value);
    }

    public void calculateMuscularMass(short biologicalSex,float ethnicGroup){
        setMuscularMass((float)(getHeight()*0.0074*Math.pow(getRightArmCirc() - 3.1416*getTriceps()/10,2)+0.00088*Math.pow(getThighCirc()-3.1416*getThigh()/10,2)+0.00441*Math.pow(getCalfCirc()-3.1416*getCalf()/10,2)+2.4*biologicalSex-0.048*getAge()+ethnicGroup+7.8));
    }

    public float sumSevenSkinFolds(){
        return getSubscapular()+getTriceps()+getChest()+getAxillary()+getSupriailiac()+getAbdominal()+getThigh();
    }

    public float sumAllSkinFolds(){
        return getSubscapular()+getTriceps()+getChest()+getAxillary()+getSupriailiac()+getAbdominal()+getThigh()+getBiceps()+getCalf();
    }

    public void calculateCorporalDensity(short biologicalSex){
        float corporal_density;

        if (biologicalSex == 0) //female
            corporal_density = (float) (1.097 - 0.00046971*sumSevenSkinFolds()+0.00000056*Math.pow(sumSevenSkinFolds(),2)- 0.00012828*getAge());

        else
            corporal_density = (float) (1.112 - 0.00043499*sumSevenSkinFolds() + 0.00000055*Math.pow(sumSevenSkinFolds(),2)-0.00028826*sumSevenSkinFolds()*getAge());

        setCorporalDensity(corporal_density);
    }

    public void calculateBodyFat(Constants method){

        float body_fat;
        if(method == Constants.FAULKNER)
            body_fat = (float) ((getSubscapular()+getTriceps()+getAbdominal()+getSupriailiac())*0.153+5.18);
        else
            body_fat = (float) ((4.95/(getCorporalDensity()) - 4.5)*100);

        setBodyFat(body_fat);
    }


}