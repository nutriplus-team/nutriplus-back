package com.nutriplus.NutriPlusBack.Domain.Food;


import javax.validation.constraints.NotNull;

public class NutritionFacts extends NutritionFactsModel {
    // Constructors
    public NutritionFacts(){}
    public NutritionFacts(double caloriesValue, double proteinsValue, double carbohydratesValue, double lipidsValue,
                   double fiberValue)
    {
        calories = caloriesValue;
        proteins = proteinsValue;
        carbohydrates = carbohydratesValue;
        lipids = lipidsValue;
        fiber = fiberValue;
    }

    public NutritionFacts(@NotNull NutritionFacts nutritionFactsValue)
    {
        calories = nutritionFactsValue.calories;
        proteins = nutritionFactsValue.proteins;
        carbohydrates = nutritionFactsValue.carbohydrates;
        lipids = nutritionFactsValue.lipids;
        fiber = nutritionFactsValue.fiber;
    }

    public void copy(@NotNull NutritionFacts nutritionFactsValue)
    {
        calories = nutritionFactsValue.calories;
        proteins = nutritionFactsValue.proteins;
        carbohydrates = nutritionFactsValue.carbohydrates;
        lipids = nutritionFactsValue.lipids;
        fiber = nutritionFactsValue.fiber;
    }

}
