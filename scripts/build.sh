#! /bin/sh
#Replace "?" with your GOOGLE_MAPS_API_KEY for connection to Google Map
GOOGLE_MAPS_API_KEY="?"

#delete all containers of GoodsPartner App
docker container rm -f $(docker container ls -a -q --filter name=goods*)

#delete images of GoodsPartner App
docker image rm -f $(docker images 'goods*' -q)

#build Docker image
docker-compose build --build-arg GOOGLE_MAPS_API_KEY=$GOOGLE_MAPS_API_KEY

#run GoodsPartner container and Postgres container
docker-compose up -d