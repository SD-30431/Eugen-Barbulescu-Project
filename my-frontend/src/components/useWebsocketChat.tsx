// src/hooks/useWebsocketChat.ts
import { useEffect, useRef, useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';

export function useWebsocketChat(urlBase: string) {
    const { user } = useAuth();
    const token = user?.token;
    const wsRef = useRef<WebSocket | null>(null);

    const [isConnected, setIsConnected] = useState(false);
    const [chatMessages, setChatMessages] = useState<string[]>([]);

    const sendChatMessage = useCallback((message: string) => {
        const ws = wsRef.current;
        if (ws && ws.readyState === WebSocket.OPEN) {
            ws.send(message);
            setChatMessages(prev => [...prev, message]);
        }
    }, []);

    useEffect(() => {
        if (wsRef.current) {
            wsRef.current.close();
            wsRef.current = null;
            setIsConnected(false);
        }
        if (!token) return;

        const ws = new WebSocket(`${urlBase}?token=${encodeURIComponent(token)}`);
        wsRef.current = ws;

        ws.onopen = () => setIsConnected(true);
        ws.onerror = () => setIsConnected(false);
        ws.onclose = () => setIsConnected(false);

        ws.onmessage = evt => {
            let text: string;
            // try to parse JSON and extract a "message" or "payload" field,
            // otherwise just use the raw string
            try {
                const obj = JSON.parse(evt.data);
                if (typeof obj.message === 'string')       text = obj.message;
                else if (typeof obj.payload === 'string')  text = obj.payload;
                else                                       text = evt.data;
            } catch {
                text = evt.data;
            }
            setChatMessages(prev => [...prev, text]);
        };

        return () => {
            ws.close();
            setIsConnected(false);
        };
    }, [token, urlBase]);

    return { isConnected, chatMessages, sendChatMessage };
}
