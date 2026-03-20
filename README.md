gets input and extracts skills + experience
sends the data to python model in localhost:8000

input format : rest call with skills and exp as key-value pairs in map

Map<String, String> request = new HashMap<>();
request.put("skills", skills);
request.put("experience", experience);

expected output format :

"Phase,Title,Description,Duration\n" +
"1,Foundation,Master Java 17 and Core OOP principles,2 weeks\n" +
"2,Spring ecosystem,Learn Spring Boot, Dependency Injection, and Context,3 weeks\n" +
"3,Data Access,Delve into JPA, Hibernate, and advanced SQL operations,2 weeks\n" +
"4,Security,Implement JWT Auth and RBAC with Spring Security,2 weeks\n" +
"5,Cloud Deployment,Dockerize and deploy microservices on AWS/Vercel,3 weeks"