# Vapor Sea Industry

This project was born out of the always unseen value tied up in Tech 2 Bluprints.  if you've ever done any amount of invention you know you end up with hundreds of bpc copies that have value but nowhere can you see this value.  This is the frustration this app serves to alleviate.  By tracking how much is paid on datacres, how much was paid at the lab and how much was lost due to probabiliies we can easily come to a good average value of our bpc library.

## To run it

I recommend using docker to run this, but if you must know how to build from scratch:

Prerequisites:
- Java 17 or higher

1. Clone the repository.
2. Make the maven wrapper executable: `chmod +x mvnw`.
3. Package the application using Maven: `./mvnw clean package`. 
4. Copy config/application-template.yml to config/application.yml and edit it to your liking.
5. Set the environment variables `CORPORATION_ID`, `EVE_CLIENT_ID` and `EVE_CLIENT_SECRET`
6. Run the application: `java -jar target/vapor-sea-industry-${VERSION}.jar`. 
7. This will create a data(the db) and vsindustry(the cache) directories with data in them.
8. Grab the frontend over at https://github.com/maurerit/vs-industry-frontend
