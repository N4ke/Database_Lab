#include <iostream>
#include <cstring>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>

#define PORT 8080
#define MAX_CLIENTS 10

void* handle_client(void* arg) {
    int client_socket = *(int*)arg;
    char buffer[1024];

    int bytes_read = recv(client_socket, buffer, sizeof(buffer), 0);
    if (bytes_read <= 0) {
        if (bytes_read == 0) {
            std::cerr << "Client disconnected." << std::endl;
        } else {
            std::cerr << "Error receiving data." << std::endl;
        }
        close(client_socket);
        pthread_exit(NULL);
    }

    std::cout << "Received: " << buffer << std::endl;
    send(client_socket, buffer, bytes_read, 0);

    close(client_socket);
    pthread_exit(NULL);
}

int main() {
    int server_socket, client_socket;
    struct sockaddr_in server_address, client_address;
    socklen_t client_address_len = sizeof(client_address);
    pthread_t thread_id;

    server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == -1) {
        std::cerr << "Error creating socket." << std::endl;
        return 1;
    }

    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(PORT);

    if (bind(server_socket, (struct sockaddr*)&server_address, sizeof(server_address)) == -1) {
        std::cerr << "Error binding socket." << std::endl;
        return 1;
    }

    if (listen(server_socket, MAX_CLIENTS) == -1) {
        std::cerr << "Error listening on socket." << std::endl;
        return 1;
    }

    std::cout << "Server listening on port " << PORT << std::endl;

    while (true) {
        client_socket = accept(server_socket, (struct sockaddr*)&client_address, &client_address_len);
        if (client_socket == -1) {
            std::cerr << "Error accepting connection." << std::endl;
            continue;
        }

        std::cout << "New connection accepted." << std::endl;

        if (pthread_create(&thread_id, NULL, handle_client, &client_socket) != 0) {
            std::cerr << "Error creating thread." << std::endl;
            close(client_socket);
            continue;
        }

        pthread_detach(thread_id);
    }

    close(server_socket);
    return 0;
}