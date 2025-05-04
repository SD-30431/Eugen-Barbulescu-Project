// src/components/ChatWindow.tsx
import React, { useState } from 'react';
import {useWebsocketChat} from "../components/useWebsocketChat.tsx";

export const ChatWindow: React.FC = () => {
    const { isConnected, chatMessages, sendChatMessage } =
        useWebsocketChat('ws://localhost:8080/ws');
    const [draft, setDraft] = useState('');

    return (
        <div style={{ border: '1px solid #ccc', padding: 10 }}>
            <div>Status: {isConnected ? '🟢 Connected' : '🔴 Disconnected'}</div>
            <div
                style={{
                    height: 200,
                    overflowY: 'auto',
                    border: '1px solid #eee',
                    margin: '10px 0',
                }}
            >
                {chatMessages.map((msg, i) => (
                    <div key={i}>{msg}</div>
                ))}
            </div>
            <input
                type="text"
                value={draft}
                onChange={e => setDraft(e.target.value)}
                placeholder="Type a message…"
                style={{ width: '80%' }}
            />
            <button
                onClick={() => {
                    sendChatMessage(draft);
                    setDraft('');
                }}
                disabled={!draft.trim() || !isConnected}
            >
                Send
            </button>
        </div>
    );
};
