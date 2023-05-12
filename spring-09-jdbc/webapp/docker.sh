docker image rm olezhuravlev/webapp:1.0.0
docker build -t olezhuravlev/webapp:1.0.0 .
docker login
docker push olezhuravlev/webapp:1.0.0
