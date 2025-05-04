import React, {
    createContext,
    useContext,
    useEffect,
    useState,
    useRef,
    ReactNode,
} from 'react';
import { useAuth } from './AuthContext';

interface WebSocketContextType {
    isConnected: boolean;
    setNotificationHandler: (handler: (payload: any) => void) => void;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const useWebSocket = (): WebSocketContextType => {
    const ctx = useContext(WebSocketContext);
    if (!ctx) throw new Error('useWebSocket must be inside WebSocketProvider');
    return ctx;
};

export const WebSocketProvider = ({ children }: { children: ReactNode }) => {
    const { user } = useAuth();
    const [isConnected, setIsConnected] = useState(false);
    const notifyRef = useRef<(p: any) => void>(() => {});

    // hold the WebSocket instance across renders
    const wsRef = useRef<WebSocket | null>(null);

    useEffect(() => {
        if (!user?.token) return;

        // build URL with token if needed
        const url = `ws://localhost:8080/ws?token=${encodeURIComponent(
            user.token
        )}`;
        const ws = new WebSocket(url);
        wsRef.current = ws;

        ws.onopen = () => {
            console.log('[WS] connected');
            setIsConnected(true);
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                notifyRef.current(data);
            } catch (err) {
                console.warn('[WS] invalid JSON:', event.data);
            }
        };

        ws.onclose = (ev) => {
            console.log('[WS] disconnected', ev.reason);
            setIsConnected(false);
        };

        ws.onerror = (err) => {
            console.error('[WS] error', err);
            setIsConnected(false);
        };

        return () => {
            ws.close();
            setIsConnected(false);
        };
    }, [user?.token]);

    const setNotificationHandler = (handler: (payload: any) => void) => {
        notifyRef.current = handler;
    };

    return (
        <WebSocketContext.Provider value={{ isConnected, setNotificationHandler }}>
            {children}
        </WebSocketContext.Provider>
    );
};
