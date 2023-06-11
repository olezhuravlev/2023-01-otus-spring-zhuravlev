docker image rm olezhuravlev/webapp-pg:3.2.0
docker build -t olezhuravlev/webapp-pg:3.2.0 .
docker login
docker push olezhuravlev/webapp-pg:3.2.0
