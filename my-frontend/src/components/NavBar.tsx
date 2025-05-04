// src/components/NavBar.tsx
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {useWebsocketChat} from "./useWebsocketChat.tsx";

const NavBar: React.FC = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const { isConnected } = useWebsocketChat('ws://localhost:8080/ws');

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav style={{
            padding: '10px',
            borderBottom: '1px solid #ccc',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            flexWrap: 'wrap',
        }}>
            <Link to="/">Home</Link>
            {user ? (
                <>
                    |
                    <span>
            Welcome, {user.username} (WS: {isConnected ? '🟢' : '🔴'})
          </span>
                    |
                    <Link to={`/authors/${user.authorId}`}>My Profile</Link>
                    |
                    <Link to="/chat">CHAT!</Link>
                    |
                    {user.role === 'ADMIN' && (
                        <>
                            <Link to="/admin">Admin Panel</Link> |
                        </>
                    )}
                    <button onClick={handleLogout} style={{ marginLeft: 'auto' }}>
                        Logout
                    </button>
                </>
            ) : (
                <>
                    |
                    <Link to="/login">Login</Link> | <Link to="/signup">Signup</Link>
                </>
            )}
        </nav>
    );
};

export default NavBar;
