package com.nutriplus.NutriPlusBack;

import com.nutriplus.NutriPlusBack.Domain.Food.Food;
import com.nutriplus.NutriPlusBack.Domain.Meal.Meal;
import com.nutriplus.NutriPlusBack.Domain.Meal.MealType;
import com.nutriplus.NutriPlusBack.Domain.Food.NutritionFacts;
import com.nutriplus.NutriPlusBack.Domain.Menu.Menu;
import com.nutriplus.NutriPlusBack.Domain.Menu.Portion;
import com.nutriplus.NutriPlusBack.Domain.Patient.Constants;
import com.nutriplus.NutriPlusBack.Domain.Patient.Patient;
import com.nutriplus.NutriPlusBack.Domain.UserCredentials;
import com.nutriplus.NutriPlusBack.Repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SpringBootTest
class NutriPlusBackApplicationTests {

	@Autowired
	private ApplicationUserRepository applicationUserRepository;

	@Autowired
	private ApplicationMenuRepository applicationMenuRepository;

	@Autowired
	private ApplicationFoodRepository applicationFoodRepository;

	@Autowired
	private ApplicationMealRepository applicationMealRepository;


	@Test
	void contextLoads() {

	}

	@Test
	void insertPatient(){
		UserCredentials test_user = applicationUserRepository.findByUsername("adriano");
		assertThat(test_user).isNotNull();
		int limit = 100;

		for(int i = 0; i < limit ; i++){
			Patient test = new Patient();
			String Name = "TestPatient" + i;
			test.setName(Name);
			test.setCorporalMass((float)i + 50);
			test_user.setPatient(test);
		}
		applicationUserRepository.save(test_user);

	}

	@Test
	void TestPatient(){

		UserCredentials user = new UserCredentials("TestPatient","test@email.com","senhaTest","Test","P");
		Patient test = new Patient();

		test.setName("TestPatient");
		test.setCorporalMass((float)89.3);
		test.setCpf("123456");
		test.calculateMethabolicRate(Constants.TINSLEY);

		user.setPatient(test);
		applicationUserRepository.save(user);

		UserCredentials testUser = applicationUserRepository.findByUsername("TestPatient");

		assertThat(testUser).isNotNull();
		assertThat(testUser.getId()).isEqualTo(user.getId());

		//Delete Data
		user.deletePatient(test);
		applicationUserRepository.save(user);
		applicationUserRepository.deletePatientFromRepository(test.getUuid());
		applicationUserRepository.deleteById(user.getId());
	}

	@Test
	void TestMenu(){
		UserCredentials userMenu = new UserCredentials("TestMenu","test@email.com","senhaTest","Test","M");

		// Add patient
		Patient testPatient = new Patient();
		testPatient.setName("TestMenuPatient");
		testPatient.setCorporalMass((float)89.3);
		testPatient.setCpf("123456");
		testPatient.calculateMethabolicRate(Constants.TINSLEY);
		userMenu.setPatient(testPatient);
		applicationUserRepository.save(userMenu);

		// Add food
		NutritionFacts testNutritionFacts = new NutritionFacts(1.1, 2.2, 3.3,
				4.4, 5.5);
		Food testFood1 = new Food("Arroz branco", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts);
		Food testFood2 = new Food("Arroz carioca", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts);


		// Add meal type
		List<Food> foodList = new ArrayList<Food>();
		foodList.add(testFood1);
		foodList.add(testFood2);
		Meal testMeal = new Meal(MealType.DINNER, foodList);
		applicationMealRepository.save(testMeal);

		// Add menu
		Menu menu = new Menu(testMeal, testPatient);
		menu.addPortion(testFood1, 150);
		applicationMenuRepository.save(menu);

		// Test User
		UserCredentials testUser = applicationUserRepository.findByUsername("TestMenu");
		assertThat(testUser).isNotNull();
		assertThat(testUser.getId()).isEqualTo(userMenu.getId());
		applicationUserRepository.deleteById(userMenu.getId());

		// Test Menu
		Long menuId = menu.getId();
		assertThat(menuId).isNotNull();
		Optional<Menu> testMenu = applicationMenuRepository.findById(menuId);
		assertThat(testMenu).isNotNull();

		// Delete Data
		applicationMenuRepository.deleteMealFromRepository(menuId);
		menu.setMealType(null);

		menu.setPatient(null);
		applicationMenuRepository.save(menu);

		applicationMenuRepository.deleteById(menuId);

		userMenu.deletePatient(testPatient);
		applicationUserRepository.save(userMenu);
		applicationUserRepository.deletePatientFromRepository(testPatient.getUuid());
		applicationUserRepository.deleteById(userMenu.getId());

		applicationMealRepository.deleteMealById(testMeal.getId());
		applicationFoodRepository.deleteFoodFromRepository(testFood1.getId());
		applicationFoodRepository.deleteFoodFromRepository(testFood2.getId());
	}

