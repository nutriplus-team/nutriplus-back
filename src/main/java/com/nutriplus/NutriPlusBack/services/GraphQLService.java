package com.nutriplus.NutriPlusBack.services;

import com.google.common.io.Resources;
import com.nutriplus.NutriPlusBack.repositories.ApplicationUserRepository;
import com.nutriplus.NutriPlusBack.services.datafetcher.FoodDataFetcher;
import com.nutriplus.NutriPlusBack.services.datafetcher.MenuDataFetcher;
import com.nutriplus.NutriPlusBack.services.datafetcher.PatientDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Service
public class GraphQLService{

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    private GraphQL graphQL;

    @Autowired
    private PatientDataFetcher patientsDataFetcher;

    @Autowired
    private FoodDataFetcher foodDataFetcher;

    @Autowired
    private MenuDataFetcher menuDataFetcher;


    @PostConstruct
    private void loadSchema() throws IOException {
        URL url = Resources.getResource("Types.graphql");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        // parse schema
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring wiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        // Patient
                        .dataFetcher("getFoodRestrictions",patientsDataFetcher.getFoodRestrictions())
                        .dataFetcher("getPatientInfo", patientsDataFetcher.getPatient())
                        .dataFetcher("getSingleRecord",patientsDataFetcher.getSingleRecord())
                        .dataFetcher("getAllPatients",patientsDataFetcher.getPatients())
                        .dataFetcher("getPatientRecords",patientsDataFetcher.getAllPatientRecords())
                        // Food
                        .dataFetcher("listFood",foodDataFetcher.listFood())
                        .dataFetcher("listFoodPaginated",foodDataFetcher.listFoodPaginated())
                        .dataFetcher("searchFood",foodDataFetcher.searchFood())
                        .dataFetcher("getUnits",foodDataFetcher.getUnits())
                        .dataFetcher("listMealsThatAFoodBelongsTo",foodDataFetcher.listMealsThatAFoodBelongsTo())
                        .dataFetcher("getMeal",foodDataFetcher.getMeal())

                        //Menu
                        .dataFetcher("getMenu", menuDataFetcher.getMenu())
                        .dataFetcher("getAllMenusForPatient", menuDataFetcher.getAllMenusForPatient())
                        .dataFetcher("getMenusForMeal", menuDataFetcher.getMenusForMeal())
                        .dataFetcher("getMenusForRecord", menuDataFetcher.getMenusForRecord())
                )
                .type("Mutation",typeWiring->typeWiring
                        // Patient
                        .dataFetcher("removePatient",patientsDataFetcher.removePatient())
                        .dataFetcher("createPatient",patientsDataFetcher.createPatient())
                        .dataFetcher("updatePatient",patientsDataFetcher.updatePatient())
                        .dataFetcher("createPatientRecord",patientsDataFetcher.createPatientRecord())
                        .dataFetcher("updatePatientRecord",patientsDataFetcher.updatePatientRecord())
                        .dataFetcher("removePatientRecord",patientsDataFetcher.removePatientRecord())
                        .dataFetcher("updateFoodRestrictions",patientsDataFetcher.updateFoodRestrictions())
                        .dataFetcher("removeFoodRestrictions",patientsDataFetcher.removeFoodRestrictions())
                        .dataFetcher("removeAllFoodRestrictions",patientsDataFetcher.removeAllFoodRestrictions())
                        // Food
                        .dataFetcher("createFood", foodDataFetcher.createFood())
                        .dataFetcher("customizeFood", foodDataFetcher.customizeFood())
                        .dataFetcher("removeFood", foodDataFetcher.removeFood())
                        .dataFetcher("startMeals", foodDataFetcher.startMeals())
                        .dataFetcher("addFoodToMeal", foodDataFetcher.addFoodToMeal())
                        .dataFetcher("removeFoodFromMeal", foodDataFetcher.removeFoodFromMeal())
                        .dataFetcher("setMeals", foodDataFetcher.setMeals())

                        //Menu
                        .dataFetcher("addMenu",menuDataFetcher.addMenu())
                        .dataFetcher("removeMenu",menuDataFetcher.removeMenu())
                        .dataFetcher("editMenu",menuDataFetcher.editMenu()))
                .build();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

}
