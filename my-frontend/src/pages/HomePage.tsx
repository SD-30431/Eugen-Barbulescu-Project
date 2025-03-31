import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export interface Book {
    bookId: number;
    title: string;
    authorId: number;
    authorName: string;
    categoryId: number;
    content: string;
}

export interface Category {
    categoryId: number;
    name: string;
}

const HomePage: React.FC = () => {
    const { user } = useAuth();
    const [selectedCategory, setSelectedCategory] = useState<string>('all');
    const [books, setBooks] = useState<Book[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    useEffect(() => {
        if (!user) return;
        const fetchCategories = async () => {
            try {
                const res = await axios.get<Category[]>(
                    'http://localhost:8080/api/categories',
                    {
                        headers: {
                            Authorization: `Bearer ${user.token}`,
                        },
                    }
                );
                setCategories(res.data);
            } catch (err: any) {
                console.error(err);
                setError('Failed to fetch categories.');
            }
        };
        fetchCategories();
    }, [user]);

    useEffect(() => {
        if (!user) return;
        setLoading(true);
        setError('');
        const fetchBooks = async () => {
            try {
                const response = await axios.get<Book[]>(
                    `http://localhost:8080/api/books?category=${selectedCategory}`,
                    {
                        headers: {
                            Authorization: `Bearer ${user.token}`,
                        },
                    }
                );
                setBooks(response.data);
            } catch (err: any) {
                console.error(err);
                setError('Failed to fetch books.');
            } finally {
                setLoading(false);
            }
        };
        fetchBooks();
    }, [selectedCategory, user]);

    const handleCategoryClick = (id: string) => {
        setSelectedCategory(id);
    };

    // Combine the "All" option with fetched categories
    const categoryButtons = [
        { id: 'all', name: 'All' },
        ...categories.map(cat => ({ id: String(cat.categoryId), name: cat.name }))
    ];

    return (
        <div style={{ padding: '20px' }}>
            <h2>Home Page</h2>
            <div style={{ marginBottom: '20px' }}>
                {categoryButtons.map((cat) => (
                    <button
                        key={cat.id}
                        onClick={() => handleCategoryClick(cat.id)}
                        style={{
                            marginRight: '8px',
                            padding: '8px 12px',
                            backgroundColor: selectedCategory === cat.id ? '#add8e6' : '#f0f0f0',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer',
                        }}
                    >
                        {cat.name}
                    </button>
                ))}
            </div>
            {loading && <p>Loading books...</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}
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
                            style={{ fontWeight: 'bold', textDecoration: 'none', color: '#333' }}
                        >
                            {book.title}
                        </Link>
                        <span> by </span>
                        <Link
                            to={`/authors/${book.authorId}`}
                            style={{ textDecoration: 'none', color: '#0066cc' }}
                        >
                            {book.authorName}
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default HomePage;
