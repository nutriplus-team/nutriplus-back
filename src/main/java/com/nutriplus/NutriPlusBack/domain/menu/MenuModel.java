package com.nutriplus.NutriPlusBack.domain.menu;

import com.nutriplus.NutriPlusBack.domain.AbstractEntity;
import com.nutriplus.NutriPlusBack.domain.meal.Meal;
import com.nutriplus.NutriPlusBack.domain.patient.Patient;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;

public abstract class MenuModel extends AbstractEntity {
    @Id
    @GeneratedValue
    public Long id;

    @Relationship(type = "MEALTYPE", direction = Relationship.UNDIRECTED)
    Meal mealType;

    @Relationship(type = "HAS_MENU", direction = Relationship.INCOMING)
    Patient patient;

    @Relationship(type = "PORTION", direction = Relationship.UNDIRECTED)
    ArrayList<Portion> portions;

    public MenuModel()
    {
        super();
    }
}