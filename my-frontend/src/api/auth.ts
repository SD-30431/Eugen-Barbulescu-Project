import axios from 'axios';

const API_URL = 'http://localhost:8080'; // Adjust this to your backend URL

export interface LoginResponse {
    token: string;
    authorId: string
    role: string
}

export interface SignupResponse {
    message: string;
}

export interface LoginRequest {
    name: string;
    password: string;
}

export interface SignupRequest {
    name: string;
    password: string;
}

export const loginApi = async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axios.post(`${API_URL}/auth/login`, data);
    return response.data;
};

export const signupApi = async (data: SignupRequest): Promise<SignupResponse> => {
    const response = await axios.post(`${API_URL}/auth/signup`, data);
    return response.data;
};
