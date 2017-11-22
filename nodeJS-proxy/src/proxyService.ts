import * as express from "express";
import * as http from "http";
import * as socketIo from "socket.io";
import * as WebSocket from "ws";

class ProxyService {

    public app: express.Application;
    public static PORT: number = 3000;
    public static WS_HOST: string = "ws://localhost:9000/api/ws/chat";
    public static RECONNECT_TIMEOUT: number = 5000; // 5 seconds

    private wsHost: string;
    private ws: WebSocket;

    private server: http.Server;
    private io: SocketIO.Server;
    private port: number;

    constructor() {
        this.createApp();
        this.config();
        this.createServer();
        this.createIOSocket();
        this.createWebSocket();
    }

    public static bootstrap(): ProxyService {
        return new ProxyService();
    }

    private createApp(): void {
        this.app = express();
    }

    private config(): void {
        this.port = ProxyService.PORT;
        this.wsHost = ProxyService.WS_HOST;
    }

    private createServer(): void {
        this.server = http.createServer(this.app);
    }

    private createIOSocket() {

        this.io = socketIo(this.server);

        this.server.listen(this.port, () => {
            console.log('Listening on port %s', this.port);
        });

        this.io.on('connect', (socket: SocketIO.Socket) => {
            console.log('IO: Connected client on port %s.', this.port);

            socket.on('send message', (value: string) => {
                this.ws.send(value, console.log.bind(null, 'WS: Sent: ', value));
            });

        });
    }

    private createWebSocket(): void {

        this.ws = new WebSocket(this.wsHost);

        this.ws.on('open', () => {
            console.log('WS: Connected: ' + this.ws.url);
        });

        this.ws.on('message', (message) => {
            console.log('WS: Received: ' + message);
            this.io.emit('message received', message);
        });

        this.ws.on('close', (code) => {
            console.log('WS: Disconnected: ' + code);
            switch (code) {
                case 1000: // Normal close
                    console.log("WS: WebSocket closed normally");
                    break;
                default: // Abnormal close
                    console.log("WS: Reconnecting WebSocket...");
                    setTimeout(() => {
                        this.createWebSocket();
                    }, ProxyService.RECONNECT_TIMEOUT);
                    break;
            }
        });

        this.ws.on('error', (error) => {
            console.log('WS: Error: ' + error.message);
        });
    }

}

declare namespace proxyService {
    type app = express.Application;
}

let proxyService = ProxyService.bootstrap();
export = proxyService.app;