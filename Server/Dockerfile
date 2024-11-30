FROM gcc:latest

RUN apt-get update && apt-get install -y \
    cmake \
    make \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN g++ -o server server.cpp -lpthread

EXPOSE 8080

CMD ["./server"]