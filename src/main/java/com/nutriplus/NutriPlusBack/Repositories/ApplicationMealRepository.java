package com.nutriplus.NutriPlusBack.Repositories;

import com.nutriplus.NutriPlusBack.Domain.Meal.Meal;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface ApplicationMealRepository extends Neo4jRepository<Meal, Long> {

    Meal getMealById(Long id);

    @Query("MATCH (m:Meal) DETACH DELETE m")
    void deleteMealById(Long Id);
}