# Vapor Sea Industry

This project was born out of the always unseen value tied up in Tech 2 Bluprints.  if you've ever done any amount of invention you know you end up with hundreds of bpc copies that have value but nowhere can you see this value.  This is the frustration this app serves to alleviate.  By tracking how much is paid on datacres, how much was paid at the lab and how much was lost due to probabiliies we can easily come to a good average value of our bpc library.

## Don't look too deep

This is cobbled together quickly and with little forethought :D.  I just want things to work but I do try to follow best practices... normally...  What you're looking at is a warehouse implementation with bolted on requirements.  I need the industry and market data, I need the blueprint data for manufacturing and invention, I need authentication so that I can fetch the data, etc.  What I really wanted though was a warehouse, plain and simple.  Show me what I have, what I can build and how much it would cost me.

I do try to keep develop in a runnable state.  I'm better here than I am over at the frontend.  I try to keep the frontend and backend in sync with each other.

## To run it

I recommend using docker-compose to run https://github.com/maurerit/vs-industry-frontend/blob/develop/docker-compose.yml, but if you must know how to build from scratch:

Prerequisites:
- Java 17 or higher

1. Clone the repository.
2. Make the maven wrapper executable: `chmod +x mvnw`.
3. Package the application using Maven: `./mvnw clean package`. 
4. Copy config/application-template.yml to config/application.yml and edit it to your liking.
5. Set the environment variables `CORPORATION_ID`, `EVE_CLIENT_ID` and `EVE_CLIENT_SECRET`
6. Run the application: `java -jar target/vapor-sea-industry-${VERSION}.jar`. 
7. This will create a data(the db) and vsindustry(the cache) directories with data in them.
8. Grab the frontend over at https://github.com/maurerit/vs-industry-frontend and run from there

## Additional Notes

For the more technically minded.  Go get yourself a copy of [SQuirrel SQL Client](https://squirrel-sql.sourceforge.io/) and download the [h2 driver](https://github.com/h2database/h2database/releases) into squirrels lib directory.  You can then connect to the database using the following connection string: jdbc:h2:{path to data dir}/data/vaporseaindustry with username sa and password of... you guessed it... password.  In here you'll have all historical industry job and market transaction along with journal entries.  Report to your hearts content.

## Changelog

All notable changes to this project are documented in the [CHANGELOG.md](CHANGELOG.md) file. Please refer to this file for information about new features, changes, and bug fixes.
