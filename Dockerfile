FROM hseeberger/scala-sbt:11.0.7_1.3.10_2.13.2
COPY . /cddaDB
CMD cd /cddaDB
RUN sbt run