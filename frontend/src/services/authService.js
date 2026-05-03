import api from "./api";

// kullanici girisi
export const login = async (data) => {
    const response = await api.post("/api/auth/login", data);
    return response.data;
};

// kullanici kaydi
export const register = async (data) => {
    const response = await api.post("/api/auth/register", data);
    return response.data;
};