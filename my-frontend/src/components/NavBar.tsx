import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NavBar: React.FC = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav style={{ padding: '10px', borderBottom: '1px solid #ccc' }}>
            <Link to="/">Home</Link> |{' '}
            {user ? (
                <>
                    <span>Welcome, {user.username}</span> |{' '}
                    {/* Button to redirect to the user's author page */}
                    <Link to={`/authors/${user.authorId}`}>My Profile</Link> |{' '}
                    {user.role === 'ADMIN' && <Link to="/admin">Admin Panel</Link>} |{' '}
                    <button onClick={handleLogout}>Logout</button>
                </>
            ) : (
                <>
                    <Link to="/login">Login</Link> | <Link to="/signup">Signup</Link>
                </>
            )}
        </nav>
    );
};

export default NavBar;
