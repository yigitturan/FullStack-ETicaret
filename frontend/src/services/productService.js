import api from "./api";

export const getProducts = async (page = 0, size = 10) => {
    const response = await api.get(`/api/products/page?page=${page}&size=${size}`);
    return response.data;
};