import React from 'react';
import {Routes, Route, BrowserRouter} from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignupPage from "./pages/SingupPage";
import BookPage from './pages/BookPage';
import EditBookPage from './pages/EditBookPage';
import PublishBookPage from './pages/PublishBookPage';
import AdminPanel from './pages/AdminPanel';
import NotFoundPage from './pages/NotFoundPage';
import NavBar from './components/NavBar';
import PrivateRoute from './components/PrivateRoute';
import AuthorPage from "./pages/AuthorPage.tsx";
import {ChatWindow} from "./pages/ChatWindow.tsx";

const App: React.FC = () => {
    return (
        <BrowserRouter>
            <NavBar />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/books/:bookId" element={<BookPage />} />
                <Route
                    path="/books/:bookId/edit"
                    element={
                        <PrivateRoute>
                            <EditBookPage />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/books/publish"
                    element={
                        <PrivateRoute>
                            <PublishBookPage />
                        </PrivateRoute>
                    }
                />
                <Route path="/authors/:authorId" element={<AuthorPage />} />
                <Route
                    path="/admin"
                    element={
                        <PrivateRoute roles={['ADMIN']}>
                            <AdminPanel />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/chat"
                    element={
                        <PrivateRoute>
                            <ChatWindow />
                        </PrivateRoute>
                    }
                />
                <Route path="*" element={<NotFoundPage />} />
            </Routes>
        </BrowserRouter>
    );
};

export default App;