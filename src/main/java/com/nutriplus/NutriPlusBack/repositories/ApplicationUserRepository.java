package com.nutriplus.NutriPlusBack.repositories;

import com.nutriplus.NutriPlusBack.domain.patient.Patient;
import com.nutriplus.NutriPlusBack.domain.patient.PatientRecord;
import com.nutriplus.NutriPlusBack.domain.UserCredentials;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.ArrayList;

public interface ApplicationUserRepository extends Neo4jRepository<UserCredentials, Long> {
    UserCredentials findByUsername(String username);
    UserCredentials findByEmail(String email);

    @Query("MATCH (p:Patient) where p.uuid=$0 DETACH DELETE p")
    void deletePatientFromRepository(String uuid);

    @Query ("MATCH (p:Patient)<-[:IS_PATIENT]-(u:UserCredentials) WHERE u.uuid=$0 RETURN p SKIP $1 LIMIT $2")
    ArrayList<Patient> findPatients(String userCredentialsUuid, int numberPage, int sizePage);

    @Query ("MATCH (r:PatientRecord)<-[:HAS_RECORD]-(p:Patient) WHERE p.uuid=$0 RETURN r SKIP $1 LIMIT $2")
    ArrayList<PatientRecord> findPatientRecords(String patientUuid, int numberPage, int sizePage);

    UserCredentials findByUuid(String uuid);

    @Query("MATCH (u:UserCredentials) WHERE u.uuid=$0 DETACH DELETE u")
    void deleteByUuid(String uuid);
}