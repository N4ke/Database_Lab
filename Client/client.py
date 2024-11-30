import socket

SERVER_HOST = 'localhost'
SERVER_PORT = 8080

def send_message(message):
    """Отправляет сообщение серверу и получает ответ."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        try:
            s.connect((SERVER_HOST, SERVER_PORT))
            s.sendall(message.encode('utf-8'))
            data = s.recv(1024)
            print(f'Received from server: {data.decode("utf-8")}')
        except ConnectionRefusedError:
            print(f"Connection refused to {SERVER_HOST}:{SERVER_PORT}")
        except Exception as e:
            print(f"An error occurred: {e}")

if __name__ == '__main__':
    while True:
        message = input("Enter message to send (or 'exit' to quit): ")
        if message.lower() == 'exit':
            break
        send_message(message)