// Import necessary modules
const net = require('net');
const crypto = require('crypto');

// Arrays to store connected clients and received messages
const clients = [];
const receivedMessages = [];

// Create a WebSocket server
const websocketServer = net.createServer((client) => {
    console.log('Client connected');

    // Event listener for data received from clients
    client.on('data', (data) => {

        // Check if the data includes the HTTP GET request for WebSocket upgrade
        if (data.toString().split('\n')[0].includes('GET / HTTP')) {

            // Parse WebSocket key from client's request headers
            const headers = data.toString().split('\n');
            let clientWebSocketKey = '';
            headers.forEach((header) => {
                if (header.includes('Sec-WebSocket-Key')) {
                    clientWebSocketKey = header.split(':')[1].trim();
                }
            });

            // Generate WebSocket accept key using WebSocket key and magic string
            const magicString = '258EAFA5-E914-47DA-95CA-C5AB0DC85B11';
            const serverWebSocketKey = crypto
                .createHash('sha1')
                .update(clientWebSocketKey + magicString)
                .digest('base64');

            // Prepare and send WebSocket handshake response to the client
            const response = `HTTP/1.1 101 Switching Protocols\r\n`
                + `Upgrade: websocket\r\n`
                + `Connection: Upgrade\r\n`
                + `Sec-WebSocket-Accept: ${serverWebSocketKey}\r\n\r\n`;
            client.write(response);

            // Add the client to the list of connected clients
            clients.push(client);

            // Send any received messages to the newly connected client
            for (let i = 0; i < receivedMessages.length; i++) {
                client.write(createWebSocketFrame(receivedMessages[i]));
            }
        } else {
            // If the data is not a WebSocket handshake, handle the message
            handleMessage(client, data);
        }
    });

    // Event listener for client disconnection
    client.on('end', () => {
        console.log('Client disconnected');
    });
});

// Start the WebSocket server
websocketServer.listen(3001, "localhost", () => {
    console.log('WebSocket server listening on port 3001');
});

// Function to handle messages received from clients
function handleMessage(client, data) {

    // Parse WebSocket frame and extract message
    const payloadLength = data[1] & 127;
    const maskStart = 2;
    const dataStart = maskStart + 4;
    let message = "";

    for (let i = dataStart; i < dataStart + payloadLength; i++) {
        const byte = data[i] ^ data[maskStart + ((i - dataStart) % 4)];
        message += String.fromCharCode(byte);
    }

    // Check if the message is valid JSON
    if (!isValidJSON(message)) {
        console.log('Received data is not valid JSON:', message);
        return;
    }

    try {
        // Parse the JSON message
        const parsedData = JSON.parse(message);

        // Check if parsedData contains 'text' property
        if (parsedData.hasOwnProperty('text')) {
            const receivedMessage = parsedData.text;
            receivedMessages.push(receivedMessage);

            // Print the received message to the terminal
            console.log('Message from client:', receivedMessage);

            // Create a WebSocket frame from the received message
            const messageToSend = createWebSocketFrame(receivedMessage);

            // Send the message back to the client who sent it
            client.write(messageToSend);

            // Broadcast the message to all connected clients
            clients.forEach((otherClient) => {
                if (otherClient !== client) {
                    otherClient.write(messageToSend);
                }
            });
        } else {
            console.log('Received data does not contain "text" property:', parsedData);
        }
    } catch (error) {
        // Handle JSON parsing error
        console.error('Error parsing JSON:', error);
    }
}

// Function to check if a string is valid JSON
function isValidJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch (error) {
        return false;
    }
}

// Function to create a WebSocket frame from a message. A websocket frame is a unit of data that is transmitted between
// a WebSocket client and server. Frames are used to encapsulate messages sent over the WebSocket connection.
// Consists of header (type, payload, masking info )and payload (the actual data being transmitted).
function createWebSocketFrame(message) {
    // Convert the message to a buffer
    const msgBuffer = Buffer.from(message);

    // Calculate the size of the message
    const msgSize = msgBuffer.length;

    // Create a buffer to hold the frame header
    const header = Buffer.alloc(2);

    // Set the first byte of the header to indicate the frame type (text)
    header.writeUInt8(0x81, 0);

    // Set the second byte of the header to indicate the message size
    header.writeUInt8(msgSize, 1);

    // Put together the header and the message buffer to form the WebSocket frame
    const frame = Buffer.concat([header, msgBuffer]);

    // Return the WebSocket frame
    return frame;
}
