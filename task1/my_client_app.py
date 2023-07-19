import socket
import sys

def start_client(server_ip, port):
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    try:
        client_socket.connect((server_ip, port))
        print("Connected to server")

        while True:
            command = input("Enter command (e.g., 'SUBSCRIBE:topic', 'UNSUBSCRIBE:topic', 'PUBLISH:message'): ")
            client_socket.send(command.encode())

            if command.lower() == 'exit':
                break

    except Exception as e:
        print(f"Error: {e}")
    finally:
        client_socket.close()

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python my_client_app.py <server_ip> <port>")
        sys.exit(1)

    server_ip = sys.argv[1]
    port = int(sys.argv[2])
    start_client(server_ip, port)
