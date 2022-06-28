FROM openjdk

RUN apt-get -y update

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY target/*.jar app.jar

EXPOSE 3400

CMD ["java","-jar","app.jar"]