	@Test
	void TestFood(){
		// Create data
		NutritionFacts testNutritionFacts = new NutritionFacts(1.1, 2.2, 3.3,
				4.4, 5.5);
		Food testFood1 = new Food("Arroz branco", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts);
		Food testFood2 = new Food();
		Food dummyFood = new Food();

		// Set examples
		testFood2.setFoodName("Arroz Carioca");
		testFood2.setFoodGroup("Gãos");
		testFood2.setMeasureTotalGrams(23.0);
		testFood2.setMeasureType("Colher tipo concha");
		testFood2.setMeasureAmount(1);
		testFood2.setNutritionFacts(testNutritionFacts);

		testFood2.setCalories(1.2);
		testFood2.setProteins(2.3);
		testFood2.setCarbohydrates(3.4);
		testFood2.setLipids(4.5);
		testFood2.setFiber(5.6);

		applicationFoodRepository.save(testFood1);
		applicationFoodRepository.save(testFood2);
		applicationFoodRepository.save(dummyFood);

		// Food tests
		Food foodFound = applicationFoodRepository.getFoodByFoodName("Arroz branco");
		assertThat(testFood1.getId()).isEqualTo(foodFound.getId());

		foodFound = applicationFoodRepository.getFoodById(testFood2.getId());
		assertThat(testFood2.getId()).isEqualTo(foodFound.getId());

		List<Food> foodList = applicationFoodRepository.findFoodByFoodNameContainingAndCustomIsFalse("Arroz");
		for (Food someFood : foodList){
			assertThat(someFood).isNotNull();
		}
		assertThat(testFood1.getId()).isEqualTo(foodList.get(0).getId());
		assertThat(testFood2.getId()).isEqualTo(foodList.get(1).getId());

		// Delete data
		applicationFoodRepository.deleteFoodFromRepository(testFood1.getId());
		applicationFoodRepository.deleteFoodFromRepository(testFood2.getId());
		applicationFoodRepository.deleteFoodFromRepository(dummyFood.getId());

	}

	@Test
	void TestMeal(){
		// Create foods
		NutritionFacts testNutritionFacts = new NutritionFacts(1.1, 2.2, 3.3,
				4.4, 5.5);
		Food testFood1 = new Food("Arroz branco", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts);
		Food testFood2 = new Food("Arroz carioca", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts);
		Food dummyFood = new Food();
		applicationFoodRepository.save(testFood1);
		applicationFoodRepository.save(testFood2);
		applicationFoodRepository.save(dummyFood);

		// Creat Meals
		List<Food> foodList = new ArrayList<Food>();
		foodList.add(testFood1);
		foodList.add(dummyFood);

		Meal testMeal = new Meal(MealType.DINNER, foodList);
		Meal dummyMeal = new Meal();

		// Adders and removers examples
		testMeal.addFood(testFood2);
		testMeal.removeFood(dummyFood);

		applicationMealRepository.save(testMeal);
		applicationMealRepository.save(dummyMeal);
		// Meal tests
		Meal mealFound = applicationMealRepository.getMealById(testMeal.getId());
		assertThat(mealFound.getId()).isNotNull();
		assertThat(mealFound.getMealType()).isEqualTo(testMeal.getMealType());

		// Delete data
		applicationMealRepository.deleteMealById(testMeal.getId());
		applicationMealRepository.deleteMealById(dummyMeal.getId());
		applicationFoodRepository.deleteFoodFromRepository(testFood1.getId());
		applicationFoodRepository.deleteFoodFromRepository(testFood2.getId());
		applicationFoodRepository.deleteFoodFromRepository(dummyFood.getId());
	}

