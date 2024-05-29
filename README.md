# Customer Rewards Service

### Description
The purpose of the service is to provide information about points collected by the customers based on the collected
transactions in the 3 months period. The 3 months period is being calculated based on the last month of the period
from the request. If the request contains month 2024-05 then the calculation is being done based on transactions from
03, 04 ,05 month of 2024.
If request contains also a customer ID then the calculation is done only for the selected customer, otherwise it is done
for all customers.

### Used tools
* liquibase to menage db changes
* H2 db
* swagger to provide API documentation (http://localhost:8080/swagger-ui/index.html#/)

### How to build the app
From the root folder of the project run:
> mvn clean install

### How to run the app
From the root folder of the project run docker commands:
> docker build --build-arg JAR_FILE=target/*.jar -t js/rewards .

> docker run -p 8080:8080 js/rewards

The application starts on port 8080

To try the API we can use swagger ui
> http://localhost:8080/swagger-ui/index.html