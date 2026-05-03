import api from "./api";

// urunleri sayfali sekilde getiriyoruz
export const getProducts = async (page = 0, size = 8) => {
    const response = await api.get(`/api/products/page?page=${page}&size=${size}`);
    return response.data;
};