
# Rotas

<!-- vscode-markdown-toc -->
* [Autorização / Token](#AutorizaoToken)
* [REST](#REST)
	* [[POST] Registro /user/register/](#POSTRegistrouserregister)
	* [[POST] Login /user/login/](#POSTLoginuserlogin)
	* [[POST] Refresh do token /user/token/refresh/](#POSTRefreshdotokenusertokenrefresh)
	* [[POST] Geração de PDF /diet/generate-PDF/](#POSTGeraodePDFdietgenerate-PDF)
	* [[POST] Enviar PDF por email /diet/send-email-PDF/{PatientId}/](#POSTEnviarPDFporemaildietsend-email-PDFPatientId)
	* [[POST] Sugestão de dieta /diet/generate/{patientId}/{mealNumber}/](#POSTSugestodedietadietgeneratepatientIdmealNumber)
	* [[POST] Substituição de refeições /diet/replace/{patientId}/{mealNumber}/](#POSTSubstituioderefeiesdietreplacepatientIdmealNumber)
* [GraphQL](#GraphQL)
	* [Food](#Food)
		* [listFood](#listFood)
		* [listFoodPaginated](#listFoodPaginated)
		* [searchFood](#searchFood)
		* [getUnits](#getUnits)
		* [getMeal](#getMeal)
		* [listMealsThatAFoodBelongsTo](#listMealsThatAFoodBelongsTo)
		* [createFood](#createFood)
		* [customizeFood](#customizeFood)
		* [removeFood](#removeFood)
		* [startMeals](#startMeals)
		* [addFoodToMeal](#addFoodToMeal)
		* [removeFoodFromMeal](#removeFoodFromMeal)
		* [setMeals](#setMeals)
	* [Menu](#Menu)
		* [getMenu](#getMenu)
		* [getAllMenusForPatient](#getAllMenusForPatient)
		* [getMenusForMeal](#getMenusForMeal)
		* [addMenu](#addMenu)
		* [removeMenu](#removeMenu)
		* [editMenu](#editMenu)
	* [Patients](#Patients)
		* [getPatientInfo](#getPatientInfo)
		* [getAllPatients](#getAllPatients)
		* [getPatientRecords](#getPatientRecords)
		* [getSingleRecord](#getSingleRecord)
		* [createPatientRecord](#createPatientRecord)
		* [removePatientRecord](#removePatientRecord)
		* [updatePatientRecord](#updatePatientRecord)
		* [updateFoodRestrictions](#updateFoodRestrictions)
		* [removeFoodRestrictions](#removeFoodRestrictions)
		* [removeAllFoodRestrictions](#removeAllFoodRestrictions)
		* [createPatient](#createPatient)
		* [removePatient](#removePatient)
		* [updatePatient](#updatePatient)

<!-- vscode-markdown-toc-config
	numbering=false
	autoSave=true
	/vscode-markdown-toc-config -->
<!-- /vscode-markdown-toc -->

## <a name='AutorizaoToken'></a>Autorização / Token
Instruções para acessar sites (que não sejam login e registro):

Utilizar o token do usuário como header dessa forma: `"Authorization: Port <TOKEN>"`

Sendo `<TOKEN>` o token recebido como resposta ao utilizar a rota de login.

O token do usuário tem validade de cinco dias. Caso ele vença, utilizar o token de refresh para solicitar um novo (de acordo com o endereço mais abaixo). O token de refresh tem validade de dez dias.


## <a name='REST'></a>REST

### <a name='POSTRegistrouserregister'></a>[POST] Registro /user/register/
Envio de JSON com campos:
```
{
    "username": "nome do usuario",
    "password1": "senha",
    "password2": "repetir a mesma senha",
    "firstName": "primeiro nome",
    "lastName": "ultimo nome",
    "email": "emaildapessoal@email.com"
}
```

Exemplo:
```
{
    "username": "bruno",
    "password1": "#Um4S3nh4B04",
    "password2": "#Um4S3nh4B04",
    "firstName": "Bruno",
    "lastName": "Moraes Perereira",
    "email": "bruno@email.com"
}
```

Retorno:
```
{
    "refresh": "token de refresh",
    "token": "token do usuario",
    "user": {
        "username": "nome do usuario",
        "id": "id da base de dados - eh uma string",
        "firstName": "primeiro nome",
        "lastName": "ultimo nome",
        "email": "emaildapessoal@email.com"
    }
}
```

### <a name='POSTLoginuserlogin'></a>[POST] Login /user/login/
Envio de JSON com campos:

```
{
    "usernameOrEmail": "nome do usuario ou email",
    "password": "senha"
}
```

Exemplo:
```
{
    "usernameOrEmail": "bruno",
    "password": "#Um4S3nh4B04"
}
```

Retorno:
```
{
    "refresh": "token de refresh",
    "token": "token do usuario",
    "user": {
        "username": "nome do usuario",
        "id": "id da base de dados - eh uma ",
        "firstName": "primeiro nome",
        "lastName": "ultimo nome",
        "email": "emaildapessoal@email.com"
    }
}
```

### <a name='POSTRefreshdotokenusertokenrefresh'></a>[POST] Refresh do token /user/token/refresh/
Envio de JSON com campos:

```
{
    "refresh": "token de refresh"
}
```

Exemplo:
```
{
    "refresh": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoIjp0cnVlLCJpZCI6MTgsImV4cCI6MTU5NTE3MjIyNiwidXNlcm5hbWUiOiJvY2ltYXIifQ.xt6eGUbyDccnd5J0j9fNqsffJ62cwoDd8x7yjLEFfjA"
}
```

Retorno:
```
{
    "username": "nome do usuario",
    "id": "id da base de dados",
    "firstName": "primeiro nome",
    "lastName": "ultimo nome",
    "email": "emaildapessoal@email.com"
}
```

### <a name='POSTGeraodePDFdietgenerate-PDF'></a>[POST] Geração de PDF /diet/generate-PDF/
Envio de JSON com campos:

"Refeição": `[ Lista de opções ]`
Cada opção contém: `[Lista de porções]`
Cada porção contém: `{"foodId": ID da comida (String), "Porção": inteiro}`

Todas as refeições precisam estar no JSON. Caso uma refeição não seja incluída no cardápio, enviar uma lista vazia, como no exemplo abaixo em "workoutSnack":

Exemplo:
```
{
    "breakfast": [
        {
            "portions": [
                {
                    "foodId": "ef3bcc17085b4b2dacf2d41f262f4d33",
                    "portion": 1
                },
                {
                    "foodId": "f6bb76e0e06f4fe5b8d41d958c942a48",
                    "portion": 5
                }
            ]
        },
        {
            "portions": [
                {
                    "foodId": "fa47fdb959b44675adbfbc315fdc9fef",
                    "portion": 2
                }
            ]
        }
    ],
    "morningSnack": [
        {
            "portions": [
                {
                    "foodId": "94c0c57b98194c8ea8b40e6392d978be",
                    "portion": 3
                }
            ]
        }
    ],
    "lunch": [
        {
            "portions": [
                {
                    "foodId": "cac26dfbe1bd4d05aa357832b043e200",
                    "portion": 100
                },
                {
                    "foodId": "d9fed10b964142acbce8ca10c63d556d",
                    "portion": 200
                }
            ]
        }
    ],
    "afternoonSnack": [
        {
            "portions": [
                {
                    "foodId": "fcb0d9b0819e4e80a16d07acb0cd2089",
                    "portion": 4
                }
            ]
        }
    ],
    "workoutSnack": [
        
    ],
    "dinner": [
        {
            "portions": [
                {
                    "foodId": "d81e3c40c9e541d4be50a92b9a53543b",
                    "portion": 2
                }
            ]
        }
    ]
}
```

Retorno:
```
{
    "base64File": "arquivo pdf codificado em base64"
}
```


### <a name='POSTEnviarPDFporemaildietsend-email-PDFPatientId'></a>[POST] Enviar PDF por email /diet/send-email-PDF/{PatientId}/

PatientID é o id do paciente {String}

Deve ser enviado um JSON com o mesmo formato da geração do cardápio (Veja o tópico "Gerar PDF do cardapio: (POST)").

Se o envio do email funcionar, será retornado HTTP 200, caso contrario HTTP 417


### <a name='POSTSugestodedietadietgeneratepatientIdmealNumber'></a>[POST] Sugestão de dieta /diet/generate/{patientId}/{mealNumber}/
Para solicitar a sugestão de um cardápio, usa-se a url acima, enviando nela o id do paciente (string) e o número da refeição (0-breakfast, 1-morningSnack, 2-Lunch, 3-AfternoonSnack, 4-preWorkout, 5-dinner).

O Post deve ser feito com um JSON como o abaixo. Se algum dos valores for zero, aquele item será desconsiderado para a sugestão do cardápio

Exemplo:
```
{
   "calories": 270,
   "proteins": 30,
   "carbohydrates": 15,
   "lipids": 10,
   "fiber": 5
}
```

A reposta possui uma lista de comidas e uma lista com as quantidades de cada comida. A relação se faz com a ordem: quantidade da primeira comida = primeiro item da lista de quantidades.

Exemplo de Resposta:
```
{
   "suggestions": [
       {
           "uuid": "6fc8831abf78457c9f2fea5aec1be168",
           "foodName": "Macarrão Integral",
           "foodGroup": "Cereais, pães e massas",
           "measureTotalGrams": 0.0,
           "measureType": "Colher de sopa",
           "measureAmount": 4,
           "nutritionFacts": {
               "calories": 146.0,
               "proteins": 12.0,
               "carbohydrates": 1.75,
               "lipids": 29.4,
               "fiber": 2.3
           }
       },
       {
           "uuid": "756465cf7e8449db99f07f7c596bc7ba",
           "foodName": "Pupunha",
           "foodGroup": " ",
           "measureTotalGrams": 0.0,
           "measureType": "Unidade",
           "measureAmount": 2,
           "nutritionFacts": {
               "calories": 20.0,
               "proteins": 1.0,
               "carbohydrates": 1.0,
               "lipids": 2.0,
               "fiber": 0.8
           }
       }
   ],
   "quantities": [
       2.0,
       1.0
   ]
}
```

### <a name='POSTSubstituioderefeiesdietreplacepatientIdmealNumber'></a>[POST] Substituição de refeições /diet/replace/{patientId}/{mealNumber}/

A sintaxe para solicitar um url de substituições de cardápios é a mesma do que a de solicitar uma sugestao de cardapio. É necessário enviar no url o id do paciente e o número da refeição (explicado em “Sugestão de alimentos a partir de valores nutritivos).

O corpo da requisição deve conter um corpo com duas listas.

* List<String> foods : contém os ids dos alimentos a serem substituídos.
* List<Double> quantities: contém as quantidades de cada alimentos.

O correlacionamento das duas listas é feito por ordem.

Um exemplo do envio está mostrado abaixo:
```
{
   "foods": ["5b4f1bfb6cc741918c3da1522c57b50d", "3f4d28aeff994f788458b857f32d6543"],
   "quantities": [
       0.5,
       1.0
   ]
}
```

O retorno da solicitação é uma lista de sugestões e uma lista com suas quantidades. Novamente, o correlacionamento das duas listas é por ordem. Abaixo há um exemplo de retorno.
```
{
   "suggestions": [
       {
           "uuid": "5b4f1bfb6cc741918c3da1522c57b50d",
           "foodName": "Feijão Carioca",
           "foodGroup": "Leguminosas",
           "measureTotalGrams": 0.0,
           "measureType": "Colher de sopa",
           "measureAmount": 5,
           "nutritionFacts": {
               "calories": 76.0,
               "proteins": 4.8,
               "carbohydrates": 0.5,
               "lipids": 15.0,
               "fiber": 8.5
           }
       },
       {
           "uuid": "7442f5a87b9f47128e9ce43af8e094ec",
           "foodName": "Queijo Cottage",
           "foodGroup": "Leite e derivados",
           "measureTotalGrams": 0.0,
           "measureType": "Colher de sopa",
           "measureAmount": 3,
           "nutritionFacts": {
               "calories": 98.0,
               "proteins": 11.0,
               "carbohydrates": 4.3,
               "lipids": 3.4,
               "fiber": 0.0
           }
       }
   ],
   "quantities": [
       0.5,
       2.0
   ]
}
```

## <a name='GraphQL'></a>GraphQL

*Bizu*: Os campos com exclamação `!` são obrigatórios, os demais podem ser omitidos se não forem relevantes para a _query_;

Todas as rotas pelo GraphQL usam a rota `/graphql/get/`

### <a name='Food'></a>Food

#### <a name='listFood'></a>listFood
Rota:
``` 
query {
    listFood {
        uuid,
        foodName,
        foodGroup,
        custom,
        created,
        measureTotalGrams,
        measureType,
        measureAmount,
        nutritionFacts {
          calories,
          proteins,
          carbohydrates,
          lipids,
          fiber
        }
    }
}
```

Exemplo:
```
query {
    listFood {
        uuid,
        foodName,
        foodGroup,
        custom,
        created,
        nutritionFacts {
          calories,
          proteins,
          carbohydrates,
          lipids,
          fiber
        }
    }
}
```

#### <a name='listFoodPaginated'></a>listFoodPaginated
Rota:
``` 
query {
    listFoodPaginated(indexPage: Int!, sizePage: Int!) {
        uuid,
        foodName,
        foodGroup,
        custom,
        created,
        measureTotalGrams,
        measureType,
        measureAmount,
        nutritionFacts {
          calories,
          proteins,
          carbohydrates,
          lipids,
          fiber
        }
    }
}
```

Exemplo:
```
query {
    listFoodPaginated(indexPage: 0, sizePage: 10) {
        uuid,
        foodName,
        foodGroup,
        custom,
        created,
        nutritionFacts {
          calories,
          proteins,
          carbohydrates,
          lipids,
          fiber
        }
    }
}
```

#### <a name='searchFood'></a>searchFood
Rota:
``` 
query {
    searchFood(partialFoodName: String!, indexPage: Int!, sizePage: Int!) {
        uuid,
        foodName,
        foodGroup,
        custom,
        created,
        measureTotalGrams,
        measureType,
        measureAmount,
        nutritionFacts {
          calories,
          proteins,
          carbohydrates,
          lipids,
          fiber
        }
    }
}
```

Exemplo:
```
query {
    searchFood(partialFoodName: "arr", indexPage: 0, sizePage: 5) {
        uuid,
        foodName,
        foodGroup,
        custom,
        created,
        nutritionFacts {
          calories,
          proteins,
          carbohydrates,
          lipids,
          fiber
        }
    }
}
```

#### <a name='getUnits'></a>getUnits
Rota:
``` 
query {
    getUnits(uuidFood: String!) {
        calories,
        proteins,
        carbohydrates,
        lipids,
        fiber,
    }
}
```

Exemplo:
```
query {
    getUnits(uuidFood: "f6bb76e0e06f4fe5b8d41d958c942a48") {
        calories,
        proteins,
        carbohydrates,
        lipids,
        fiber,
    }
}
```

#### <a name='getMeal'></a>getMeal
Rota:
``` 
query {
    getMeal(mealType: Int!) {
        mealType, 
        foodList{
            uuid
            foodName,
            foodGroup,
            custom,
            created
        }
    }
}
```

Exemplo:
```
query {
    getMeal(mealType: 0) {
        mealType, 
        foodList{
            uuid
            foodName,
            foodGroup,
            custom,
            created
        }
    }
}
```
Opções de `mealType`:
```
BREAKFAST: 0
MORNING_SNACK: 1
LUNCH: 2
AFTERNOON_SNACK: 3
PRE_WORKOUT: 4
DINNER: 5
```
#### <a name='listMealsThatAFoodBelongsTo'></a>listMealsThatAFoodBelongsTo

Rota:
``` 
query {
    listMealsThatAFoodBelongsTo(uuidFood: String!)
}
```

Exemplo:
```
query {
    listMealsThatAFoodBelongsTo(uuidFood: "3743096a5b3d4fc9991af06182a9cbd6")
}
```

#### <a name='createFood'></a>createFood
Rota:
```
mutation {
    createFood( foodInput: {
                    foodName: String!
                    foodGroup: String!
                    measureTotalGrams: Float!
                    measureType: String!
                    measureAmount: Float!
                },
                nutritionInput: {
                    calories: Float!
                    proteins: Float!
                    carbohydrates: Float!
                    lipids: Float!
                    fiber: Float!
                })
}
```

Exemplo:
```
mutation {
    createFood(foodInput: {
                    foodName: "Pudim de Passas", 
                    foodGroup: "Doces",
                    measureTotalGrams: 21.0,
                    measureType: "Medidas Carteadas",
                    measureAmount: 110.0
                },
                nutritionInput: {
                    calories: 1.0,
                    proteins: 2.0,
                    carbohydrates: 3.0,
                    lipids: 4.0,
                    fiber: 5.0    
                })
}
```


#### <a name='customizeFood'></a>customizeFood
Rota:
```
customizeFood(uuidFood: String!,
                customInput: {
                    measureTotalGrams: Float!
                    measureType: String!
                    measureAmount: Float!
                }, 
              nutritionInput: {
                    calories: Float!
                    proteins: Float!
                    carbohydrates: Float!
                    lipids: Float!
                    fiber: Float!
              })
```

Exemplo:
```
mutation {
    customizeFood(uuidFood: "6584956257c94bc892ac8f9506087243",
                    customInput: {
                        measureTotalGrams: 999.9,
                        measureType: "Medidas Carteadas",
                        measureAmount: 999.9 
                    },
                    nutritionInput: {
                        calories: 9.9,
                        proteins: 9.9,
                        carbohydrates: 9.9,
                        lipids: 9.9,
                        fiber: 9.9    
                    })
}
```
#### <a name='removeFood'></a>removeFood
Rota:
```
mutation {
    removeFood (uuidFood: String!)
}
```

Exemplo:
```
mutation {
    removeFood(uuidFood: "3743096a5b3d4fc9991af06182a9cbd6")
}
```

#### <a name='startMeals'></a>startMeals
Descrição:
Cria as meals (refeições) que não existem. Geralmente será usada só em caso de reinicialização da DB ou um delete acidental da meal.

Rota:
``` 
mutation {
    startMeals
}
```

#### <a name='addFoodToMeal'></a>addFoodToMeal
Rota:
```
mutation {
    addFoodToMeal (uuidFood: String!, mealTypes: [Int])
}
```

Exemplo:
```
mutation {
    addFoodToMeal(uuidFood: "3743096a5b3d4fc9991af06182a9cbd6", mealTypes: [5, 2])
}
```
#### <a name='removeFoodFromMeal'></a>removeFoodFromMeal
Rota:
```
mutation {
    removeFoodFromMeal (uuidFood: String!, mealTypes: [Int])
}
```

Exemplo:
```
mutation {
    removeFoodFromMeal(uuidFood: "3743096a5b3d4fc9991af06182a9cbd6", mealTypes: [5, 2])
}
```

#### <a name='setMeals'></a>setMeals
Rota:
```
mutation {
    setMeals (uuidFood: String!, mealTypes: [Int])
}
```

Exemplo:
```
mutation {
    setMeals(uuidFood: "e83debb7bf564126ae7e77ebe20ab1e3", mealTypes: [4, 5])
}
```


### <a name='Menu'></a>Menu

#### <a name='getMenu'></a>getMenu
Retorna um Menu a partir de seu UUID e do UUID do paciente a quem o Menu está relacionado. Os campos no interior da função ```getMenu``` são opcionais, isto é, eles são os valores cujo retorno é desejado.

Rota:
```
query {
   getMenu(uuidMenu: String!, uuidPatient: String!) {
      uuid
      mealType
      portions {
         quantity
         food {
            uuid
            foodName
            # ISTO EH UM COMENTARIO: aqui pode-se por quaisquer campo que se deseja de Food
            nutritionFacts {
               # ISTO EH UM COMENTARIO: campos que se deseja de nutritionFacts
            }
         }
      }
   }
}
```

Os campos disponíveis para Food são: uuid, foodName, foodGroup, measureTotalGrams, measureType, measureAmount, custom, created, nutritionFacts.
Os campos disponíveis para NutritionFacts são: calories, proteins, lipids, fiber.

Exemplo:
```
query {
   getMenu(uuidMenu: "593e5457ff904ba4962e811cefe44dd8", uuidPatient: "3dbd7c50ab814f3abd4d06aab685d4e6") {
      uuid
      mealType
      portions {
         quantity
         food {
            uuid
            foodName
            foodGroup
            nutritionFacts {
               proteins
               calories
            }
         }
      }
   }
}
```

#### <a name='getAllMenusForPatient'></a>getAllMenusForPatient
Retorna todos os menus disponíveis para um paciente. Os campos disponíveis para Food e para NutritionFacts são os mesmos citados em [getMenu](#getMenu).

Rota:
```
query {
   getAllMenusForPatient(uuidPatient: String!) {
      uuid
      mealType
      portions {
         quantity
         food {
            uuid
            measureTotalGrams
            # ISTO EH UM COMENTARIO: aqui pode-se por quaisquer campo que se deseja de Food
            nutritionFacts {
               # ISTO EH UM COMENTARIO: campos que se deseja de nutritionFacts
            }
         }
      }
   }
}

```

Exemplo:
```
query {
   getAllMenusForPatient(uuidPatient: "593e5457ff904ba4962e811cefe44dd8") {
      uuid
      mealType
      portions {
         quantity
         food {
            uuid
            measureTotalGrams
            # ISTO EH UM COMENTARIO: aqui pode-se por quaisquer campo que se deseja de Food
            nutritionFacts {
               # ISTO EH UM COMENTARIO: campos que se deseja de nutritionFacts
            }
         }
      }
   }
}
```
#### <a name='getMenusForRecord'></a>getMenusForRecord
Retorna todos os uuids dos Menus relacionados a uma ficha de paciente.

```
query {
    getMenusForRecord(uuidRecord: String!)
}
```

Exemplo:

```
query {
    getMenusForRecord(uuidRecord: "593e5457ff904ba4962e811cefe44dd8")
    {
        uuid,
        mealType
        //ISTO EH UM COMENTARIO: Essa função somente retorna esses dois parametros
    }
}
```


#### <a name='getMenusForMeal'></a>getMenusForMeal
Retorna todos os Menus de um paciente para uma determinada refeição. Os campos disponíveis para Food e para NutritionFacts são os mesmos citados em [getMenu](#getMenu). Só lembrando que a corresnpondência entre uma refeição e seu número é 0-breakfast, 1-morningSnack, 2-Lunch, 3-AfternoonSnack, 4-preWorkout, 5-dinner.

Rota:
```
query {
   getMenusForMeal(uuidPatient: String!, mealType: Int!) {
      uuid
      mealType
      portions {
         quantity
         food {
            uuid
            measureTotalGrams
            # ISTO EH UM COMENTARIO: aqui pode-se por quaisquer campo que se deseja de Food
            nutritionFacts {
               # ISTO EH UM COMENTARIO: campos que se deseja de nutritionFacts
            }
         }
      }
   }
}
```

Exemplo:
```
query {
   getMenusForMeal(uuidPatient: "593e5457ff904ba4962e811cefe44dd8"), mealType: 5) {
      uuid
      mealType
      portions {
         quantity
         food {
            foodName
            measureTotalGrams
            nutritionFacts {
              fiber
              calories
              proteins
            }
         }
      }
   }
}
```




#### <a name='addMenu'></a>addMenu
Obs.: Retorna o uuid do menu se funcionar ou "ERROR" caso contrário.
Rota:
```
mutation
{
    addMenu(mealType: Int!, uuidRecord: String!, uuidPatient: String!, uuidFoods: [String], quantities: [Float])
}
```

Exemplo:
```
mutation
{
    addMenu(mealType: 3, uuidRecord: "593e5457ff904ba4962e811cefe44dd8", uuidPatient: "5b96c7faacfc4ead8770b07157b5dbd7", uuidFoods: ["3743096a5b3d4fc9991af06182a9cbd6"], quantities: [1.0])
}
```

#### <a name='removeMenu'></a>removeMenu
Rota:
```
mutation
{
    remove(uuidMenu: String!)
}
```

Exemplo:
```
mutation
{
    removeMenu(uuidMenu: "13f9ff7a50aa418ebffc4678d921afd0")
}
```
#### <a name='editMenu'></a>editMenu
Rota:
```
mutation
{
    editMenu(uuidMenu: String!, uuidFoods: [String], quantities: [Float])
}
```

Exemplo:
```
mutation
{
    editMenu(uuidMenu: "18542bee724c4f20a93ddc14f4a79acc", uuidFoods: ["69c009161eda40048dc6be1b93a1793e"], quantities: [2.0])
}
```


### <a name='Patients'></a>Patients

**Duvidas**: 
* O que é o campo `nutritionist`? Deve conter o nome do nutricionista relacionado ao paciente.
* Qual o mapa numério de `ethnicGroup`? 0 para branco/hispânico e 1.1 para afrodescendentes.
* Qual o mapa numério de `biologicalSex`? Variavel do tipo inteiro, onde 0 = feminino ou 1 = masculino.
* Qual o mapa numério de `physicalActivityLevel`? 

#### <a name='getPatientInfo'></a>getPatientInfo
Rota:
OBS: Todos os itens do get são opcionais.
```
query {
    getPatientInfo(uuidPatient: String!) {
        uuid,
        name,
	foodRestrictions,
        ethnicGroup,
        email,
        dateOfBirth,
        nutritionist,
        cpf,
        biologicalSex
    }
}
```

Exemplo:
OBS: Todos os itens do get são opcionais.
```
query {
    getPatientInfo(uuidPatient: "TODO") {
        uuid,
        name,
	foodRestrictions,
        ethnicGroup,
        email,
        dateOfBirth,
        nutritionist,
        cpf,
        biologicalSex
    }
}
```

#### <a name='getAllPatients'></a>getAllPatients

Rota:
OBS: Todos os itens do get são opcionais.
```
query {
    getAllPatients(indexPage: Int!, sizePage: Int!) {
        uuid,
        name,
	foodRestrictions,
        ethnicGroup,
        email,
        dateOfBirth,
        nutritionist,
        cpf,
        biologicalSex
    }
}
```

Exemplo:
```
query {
    getAllPatients(indexPage: 0, sizePage: 5) {
        uuid,
        name,
	foodRestrictions,
        ethnicGroup,
        email,
        dateOfBirth,
        nutritionist,
        cpf,
        biologicalSex
    }
}
```

#### <a name='getPatientRecords'></a>getPatientRecords
Rota:
OBS: Todos os itens do get são opcionais.
```
query {
    getPatientRecords(uuidPatient: String!, indexPage: Int!, sizePage: Int!) {
        uuid,
        uuidPatient,
        anamnesis,
	exam,
	corporalMass,
	methodBodyFat,
	methodMethabolicRate,
        height,
        abdominal,
        isAthlete,
        age,
        physicalActivityLevel,
        observations,
        subscapular,
        triceps,
        biceps,
        chest,
        axillary,
        supriailiac,
        thigh,
        calf,
        waistCirc,
        abdominalCirc,
        hipsCirc,
        rightArmCirc,
        thighCirc,
        calfCirc,
        muscularMass,
        corporalDensity,
        bodyFat,
        methabolicRate,
        dateModified,
        energyRequirements
    }
}
```

Exemplo:
OBS: Todos os itens do get são opcionais.
```
query {
    getPatientRecords(uuidPatient: "TODO", indexPage: 0, sizePage: 5) {
        uuid,
        uuidPatient,
        anamnesis,
	exam,
	corporalMass,
	methodBodyFat,
	methodMethabolicRate,
        height,
        abdominal,
        isAthlete,
        age,
        physicalActivityLevel,
        observations,
        subscapular,
        triceps,
        biceps,
        chest,
        axillary,
        supriailiac,
        thigh,
        calf,
        waistCirc,
        abdominalCirc,
        hipsCirc,
        rightArmCirc,
        thighCirc,
        calfCirc,
        muscularMass,
	dateModified
        //COMENTARIO: Adicionar menus aqui nao vai retornar nada
    }
}
```

#### <a name='getSingleRecord'></a>getSingleRecord
Rota:
OBS: Todos os itens do get são opcionais.
```
query {
    getSingleRecord(uuidRecord: String!) {
        uuid,
        uuidPatient,
        anamnesis,
    	exam,
        corporalMass,
        methodBodyFat,
        methodMethabolicRate,
        height,
        abdominal,
        isAthlete,
        age,
        physicalActivityLevel,
        observations,
        subscapular,
        triceps,
        biceps,
        chest,
        axillary,
        supriailiac,
        thigh,
        calf,
        waistCirc,
        abdominalCirc,
        hipsCirc,
        rightArmCirc,
        thighCirc,
        calfCirc,
        muscularMass,
        corporalDensity,
        bodyFat,
        methabolicRate,
        dateModified,
        energyRequirements,
        menus //COMENTARIO: Isso retorna uma lista com os uuids dos cardapios associados
    }
}
```

Exemplo:
OBS: Todos os itens do get são opcionais.
```
query {
    getSingleRecord(uuidRecord: "TODO") {
        uuid,
        uuidPatient,
        anamnesis,
	exam,
	corporalMass,
	methodBodyFat,
	methodMethabolicRate,
        height,
        abdominal,
        isAthlete,
        age,
        physicalActivityLevel,
        observations,
        subscapular,
        triceps,
        biceps,
        chest,
        axillary,
        supriailiac,
        thigh,
        calf,
        waistCirc,
        abdominalCirc,
        hipsCirc,
        rightArmCirc,
        thighCirc,
        calfCirc,
        muscularMass,
        corporalDensity,
        bodyFat,
        methabolicRate,
        dateModified,
        energyRequirements,
        menus
    }
}
```

#### <a name='createPatientRecord'></a>createPatientRecord
Rota:
OBS: O input, bem como suas variáveis que o compõem, são opcionais. Favor enviar todos os dados necessários
para o cálculo de gordura corporal e de energia bem como os métodos que serão utilizados.

Caso envie itens como energyRequirements ou corporalDensity, por exemplo, eles serão estabelecidos como padrões
e o cálculo deles não será realizado.
```
mutation {
    createPatientRecord(uuidPatient: String!,
               input: {
                        uuid,
			uuidPatient,
			anamnesis,
			exam,
			corporalMass,
			methodBodyFat,
			methodMethabolicRate,
			height,
			abdominal,
			isAthlete,
			age,
			physicalActivityLevel,
			observations,
			subscapular,
			triceps,
			biceps,
			chest,
			axillary,
			supriailiac,
			thigh,
			calf,
			waistCirc,
			abdominalCirc,
			hipsCirc,
			rightArmCirc,
			thighCirc,
			calfCirc,
			muscularMass,
			corporalDensity,
			bodyFat,
			methabolicRate,
			dateModified,
			energyRequirements
                 })
}
```

Exemplo:
```
mutation {
    createPatientRecord(uuidPatient: "d0738c5d83994872a71dfbcec704e2e8",
                        input: {
                            methodBodyFat: "faulkner",
			    methodMethabolicRate: "tinsley",
                            corporalMass: 104.0,
                            height: 1.83,
                            abdominal: 110.0,
                            isAthlete: false,
                            age: 38,
                            physicalActivityLevel: 1.0,
                            observations: "Fumante",
                            waistCirc: 1.0,
                            abdominalCirc: 1.0,
                            hipsCirc:   1.0,
                            rightArmCirc: 1.0,
                            methabolicRate: 1.0,
                        })
}
```

#### <a name='removePatientRecord'></a>removePatientRecord
Rota:
```
mutation {
    removePatientRecord(uuidPatientRecord: String!)
}
```

Exemplo:
```
mutation {
    removePatientRecord(uuidPatientRecord: "760668c9a8a949139d8ef7ccb7e23043")
}
```

#### <a name='updatePatientRecord'></a>updatePatientRecord
Rota:
OBS: O input, bem como suas variáveis que o compõem, são opcionais.
OBS: É necessario passar o método para selecionar a forma que os cálculos serão realizados.
```
mutation {
    updatePatientRecord(uuidPatient: String!,
                        uuidPatientRecord: String!,
                        input: {
                            	    anamnesis: String,
				    exam: String,
				    methodBodyFat: String,
				    methodMethabolicRate: String,
				    corporalMass: Float,
				    height: Float,
				    abdominal: Float,
				    isAthlete: Boolean,
				    age: Int,
				    physicalActivityLevel: Float,
				    observations: String,
				    subscapular: Float,
				    triceps: Float,
				    biceps: Float,
				    chest: Float,
				    axillary: Float,
				    supriailiac: Float,
				    thigh: Float,
				    calf: Float,
				    waistCirc: Float,
				    abdominalCirc: Float,
				    hipsCirc:   Float,
				    rightArmCirc: Float,
				    thighCirc:  Float,
				    calfCirc:   Float,
				    muscularMass: Float,
				    corporalDensity: Float,
				    bodyFat:    Float,
				    methabolicRate: Float,
				    energyRequirements: Float
                        })
}
```

Exemplo:
```
mutation {
    updatePatientRecord(uuidPatient: "0987235abce35524",
                        uuidPatientRecord: "760668c9a8a949139d8ef7ccb7e23043",
                        input: {
                            corporalMass: 104.0,
                            height: 1.83,
                            abdominal: 110.0,
                            isAthlete: false,
                            age: 38,
                            physicalActivityLevel: 1.0,
                            observations: "Fumante",
                            subscapular: 1.0,
                            triceps: 1.0,
                            biceps: 1.0,
                            chest: 1.0,
                            axillary: 1.0,
                            supriailiac: 1.0,
                            thigh: 1.0,
                            calf: 1.0,
                            waistCirc: 1.0,
                            abdominalCirc: 1.0,
                            hipsCirc:   1.0,
                            rightArmCirc: 1.0,
                            thighCirc:  1.0,
                            calfCirc:   1.0,
                            muscularMass: 1.0,
                            corporalDensity: 1.0,
                            bodyFat:    1.0,
                            methabolicRate: 1.0,
                            energyRequirements: 1.0,
                        })
}
```


#### <a name='updateFoodRestrictions'></a>updateFoodRestrictions
Rota:
Não precisa mais de ser utilizado, se passar o foodRestrictions no updatePatient já funciona da mesma forma
```
mutation{
   updateFoodRestrictions(uuidPatient: String!, uuidFoods: [String])
}

```

Exemplo:
```
mutation {
    updateFoodRestrictions(  uuidPatient: "ad12e11adasdas12321213212asasd",
                             uuidFoods: ["12","1","12"])
}
```

#### <a name='removeFoodRestrictions'></a>removeFoodRestrictions
Rota:
```
mutation{
   removeFoodRestrictions(uuidPatient: String!, uuidFoods: [String])
}

```

Exemplo:
```
mutation {
    removeFoodRestrictions(  uuidPatient: "ad12e11adasdas12321213212asasd",
                             uuidFoods: ["12","1","12"])
}
```

#### <a name='removeAllFoodRestrictions'></a>removeAllFoodRestrictions
Rota:
```
mutation{
   removeAllFoodRestrictions(uuidPatient: String!)
}

```

Exemplo:
```
mutation {
    removeAllFoodRestrictions( uuidPatient: "ad12e11adasdas12321213212asasd")
}
```


#### <a name='createPatient'></a>createPatient
Rota:
```
mutation {
    createPatient(  input: {
                        name:  String,
    			ethnicGroup: Float, // 0 para branco/hispânico e 1.1 para afrodescendentes.
    			foodRestrictions: [String],
    			email: String,
    			dateOfBirth: String,
    			nutritionist: String, // Deve conter o nome do nutricionista relacionado ao paciente.
    			cpf: String,
    			biologicalSex: Int // 0 = feminino ou 1 = masculino.
			
                    })
}
```

Exemplo:
```
mutation {
    createPatient(input: {
                        name:  "Luís Iago José Lima",
                        ethnicGroup: 0,
			foodRestrictions: ["12","125","158"],
                        email: "luisiagojoselima@metalplasma.com.br",
                        dateOfBirth: "21/01/1957",
                        nutritionist: "Ocimar",
                        cpf: "412.281.778-13",
                        biologicalSex: 0,
                    })
}
```

#### <a name='removePatient'></a>removePatient
Rota:
```
mutation {
    removePatient(  uuidPatient: String!)
}
```

Exemplo:
```
mutation {
    removePatient(  uuidPatient: "d0738c5d83994872a71dfbcec704e2e8")
}
```

#### <a name='updatePatient'></a>updatePatient
Rota:
```
mutation {
    updatePatient(  uuidPatient: String!,
                    input: {
                        name:  String,
                        ethnicGroup: Float, // 0 para branco/hispânico e 1.1 para afrodescendentes.
                        email: String,
			foodRestrictions: [String],
                        dateOfBirth: String,
                        nutritionist: String, // Deve conter o nome do nutricionista relacionado ao paciente.
                        cpf: String,
                        biologicalSex: Int, // 0 = feminino ou 1 = masculino.
                    })
}
```

Exemplo:
```
mutation {
    updatePatient(  uuidPatient: "d0738c5d83994872a71dfbcec704e2e8", 
                    input: {
                        name:  "Francisco Carlos Eduardo Alves",
                        ethnicGroup: 1.1,
			foodRestrictions: ["12","125","158"],
                        email: "franciscoalves@imobideal.com",
                        dateOfBirth: "08/02/1954",
                        nutritionist: "Ocimar",
                        cpf: "051.223.837-58",
                        biologicalSex: 0,
                    })
}
```
