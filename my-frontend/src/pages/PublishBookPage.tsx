import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const PublishBookPage = () => {
    const navigate = useNavigate();
    const { user } = useAuth();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [category, setCategory] = useState('');
    const [categories, setCategories] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!user) return;
        const fetchCategories = async () => {
            try {
                const res = await axios.get('http://localhost:8080/api/categories', {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                setCategories(res.data);
            } catch (err) {
                setError('Error fetching categories');
            }
        };
        fetchCategories();
    }, [user]);

    const handlePublish = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post(
                'http://localhost:8080/api/books',
                { title, content, categoryId: category },
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            navigate(`/books/${res.data.bookId}`);
        } catch (err) {
            setError('Error publishing book');
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <button onClick={() => navigate(-1)}>Back</button>
            <h1>Publish New Book</h1>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <form onSubmit={handlePublish}>
                <div>
                    <label>Title: </label>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Content: </label>
                    <textarea
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        required
                        rows="10"
                    />
                </div>
                <div>
                    <label>Category: </label>
                    <select value={category} onChange={(e) => setCategory(e.target.value)} required>
                        <option value="" disabled>
                            Select Category
                        </option>
                        {categories.map((cat) => (
                            <option key={cat.categoryId} value={cat.categoryId}>
                                {cat.name}
                            </option>
                        ))}
                    </select>
                </div>
                <button type="submit">Publish Book</button>
            </form>
        </div>
    );
};

export default PublishBookPage;
