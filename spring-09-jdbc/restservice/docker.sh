docker image rm olezhuravlev/restservice:1.0.0
docker build -t olezhuravlev/restservice:1.0.0 .
docker login
docker push olezhuravlev/restservice:1.0.0
