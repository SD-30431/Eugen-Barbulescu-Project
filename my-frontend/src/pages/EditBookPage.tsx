import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const EditBookPage = () => {
    const { bookId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [category, setCategory] = useState('');
    const [categories, setCategories] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) return;
        const fetchCategories = async () => {
            try {
                const res = await axios.get('http://localhost:8080/api/categories', {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                console.log(res, res.data)
                setCategories(res.data);
            } catch (err) {
                setError('Error fetching categories');
            }
        };
        fetchCategories();
    }, [user]);

    useEffect(() => {
        if (!user) return;
        const fetchBook = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/api/books/${bookId}`, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                const book = res.data;
                if (book.authorId !== user.authorId) {
                    setError('You are not authorized to edit this book.');
                } else {
                    setTitle(book.title);
                    setContent(book.content);
                    setCategory(String(book.categoryId));
                }
            } catch (err) {
                setError('Error fetching book data');
            } finally {
                setLoading(false);
            }
        };
        fetchBook();
    }, [bookId, user]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put(
                `http://localhost:8080/api/books/${bookId}`,
                { title, content, categoryId: category },
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            navigate(`/books/${bookId}`);
        } catch (err) {
            setError('Error updating book');
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('Are you sure you want to delete this book?')) return;
        try {
            await axios.delete(`http://localhost:8080/api/books/${bookId}`, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            navigate('/books');
        } catch (err) {
            setError('Error deleting book');
        }
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div style={{ padding: '20px' }}>
            <button onClick={() => navigate(-1)}>Back</button>
            <h1>Edit Book</h1>
            <form onSubmit={handleSubmit}>
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
                <button type="submit">Save Changes</button>
            </form>
            <hr />
            <button onClick={handleDelete} style={{ marginTop: '20px' }}>
                Delete Book
            </button>
        </div>
    );
};

export default EditBookPage;
