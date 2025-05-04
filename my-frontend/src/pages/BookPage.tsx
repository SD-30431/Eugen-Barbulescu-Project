// src/pages/BookPage.tsx

import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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

interface ReviewDTO {
    reviewId: number;
    bookId: number;
    bookTitle: string;
    authorId: number;
    authorName: string;
    rating: number;
}

interface CommentDTO {
    commentId: number;
    bookId: number;
    bookTitle: string;
    authorId: number;
    authorName: string;
    commentStr: string;
}

const BookPage: React.FC = () => {
    const { bookId } = useParams<{ bookId: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [book, setBook] = useState<Book | null>(null);
    const [reviews, setReviews] = useState<ReviewDTO[]>([]);
    const [comments, setComments] = useState<CommentDTO[]>([]);
    const [reviewRating, setReviewRating] = useState<string>('');
    const [commentText, setCommentText] = useState<string>('');
    const [error, setError] = useState<string>('');

    useEffect(() => {
        if (!user) return;

        const fetchData = async () => {
            try {
                const [bookRes, reviewsRes, commentsRes] = await Promise.all([
                    axios.get<Book>(`http://localhost:8080/api/books/${bookId}`, {
                        headers: { Authorization: `Bearer ${user.token}` },
                    }),
                    axios.get<ReviewDTO[]>(`http://localhost:8080/api/reviews/books/${bookId}`, {
                        headers: { Authorization: `Bearer ${user.token}` },
                    }),
                    axios.get<CommentDTO[]>(`http://localhost:8080/api/comments/books/${bookId}`, {
                        headers: { Authorization: `Bearer ${user.token}` },
                    }),
                ]);

                setBook(bookRes.data);
                setReviews(reviewsRes.data);
                setComments(commentsRes.data);
            } catch (err) {
                console.error(err);
                setError('Error fetching book data');
            }
        };

        fetchData();
    }, [bookId, user]);

    const handleReviewSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!user) return;

        try {
            const res = await axios.post<ReviewDTO>(
                `http://localhost:8080/api/reviews/books/${bookId}`,
                { review: parseInt(reviewRating, 10) },
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            setReviews([...reviews, res.data]);
            setReviewRating('');
        } catch (err) {
            console.error(err);
            setError('Error submitting review');
        }
    };

    const handleCommentSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!user) return;

        try {
            const res = await axios.post<CommentDTO>(
                `http://localhost:8080/api/comments/books/${bookId}`,
                { commentString: commentText },
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            setComments([...comments, res.data]);
            setCommentText('');
        } catch (err) {
            console.error(err);
            setError('Error submitting comment');
        }
    };

    const handleDownloadXml = async () => {
        if (!user) return;

        try {
            const res = await axios.get(
                `http://localhost:8080/api/books/${bookId}/export`,
                {
                    headers: { Authorization: `Bearer ${user.token}` },
                    responseType: 'blob',
                }
            );
            const blob = new Blob([res.data], { type: 'application/xml' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `book-${bookId}.xml`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error(err);
            setError('Failed to download XML');
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <button onClick={() => navigate('/')}>Back</button>
            {error && <p style={{ color: 'red' }}>{error}</p>}

            {book ? (
                <div>
                    <h1>{book.title}</h1>
                    <p>By: {book.authorName}</p>

                    {user && book.authorId === user.authorId && (
                        <>
                            <button onClick={() => navigate(`/books/${book.bookId}/edit`)}>
                                Edit Book
                            </button>
                            <button
                                onClick={handleDownloadXml}
                                style={{ marginLeft: '8px' }}
                            >
                                Download XML
                            </button>
                        </>
                    )}

                    <div>
                        <h2>Reviews</h2>
                        {reviews.length > 0 ? (
                            reviews.map((review) => (
                                <div key={review.reviewId}>{review.rating} Stars</div>
                            ))
                        ) : (
                            <p>No reviews yet.</p>
                        )}
                        <form onSubmit={handleReviewSubmit}>
                            <input
                                type="number"
                                min="1"
                                max="5"
                                value={reviewRating}
                                onChange={(e) => setReviewRating(e.target.value)}
                                placeholder="Rate (1-5)"
                                required
                            />
                            <button type="submit">Submit Review</button>
                        </form>
                    </div>

                    <div>
                        <h2>Comments</h2>
                        {comments.length > 0 ? (
                            comments.map((c) => (
                                <div key={c.commentId}>{c.commentStr}</div>
                            ))
                        ) : (
                            <p>No comments yet.</p>
                        )}
                        <form onSubmit={handleCommentSubmit}>
                            <input
                                type="text"
                                value={commentText}
                                onChange={(e) => setCommentText(e.target.value)}
                                placeholder="Leave a comment"
                                required
                            />
                            <button type="submit">Submit Comment</button>
                        </form>
                    </div>
                </div>
            ) : (
                <p>Loading book details...</p>
            )}
        </div>
    );
};

export default BookPage;