	@Test
	void OverallTest(){
		// Create foods
		NutritionFacts testNutritionFacts1 = new NutritionFacts(1.1, 2.2, 3.3,
				4.4, 5.5);
		NutritionFacts testNutritionFacts2 = new NutritionFacts(1.1, 2.2, 3.3,
				4.4, 5.5);
		Food testFood1 = new Food("Arroz branco", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts1);
		Food testFood2 = new Food("Arroz carioca", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts2);

		applicationFoodRepository.save(testFood1);
		applicationFoodRepository.save(testFood2);

		// Creat Meals
		List<Food> foodList = new ArrayList<Food>();
		foodList.add(testFood1);
		Meal testMeal = new Meal(MealType.DINNER, foodList);
		testMeal.addFood(testFood2);
		applicationMealRepository.save(testMeal);

		// Meal tests
		Meal mealFound = applicationMealRepository.getMealById(testMeal.getId());
		assertThat(mealFound.getId()).isNotNull();
		assertThat(mealFound.getMealType()).isEqualTo(testMeal.getMealType());

		// Add Nutricionista
		UserCredentials userMenu = new UserCredentials("Nutricionista","test@email.com","senhaTest","Nutricionista","M");

		// Add Custom Food
		Food customFood = new Food(userMenu, testFood1);
		Food createdFood = new Food(userMenu, "Açaí", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts1);
		applicationFoodRepository.save(customFood);
		applicationFoodRepository.save(createdFood);

		testMeal.addFood(customFood);
		testMeal.addFood(createdFood);
		applicationMealRepository.save(testMeal);

		// Add data
		Patient testPatient = new Patient();
		testPatient.setName("Toso");
		testPatient.setCorporalMass((float)89.3);
		testPatient.setCpf("123456");
		testPatient.calculateMethabolicRate(Constants.TINSLEY);
		userMenu.setPatient(testPatient);

		Patient testPatient2 = new Patient();
		testPatient2.setName("Foo");
		testPatient2.setCorporalMass((float)89.3);
		testPatient2.setCpf("123466");
		testPatient2.calculateMethabolicRate(Constants.TINSLEY);
		userMenu.setPatient(testPatient2);

		applicationUserRepository.save(userMenu);

		Menu menu = new Menu(testMeal, testPatient, testFood2, 150f);
		menu.addPortion(customFood, 200f);
		applicationMenuRepository.save(menu);

		Menu menu2 = new Menu(testMeal, testPatient2, createdFood, 150f);
		applicationMenuRepository.save(menu2);

		// Test Menu
		Long menuId = menu.getId();
		assertThat(menuId).isNotNull();
		Optional<Menu> test_menu = applicationMenuRepository.findById(menuId);
		assertThat(test_menu).isNotNull();

		// Delete data
		applicationFoodRepository.delete(customFood);
		applicationFoodRepository.delete(createdFood);
		applicationMealRepository.deleteMealById(testMeal.getId());
		applicationFoodRepository.deleteFoodFromRepository(testFood1.getId());
		applicationFoodRepository.deleteFoodFromRepository(testFood2.getId());
		applicationMenuRepository.deleteMealFromRepository(menu.getMealType().getId());
		menu.setMealType(null);
		menu.setPatient(null);
		applicationMenuRepository.save(menu);
		applicationMenuRepository.deleteById(menuId);
		applicationMenuRepository.deleteById(menu2.getId());
		userMenu.deletePatient(testPatient);
		applicationUserRepository.save(userMenu);
		applicationUserRepository.deletePatientFromRepository(testPatient.getUuid());
		applicationUserRepository.deletePatientFromRepository(testPatient2.getUuid());
		applicationUserRepository.deleteById(userMenu.getId());
	}

	@Test
	void foodRestrictionTest()
	{
		NutritionFacts testNutritionFacts = new NutritionFacts(1.1, 2.2, 3.3,
				4.4, 5.5);
		Food testFood1 = new Food("Arroz branco", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts);
		Food testFood2 = new Food("Arroz carioca", "Grãos", 23.9,
				"Colher de sopa", 5, testNutritionFacts);
		Food dummyFood = new Food();
		applicationFoodRepository.save(testFood1);
		applicationFoodRepository.save(testFood2);
		applicationFoodRepository.save(dummyFood);
	}
}
