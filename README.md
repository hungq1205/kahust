A simple webapp inspired by Kahoot

Run application by compose the **docker-compose.yml** and launch the **frontend server** in the **/frontend** folder

# Architecture
Use **microservices** architecture with each component is containerized with **Docker**:
![kahust](https://github.com/user-attachments/assets/1864c017-5519-485d-8350-a99c0351f3b0)

&emsp; **ReactJS** as frontend

&emsp; **Java Spring Boot** for backend services:

&emsp;&emsp; - **Eureka:** service registry server

&emsp;&emsp; - **Gateway:** spring mvc servlet gateway, and it also handles **WebSocket**

&emsp;&emsp; - **User Service:** handles user information and also contains an inner authorization service for authenticate with **JWT**

&emsp;&emsp; - **Quiz Service, Room Service:** handles game logic and store informations

# Main Use Cases:

**1. User login and registration:**
![z1](https://github.com/user-attachments/assets/b3fe8607-40fe-4a91-95ce-5171933e36fe)

**2. Quiz and Quiz Set management:**

Choose or create set to edit 
![QuizSet](https://github.com/user-attachments/assets/17e09f26-d6ad-4e65-a7a2-6cac0482f331)

View, create and edit quizzes in set:
![QuizMan](https://github.com/user-attachments/assets/2c0f9a76-e572-41cb-870d-a09a5db07568)
![QuizAddEdit](https://github.com/user-attachments/assets/f779b075-3c6b-43f2-9247-01aa9c39b237)

**3. Play:**

Create or join room (create need quiz set and join room need room id)
![QuizSelect](https://github.com/user-attachments/assets/1d897dd5-d89c-40ac-824b-16fe8017595c)

Room owner manages and participant plays
![z2](https://github.com/user-attachments/assets/a394a0f3-5834-4c00-a226-46b850275fcc)
![z3](https://github.com/user-attachments/assets/5968c671-d68f-40ba-a255-ea63a9b2b906)
![5](https://github.com/user-attachments/assets/d761ef79-1519-454f-9ac8-ef4a73827dea)

