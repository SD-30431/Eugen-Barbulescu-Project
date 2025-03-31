import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

interface Book {
    bookId: number;
    title: string;
    authorId: number;
    authorName: string;
    categoryId: number;
    content: string;
}

interface Subscription {
    id: {
        subscriberId: number;
        subscribedToId: number;
    };
}

const AuthorPage: React.FC = () => {
    const { authorId } = useParams<{ authorId: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [books, setBooks] = useState<Book[]>([]);
    const [authorName, setAuthorName] = useState('');
    const [error, setError] = useState<string>('');
    const [subscribed, setSubscribed] = useState<boolean>(false);

    // Fetch the books for this author (and extract author name)
    useEffect(() => {
        if (!user) return;
        const fetchBooks = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/api/books?category=all`, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                const filtered = res.data.filter(
                    (book: Book) => book.authorId === parseInt(authorId!, 10)
                );
                setBooks(filtered);
                if (filtered.length > 0) {
                    setAuthorName(filtered[0].authorName);
                }
            } catch (err) {
                console.error(err);
                setError('Error fetching books');
            }
        };
        fetchBooks();
    }, [authorId, user]);

    useEffect(() => {
        if (!user) return;
        const checkSubscription = async () => {
            try {
                const res = await axios.get<Subscription[]>(
                    `http://localhost:8080/api/subscriptions`,
                    {
                        headers: { Authorization: `Bearer ${user.token}` },
                    }
                );
                console.log(res.data)
                const isSubscribed = res.data.some(
                    (sub) => sub.subscribedToId === parseInt(authorId!, 10)
                );
                setSubscribed(isSubscribed);
            } catch (err) {
                console.error(err);
            }
        };
        // Only check subscription if viewing another author
        if (user && parseInt(authorId!, 10) !== user.authorId) {
            checkSubscription();
        }
    }, [authorId, user]);

    const handleBack = () => {
        navigate('/');
    };

    // Toggle subscription: if subscribed, unsubscribe; otherwise, subscribe.
    const toggleSubscription = async () => {
        if (!user) return;
        try {
            if (!subscribed) {
                await axios.post(
                    `http://localhost:8080/api/subscriptions/${authorId}`,
                    null,
                    { headers: { Authorization: `Bearer ${user.token}` } }
                );
                setSubscribed(true);
                alert('Subscribed successfully!');
            } else {
                await axios.delete(`http://localhost:8080/api/subscriptions/${authorId}`, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                setSubscribed(false);
                alert('Unsubscribed successfully!');
            }
        } catch (err: any) {
            console.error(err);
            alert(err.response?.data || 'Subscription action failed');
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <button onClick={handleBack}>Back</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <h1>Author: {authorName || authorId}</h1>
            {/* If current user is the same as the author, show Publish New Book button */}
            {user && parseInt(authorId!, 10) === user.authorId && (
                <button onClick={() => navigate('/books/publish')}>Publish New Book</button>
            )}
            {/* Otherwise, show the subscribe/unsubscribe toggle button */}
            {user && parseInt(authorId!, 10) !== user.authorId && (
                <button onClick={toggleSubscription} style={{ marginLeft: '10px' }}>
                    {subscribed ? 'Unsubscribe' : 'Subscribe'}
                </button>
            )}
            <h2>Books</h2>
            {books.length > 0 ? (
                <ul style={{ listStyleType: 'none', padding: 0 }}>
                    {books.map((book) => (
                        <li
                            key={book.bookId}
                            style={{
                                marginBottom: '10px',
                                padding: '10px',
                                border: '1px solid #ccc',
                                borderRadius: '4px',
                            }}
                        >
                            <Link
                                to={`/books/${book.bookId}`}
                                style={{
                                    fontWeight: 'bold',
                                    textDecoration: 'none',
                                    color: '#333',
                                }}
                            >
                                {book.title}
                            </Link>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No books found for this author.</p>
            )}
        </div>
    );
};

export default AuthorPage;
