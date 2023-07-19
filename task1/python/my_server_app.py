import socket
import sys
import threading

subscribers = {}  # Store topics and subscribed clients

def handle_client(client_socket, address):
    try:
        while True:
            data = client_socket.recv(1024).decode()
            if not data:
                break

            print(f"Received from {address}: {data}")
            process_message(data, address)

    except Exception as e:
        print(f"Error: {e}")
    finally:
        client_socket.close()

def process_message(message, address):
    if message.startswith("SUBSCRIBE:"):
        topic = message.replace("SUBSCRIBE:", "")
        subscribers.setdefault(topic, set()).add(address)
        print(f"Client {address} subscribed to topic: {topic}")
    elif message.startswith("UNSUBSCRIBE:"):
        topic = message.replace("UNSUBSCRIBE:", "")
        if topic in subscribers and address in subscribers[topic]:
            subscribers[topic].remove(address)
            print(f"Client {address} unsubscribed from topic: {topic}")
    else:
        send_message_to_subscribers(message, address)

def send_message_to_subscribers(message, address):
    for topic, subscriber_list in subscribers.items():
        if address in subscriber_list:
            for subscriber in subscriber_list:
                if address != subscriber:
                    try:
                        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                        client_socket.connect(subscriber)
                        client_socket.send(message.encode())
                        client_socket.close()
                    except:
                        print(f"Unable to send message to client {subscriber}")

def start_server(port):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    try:
        server_socket.bind(('', port))
        server_socket.listen(5)
        print(f"Server started and listening on port {port}")

        while True:
            client_socket, client_address = server_socket.accept()
            print(f"Connected to client: {client_address}")

            client_thread = threading.Thread(target=handle_client, args=(client_socket, client_address))
            client_thread.start()

    except Exception as e:
        print(f"Error: {e}")
    finally:
        server_socket.close()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python my_server_app.py <port>")
        sys.exit(1)

    port = int(sys.argv[1])
    start_server(port)
