# ZedkaShop
---
## **Оглавление**
- [1. Введение](#intro)
- [2. Требования пользователя](#user_requirements)
  - [2.1 Программные интерфейсы](#interfaces)
  - [2.2 Интерфейс пользователя](#ui)
  - [2.3 Характеристики пользователей](#user_characteristics)
  - [2.4 Предположения и зависимости](#assumptions)
- [3. Системные требования](#system_requirements)
  - [3.1 Функциональные требования](#functional_requirements)
  - [3.2 Нефункциональные требования](#non_functional_requirements)
    
<a name="intro"></a>
## **1. Введение**
**Название проекта:** ZedkaShop

**Технологии и инструменты разработки:**

• Язык разработки: Kotlin.

• Фреймворк для создания UI: XML.

• Среда разработки: Android Studio.

**Описание проекта:**  
Проект представляет собой мобильное приложение для магазина специальной экипировки ZedkaShop. Пользователи смогут: просматривать ассортимент экипировки, добавлять товары в корзину и оформлять заказы.

**Границы проекта:** 
Приложение не включает в себя: функции онлайн-консультаций, видеоуроки по использованию экипировки, финансовый учёт и управление личными финансами.


<a name="user_requirements"></a>
## **2. Требования пользователя**

<a name="interfaces"></a>
### **2.1 Программные интерфейсы:**

Продукт будет взаимодействовать с несколькими внешними системами:

• Firebase Authentication для авторизации пользователей и хранения данных о клиентах и администраторах.

• Firebase Realtime Database для динамического хранения и синхронизации данных о товарах, категориях и заказах в реальном времени.

• Payment Gateway API для обработки онлайн-платежей и управления транзакциями.


<a name="ui"></a>
### **2.2 Интерфейс пользователя:**

Приложение будет содержать следующие основные интерфейсы:

• Окно загрузки Представляет собой SplashScreen, во время которого происходит подгрузка приложения.

  <img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/SplashScreen.png" alt="Окно загрузки" width="300">

• Главная страница: Представляет список товаров из категорий наиболее популярные и товары с наибольшим рейтингом.

  <img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/HomePage.png" alt="Главная страница" width="300">

• Каталог: Представляет ассортимент специальной экипировки, доступной для покупки. Пользователи могут просматривать товары, читать их описания, цены и характеристики.

<img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/CataloguePage.png" alt="Каталог" width="300">

• Фильтр:Позволяет пользователям уточнять поиск экипировки по различным параметрам, таким как размер, цена и производитель. Это помогает быстрее находить нужные товары.

<img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/FilterPage.png" alt="Фильтр" width="300">

• Корзина:Содержит список выбранной экипировки, которую пользователь планирует приобрести. Здесь можно просматривать и изменять количество товаров, а также удалять ненужные позиции перед оформлением заказа.

  <img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/CartPage.png" alt="Корзина" width="300">

• Избранное: Позволяет пользователям сохранять понравившиеся товары для быстрого доступа в будущем. Удобно для сравнения и принятия решений о покупке.

<img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/FavPage.png" alt="Избранное" width="300">

• Профиль: Страница для управления личной информацией пользователя, такой как адреса доставки, история заказов и настройки учетной записи. Пользователи могут обновлять свои данные и следить за своими покупками.

 <img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/ProfilePage.png" alt="Профиль" width="300">

• Страница авторизации:Позволяет существующим пользователям входить в свою учетную запись, используя логин и пароль. Это обеспечивает доступ к персонализированным функциям и истории заказов.

<img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/AuthPage.png" alt="Страница авторизации" width="300">
• Страница регистрации: Предоставляет возможность новым пользователям создать учетную запись, чтобы начать делать покупки. Пользователи вводят свои данные для создания профиля и получения доступа к функциям магазина.

 <img src="https://github.com/alwayswnnasleep/ZedkaShop/blob/master/docs/Mockups/RegPage.png" alt="Страница регистрации" width="300">

Пример взаимодействия:

|Действие пользователя	                        | Реакция системы                                                |              
|-----------------------------------------------|----------------------------------------------------------------|        
|Ввод логина и пароля, нажимает "Войти"	        | Проверка данных и вход в систему                               |
|Нажимает на товар в списке	                    | Открывается страница с информацией товаре и его характеристиках|
|Нажимает на кнопку "В корзину"	                | Товар попадает в корзину                                       |
|Нажимает "Оформить заказ"	                    | Переход к странице оформления заказа                           |
|Вводит адрес доставки и выбирает способ оплаты |	Система сохраняет информацию и отображает подтверждение заказа |
|Нажимает "Подтвердить заказ"	                  |Заказ обрабатывается                                            |

<a name="user_specifications"></a>
### **2.3 Характеристики пользователей:**

• Продавцы: Возраст 18-45 лет, с опытом работы в области продаж или обслуживания клиентов. Уровень технической грамотности — средний и выше. Используют приложение для управления запасами и обработки заказов.

• Покупатели: Молодёжь и взрослые от 18 до 50 лет, большинство из которых имеют средний уровень владения смартфонами. Используют приложение для поиска и покупки специальной экипировки, а также для получения информации о новых поступлениях и характеристиках.


<a name="assumptions"></a>
### **2.4 Предположения и зависимости:**

• Приложение будет работать только на устройствах с Android 6.0 и выше.

• Все пользователи должны иметь подключение к интернету для синхронизации данных с облаком.


<a name="system_requirements"></a>
## **3. Системные требования**

<a name="functional_requirements"></a>
### **3.1 Функциональные требования:**

• Приложение должно позволять пользователям (администраторам) покупать(продавать) специальную экипировку.

• Пользователи должны иметь возможность добавлять и редактировать информацию о своем адресе и реквизитак.

• Авторизация через Firebase должна быть обязательной для всех пользователей.

• Приложение должно поддерживать управление жестами для выполнения определённых действий, таких как редактирование или удаление покупок.

• Приложение должно поддерживать синхронизацию данных с облаком (Firebase).

• Поддержка темной и светлой темы интерфейса.

<a name="non_functional_requirements"></a>
### **3.2 Нефункциональные требования:**

• Надёжность: Приложение должно быть устойчивым к потерям соединения с интернетом, с возможностью локального хранения данных и последующей синхронизации.

• Безопасность: Данные пользователей и залов должны быть защищены, а авторизация — проводиться через надёжную систему (например, Firebase Authentication).

• Производительность: Приложение должно загружаться и работать плавно на большинстве современных устройств.

• Удобство использования: Интерфейс должен быть интуитивно понятным, особенно для пользователей с базовыми техническими навыками.