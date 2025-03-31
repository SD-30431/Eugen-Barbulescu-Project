import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface AuthActivity {
    id: number;
    authorId: number;
    activity: string;
    timestamp: string;
}

interface CategoryStats {
    categoryId: number;
    bookCount: number;
}

type ActiveTab = 'activity' | 'categories';

const AdminPanel: React.FC = () => {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [activeTab, setActiveTab] = useState<ActiveTab>('activity');
    const [activities, setActivities] = useState<AuthActivity[]>([]);
    const [categoryStats, setCategoryStats] = useState<CategoryStats[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    const fetchActivities = async () => {
        setLoading(true);
        setError('');
        try {
            const res = await axios.get<AuthActivity[]>(
                'http://localhost:8080/api/admin/activity',
                {
                    headers: { Authorization: `Bearer ${user?.token}` },
                }
            );
            console.log(res.data);
            setActivities(res.data);
        } catch (err: any) {
            console.error(err);
            setError('Failed to fetch login/logout activities.');
        } finally {
            setLoading(false);
        }
    };

    // Function to load category statistics
    const fetchCategoryStats = async () => {
        setLoading(true);
        setError('');
        try {
            const res = await axios.get<CategoryStats[]>(
                'http://localhost:8080/api/admin/categories',
                {
                    headers: { Authorization: `Bearer ${user?.token}` },
                }
            );
            setCategoryStats(res.data);
        } catch (err: any) {
            console.error(err);
            setError('Failed to fetch category statistics.');
        } finally {
            setLoading(false);
        }
    };

    // Fetch data when activeTab changes
    useEffect(() => {
        if (!user) return;
        if (activeTab === 'activity') {
            fetchActivities();
        } else if (activeTab === 'categories') {
            fetchCategoryStats();
        }
    }, [activeTab, user]);

    const handleTabChange = (tab: ActiveTab) => {
        setActiveTab(tab);
    };

    const handleBack = () => {
        navigate('/');
    };

    return (
        <div style={{ padding: '20px' }}>
            <button onClick={handleBack} style={{ marginBottom: '20px' }}>
                Back to Home
            </button>
            <h2>Admin Panel</h2>
            <div style={{ marginBottom: '20px' }}>
                <button
                    onClick={() => handleTabChange('activity')}
                    style={{
                        marginRight: '8px',
                        padding: '8px 12px',
                        backgroundColor: activeTab === 'activity' ? '#add8e6' : '#f0f0f0',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer'
                    }}
                >
                    Login/Logout Activity
                </button>
                <button
                    onClick={() => handleTabChange('categories')}
                    style={{
                        padding: '8px 12px',
                        backgroundColor: activeTab === 'categories' ? '#add8e6' : '#f0f0f0',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer'
                    }}
                >
                    Category Statistics
                </button>
            </div>
            {loading && <p>Loading data...</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {activeTab === 'activity' && !loading && (
                <div>
                    <h3>Recent Login/Logout Activity</h3>
                    {activities.length === 0 ? (
                        <p>No activity records found.</p>
                    ) : (
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                            <tr>
                                <th style={{ border: '1px solid #ccc', padding: '8px' }}>ID</th>
                                <th style={{ border: '1px solid #ccc', padding: '8px' }}>Author ID</th>
                                <th style={{ border: '1px solid #ccc', padding: '8px' }}>Activity</th>
                                <th style={{ border: '1px solid #ccc', padding: '8px' }}>Timestamp</th>
                            </tr>
                            </thead>
                            <tbody>
                            {activities.map((act) => (
                                <tr key={act.id}>
                                    <td style={{ border: '1px solid #ccc', padding: '8px' }}>{act.id}</td>
                                    <td style={{ border: '1px solid #ccc', padding: '8px' }}>{act.authorId}</td>
                                    <td style={{ border: '1px solid #ccc', padding: '8px' }}>{act.activity}</td>
                                    <td style={{ border: '1px solid #ccc', padding: '8px' }}>{new Date(act.timestamp).toLocaleString()}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}
            {activeTab === 'categories' && !loading && (
                <div>
                    <h3>Books per Category</h3>
                    {categoryStats.length === 0 ? (
                        <p>No category statistics available.</p>
                    ) : (
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                            <tr>
                                <th style={{ border: '1px solid #ccc', padding: '8px' }}>Category ID</th>
                                <th style={{ border: '1px solid #ccc', padding: '8px' }}>Book Count</th>
                            </tr>
                            </thead>
                            <tbody>
                            {categoryStats.map((stat, index) => (
                                <tr key={index}>
                                    <td style={{ border: '1px solid #ccc', padding: '8px' }}>{stat.categoryId}</td>
                                    <td style={{ border: '1px solid #ccc', padding: '8px' }}>{stat.bookCount}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}
        </div>
    );
};

export default AdminPanel;
