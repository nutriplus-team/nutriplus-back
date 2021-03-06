package com.nutriplus.NutriPlusBack.services.datafetcher;

import com.nutriplus.NutriPlusBack.domain.UserCredentials;
import com.nutriplus.NutriPlusBack.domain.food.Food;
import com.nutriplus.NutriPlusBack.domain.patient.Constants;
import com.nutriplus.NutriPlusBack.domain.patient.Patient;
import com.nutriplus.NutriPlusBack.domain.patient.PatientRecord;
import com.nutriplus.NutriPlusBack.domain.patient.PatientRecordDTO;
import com.nutriplus.NutriPlusBack.repositories.ApplicationRecordRepository;
import com.nutriplus.NutriPlusBack.repositories.ApplicationUserRepository;
import graphql.schema.DataFetcher;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class PatientDataFetcher {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private ApplicationRecordRepository applicationRecordRepository;

    @Autowired
    private ModelMapper modelMapper;


    public DataFetcher<Patient> getPatient() {
        return dataFetchingEnvironment -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            return user.getPatientByUuid(uuidPatient);
        };
    }

    public DataFetcher<ArrayList<Food>> getFoodRestrictions() {
        return dataFetchingEnvironment -> {
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            ArrayList<String> listUuids = user.getPatientByUuid(uuidPatient).getFoodRestrictionsUUID();
            return applicationUserRepository.findFoodRestrictions(listUuids);
        };
    }


    public DataFetcher<Boolean> removePatient(){
        return dataFetchingEnvironment -> {
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            Patient patient = user.getPatientByUuid(uuidPatient);
            if(patient == null)
                return false;
            ArrayList<PatientRecord> patientRecordList = applicationUserRepository.findPatientRecords(uuidPatient,0,Integer.MAX_VALUE);
            if(patient.getUuid().equals(uuidPatient))
            {
                for(PatientRecord record : patientRecordList){
                    applicationUserRepository.deletePatientRecordFromRepository(record.getUuid());
                }
                applicationUserRepository.deletePatientFromRepository(uuidPatient);
                return true;
            }else return false;
        };
    }

    public DataFetcher<Boolean> createPatient(){
        return dataFetchingEnvironment -> {

            LinkedHashMap<String,Object> input = dataFetchingEnvironment.getArgument("input");
            Patient patient = new Patient();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();

            for(String key:input.keySet()){
                Optional<Method> matchedMethod = Arrays.stream(patient.getClass().getDeclaredMethods()).filter(
                        method -> method.getName().toLowerCase().contains("set"+key.toLowerCase())
                ).findAny();
                if(matchedMethod.isPresent())
                {
                    matchedMethod.get().invoke(patient, input.get(key));
                }
            }
            user.setPatient(patient);
            applicationUserRepository.save(user);
            return true;
        };
    }

    public DataFetcher<String> createPatientRecord(){
        return dataFetchingEnvironment -> {
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            LinkedHashMap<String,Object> input = dataFetchingEnvironment.getArgument("input");
            PatientRecord patientRecord = new PatientRecord();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            Patient patient = user.getPatientByUuid(uuidPatient);
            if(patient == null)
                return "";

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date(System.currentTimeMillis());
            patientRecord.setDateModified(formatter.format(date));

            Constants methodBodyFatConstants = null;
            Constants methodMethabolicRateConstants = null;
            if(input.containsKey("methodBodyFat")){
                String methodBodyFatString = (String) input.get("methodBodyFat");
                methodBodyFatConstants = patientRecord.convertMethodStringToConstants(methodBodyFatString);
            }else{
                methodBodyFatConstants = Constants.FAULKNER;
            }
            if(input.containsKey("methodMethabolicRate")){
                String methodMethabolicRateString = (String) input.get("methodMethabolicRate");
                methodMethabolicRateConstants = patientRecord.convertMethodStringToConstants(methodMethabolicRateString);
            }else{
                methodMethabolicRateConstants = Constants.MIFFLIN;
            }
            for(String key : input.keySet()){
                Field field = PatientRecord.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(patientRecord, input.get(key));
            }

            if(!input.containsKey("age")){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date dateOfBirth = sdf.parse(patient.getDateOfBirth());
                Calendar today = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
                Calendar birthDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
                birthDate.setTime(dateOfBirth);

                int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

                if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                        (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
                    age--;

                    // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
                }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
                        (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
                    age--;
                }

                patientRecord.setAge(age);
            }

            if (input.containsKey("subscapular")&&input.containsKey("triceps")&&
                    input.containsKey("chest")&&input.containsKey("axillary")&&
                    input.containsKey("abdominal")&&input.containsKey("thigh")&&
                    !input.containsKey("corporalDensity")){

                patientRecord.calculateCorporalDensity(patient.getBiologicalSex());
            }

            if (((input.containsKey("triceps") && input.containsKey("abdominal") && input.containsKey("supriailiac")) ||
            !methodBodyFatConstants.equals(Constants.FAULKNER)&&input.containsKey("corporalDensity"))&& !input.containsKey("bodyFat")){
                patientRecord.calculateBodyFat(methodBodyFatConstants);
            }

            if(input.containsKey("height")&&input.containsKey("rightArmCirc")&&
                input.containsKey("triceps")&& input.containsKey("calf")&&input.containsKey("calfCirc")&&
                input.containsKey("thigh")&&input.containsKey("thighCirc")&&
                !input.containsKey("muscularMass")){

                patientRecord.calculateMuscularMass(patient.getBiologicalSex(),patient.getEthnicGroup());
            }

            if(input.containsKey("corporalMass")){
                if(!input.containsKey("methabolicRate"))
                    patientRecord.calculateMethabolicRate(methodMethabolicRateConstants,patient.getBiologicalSex());
                if(!input.containsKey("energyRequirements"))
                    patientRecord.calculateEnergyRequirements();
            }
            String uuidRecord = patientRecord.getUuid();
            patient.setPatientRecord(patientRecord);
            applicationUserRepository.save(user);
            return uuidRecord;
        };
    }

    public DataFetcher<Boolean> updateFoodRestrictions(){
        return dataFetchingEnvironment -> {
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            ArrayList<String> restrictedFoods = dataFetchingEnvironment.getArgument("uuidFoods");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            Patient patient = user.getPatientByUuid(uuidPatient);
            if(patient == null)
                return false;

            for(String uuidFood : restrictedFoods){
                if(!patient.getFoodRestrictionsUUID().contains(uuidFood))
                    patient.getFoodRestrictionsUUID().add(uuidFood);
            }

            if(patient.getUuid().equals(uuidPatient)) {
                applicationUserRepository.updatePatientFoodsRestrictionsFromRepository(uuidPatient,patient.getFoodRestrictionsUUID());
                return true;
            }else return false;
        };
    }

    public DataFetcher<Boolean> updatePatientRecord(){
        return dataFetchingEnvironment -> {
            String uuidPatientRecord = dataFetchingEnvironment.getArgument("uuidPatientRecord");
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            LinkedHashMap<String,Object> input = dataFetchingEnvironment.getArgument("input");

            PatientRecord patientRecord = applicationUserRepository.findSingleRecord(uuidPatientRecord);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            Patient patient = user.getPatientByUuid(uuidPatient);
            if(patient == null)
                return false;

            if(patientRecord.getUuid().equals(uuidPatientRecord)) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date(System.currentTimeMillis());
                patientRecord.setDateModified(formatter.format(date));

                for(String key : input.keySet()){
                    Field field = PatientRecord.class.getDeclaredField(key);
                    field.setAccessible(true);
                    field.set(patientRecord, input.get(key));
                }

                Constants methodBodyFat = null;
                Constants methodMethabolicRate = null;


                if(!input.containsKey("corporalDensity")){
                    Double corporalDensity = patientRecord.calculateCorporalDensity(patient.getBiologicalSex());
                    input.put("corporalDensity",corporalDensity);
                }

                if(!input.containsKey("bodyFat")){
                    if(input.containsKey("methodBodyFat")) {
                        String methodBodyFatString = (String) input.get("methodBodyFat");
                        methodBodyFat = patientRecord.convertMethodStringToConstants(methodBodyFatString);

                    }else{
                        methodBodyFat = patientRecord.convertMethodStringToConstants(patientRecord.getMethodBodyFat());
                    }
                    Double bodyFat = patientRecord.calculateBodyFat(methodBodyFat);
                    input.put("bodyFat",bodyFat);
                }


                if(!input.containsKey("muscularMass")){
                    Double muscularMass = patientRecord.calculateMuscularMass(patient.getBiologicalSex(),patient.getEthnicGroup());
                    input.put("muscularMass",muscularMass);
                }


                if(!input.containsKey("methabolicRate")){
                    if(input.containsKey("methodMethabolicRate")) {
                        String methodMethabolicRateString = (String) input.get("methodMethabolicRate");
                        methodMethabolicRate = patientRecord.convertMethodStringToConstants(methodMethabolicRateString);
                    }else{
                        methodMethabolicRate = patientRecord.convertMethodStringToConstants(patientRecord.getMethodMethabolicRate());
                    }
                    Double methabolicRate = patientRecord.calculateMethabolicRate(methodMethabolicRate, patient.getBiologicalSex());
                    input.put("methabolicRate",methabolicRate);
                }


                if(!input.containsKey("energyRequirements")){
                    Double energyRequirements = patientRecord.calculateEnergyRequirements();
                    input.put("energyRequirements",energyRequirements);
                }

                applicationUserRepository.updatePatientRecordFromRepository(uuidPatientRecord,input);

                return true;
            }else return false;
        };
    }



    public DataFetcher<Boolean> updatePatient(){
        return dataFetchingEnvironment -> {
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            LinkedHashMap<String,Object> input = dataFetchingEnvironment.getArgument("input");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            Patient patient = user.getPatientByUuid(uuidPatient);
            if(patient == null)
                return false;

            if(input.containsKey("restrictedFoods")){

                ArrayList<String> restrictedFoods = (ArrayList<String>) input.get("restrictedFoods");
                patient.getFoodRestrictionsUUID().clear();
                for(String uuidFood : restrictedFoods){
                    if(!patient.getFoodRestrictionsUUID().contains(uuidFood))
                        patient.getFoodRestrictionsUUID().add(uuidFood);
                }

                applicationUserRepository.updatePatientFoodsRestrictionsFromRepository(uuidPatient,patient.getFoodRestrictionsUUID());
            }



            if(patient.getUuid().equals(uuidPatient)){
                if(input.containsKey("dateOfBirth")){
                    String dateOfBirth = (String) input.get("dateOfBirth");
                    Date dateOfBirthValue = new SimpleDateFormat("dd/MM/yyyy").parse(dateOfBirth);
                    String dateOfBirthNeo4JFormat = new SimpleDateFormat("dd-MM-yyyy").format(dateOfBirthValue);
                    dateOfBirthNeo4JFormat = dateOfBirthNeo4JFormat.concat("T02:00:00.000Z");
                    input.put("dateOfBirthValue", dateOfBirthNeo4JFormat);
                }
                applicationUserRepository.updatePatientFromRepository(uuidPatient,input);
                return true;

            }else return false;
        };
    }


    public DataFetcher<Boolean> removePatientRecord(){
        return dataFetchingEnvironment -> {
            String uuidPatientRecord = dataFetchingEnvironment.getArgument("uuidPatientRecord");
            PatientRecord patientRecord = applicationUserRepository.findSingleRecord(uuidPatientRecord);
            if(patientRecord.getUuid().equals(uuidPatientRecord))
            {
                applicationUserRepository.deletePatientRecordFromRepository(uuidPatientRecord);
                return true;
            }else return false;
        };
    }

    private PatientRecordDTO mapRecordToDTO(PatientRecord record)
    {
        return modelMapper.map(record, PatientRecordDTO.class);
    }

    public DataFetcher<PatientRecordDTO> getSingleRecord(){
        return dataFetchingEnvironment -> {
            String uuidRecord = dataFetchingEnvironment.getArgument("uuidRecord");
            PatientRecord patientRecord = applicationUserRepository.findSingleRecord(uuidRecord);
            if(patientRecord == null)
                return null;

            PatientRecordDTO recordDTO = mapRecordToDTO(patientRecord);
            List<String> menusUuids = applicationRecordRepository.getMenusUuidsFromPatientRecord(uuidRecord);
            recordDTO.setMenus(menusUuids);

            if(patientRecord.getUuid().equals(uuidRecord)) {
                return recordDTO;
            }else return null;
        };
    }


    public DataFetcher<ArrayList<Patient>> getPatients(){
        return dataFetchingEnvironment -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();

            int indexPage = dataFetchingEnvironment.getArgument("indexPage");
            int sizePage = dataFetchingEnvironment.getArgument("sizePage");
            indexPage = indexPage*sizePage;
            return applicationUserRepository.findPatients(user.getUuid(),indexPage,sizePage);
        };
    }


    public DataFetcher<ArrayList<PatientRecord>> getAllPatientRecords(){
        return dataFetchingEnvironment -> {

            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            int indexPage = dataFetchingEnvironment.getArgument("indexPage");
            int sizePage = dataFetchingEnvironment.getArgument("sizePage");
            indexPage = indexPage*sizePage;
            return applicationUserRepository.findPatientRecords(uuidPatient,indexPage,sizePage);
        };
    }


    public DataFetcher<Boolean> removeFoodRestrictions() {
        return dataFetchingEnvironment ->{
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");
            ArrayList<String> restrictedFoods = dataFetchingEnvironment.getArgument("uuidFoods");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();
            Patient patient = user.getPatientByUuid(uuidPatient);
            if(patient == null)
                return false;

            for(String uuidFood : restrictedFoods){
                patient.getFoodRestrictionsUUID().remove(uuidFood);
            }

            if(patient.getUuid().equals(uuidPatient)) {
                applicationUserRepository.updatePatientFoodsRestrictionsFromRepository(uuidPatient,patient.getFoodRestrictionsUUID());
                return true;
            }else return false;
        };
    }

    public DataFetcher<Boolean> removeAllFoodRestrictions() {
        return dataFetchingEnvironment ->{
            String uuidPatient = dataFetchingEnvironment.getArgument("uuidPatient");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserCredentials user = (UserCredentials) authentication.getCredentials();

            Patient patient = user.getPatientByUuid(uuidPatient);
            if(patient == null)
                return false;

            patient.getFoodRestrictionsUUID().clear();

            if(patient.getUuid().equals(uuidPatient)) {
                applicationUserRepository.updatePatientFoodsRestrictionsFromRepository(uuidPatient,patient.getFoodRestrictionsUUID());
                return true;
            }else return false;
        };
    }
}
